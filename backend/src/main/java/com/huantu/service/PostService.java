package com.huantu.service;

import com.huantu.common.ResultCode;
import com.huantu.common.exception.BusinessException;
import com.huantu.dto.request.PostPublishRequest;
import com.huantu.dto.response.PostVO;
import com.huantu.entity.*;
import com.huantu.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 社区帖子业务逻辑
 */
@Service
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired private PostMapper postMapper;
    @Autowired private PostInteractMapper postInteractMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private ScenicMapper scenicMapper;
    @Autowired private RouteScenicMapper routeScenicMapper;

    /**
     * 发布帖子
     */
    public PostVO publish(Long userId, PostPublishRequest req) {
        Post post = new Post();
        post.setAuthorId(userId);
        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setImages(req.getImages());
        post.setCity(req.getCity());
        post.setCityCode(req.getCityCode());
        post.setLocationTag(req.getLocationTag());
        post.setIsLocalAuth(req.getIsLocalAuth() != null ? req.getIsLocalAuth() : 0);
        post.setScenicId(req.getScenicId());
        post.setStatus(0);
        postMapper.insert(post);

        log.info("新帖子: postId={}, userId={}", post.getId(), userId);
        return toVO(post, userId, null);
    }

    /**
     * 帖子列表（按城市筛选）
     */
    public List<PostVO> getList(String cityCode, int page, int pageSize, Long currentUserId) {
        int offset = (page - 1) * pageSize;
        List<Post> posts = postMapper.findList(cityCode, offset, pageSize);

        if (posts.isEmpty()) return List.of();

        // 批量查询用户互动状态
        List<Long> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Set<Long> likedPostIds = Set.of();
        Set<Long> collectedPostIds = Set.of();

        if (currentUserId != null) {
            List<PostInteract> likes = postInteractMapper.findByUserAndPosts(currentUserId, postIds, 1);
            likedPostIds = likes.stream().map(PostInteract::getPostId).collect(Collectors.toSet());

            List<PostInteract> collects = postInteractMapper.findByUserAndPosts(currentUserId, postIds, 2);
            collectedPostIds = collects.stream().map(PostInteract::getPostId).collect(Collectors.toSet());
        }

        Set<Long> finalLiked = likedPostIds;
        Set<Long> finalCollected = collectedPostIds;
        return posts.stream()
                .map(p -> {
                    PostVO vo = toVO(p, currentUserId, null);
                    vo.setLiked(finalLiked.contains(p.getId()));
                    vo.setCollected(finalCollected.contains(p.getId()));
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 帖子详情
     */
    public PostVO getDetail(Long postId, Long currentUserId) {
        Post post = postMapper.findById(postId);
        if (post == null) throw new BusinessException(ResultCode.POST_NOT_FOUND);

        PostVO vo = toVO(post, currentUserId, null);

        if (currentUserId != null) {
            PostInteract liked = postInteractMapper.findByUserAndPost(currentUserId, postId, 1);
            PostInteract collected = postInteractMapper.findByUserAndPost(currentUserId, postId, 2);
            vo.setLiked(liked != null);
            vo.setCollected(collected != null);
        }

        return vo;
    }

    /**
     * 互动（点赞/收藏/有用）—— 切换模式：点了取消，没点点上
     */
    @Transactional
    public Map<String, Object> interact(Long userId, Long postId, Integer type) {
        Post post = postMapper.findById(postId);
        if (post == null) throw new BusinessException(ResultCode.POST_NOT_FOUND);

        PostInteract exist = postInteractMapper.findByUserAndPost(userId, postId, type);
        boolean isActive;

        if (exist != null) {
            // 取消互动
            postInteractMapper.deleteById(exist.getId());
            updatePostCount(postId, type, -1);
            isActive = false;
        } else {
            // 添加互动
            PostInteract interact = new PostInteract();
            interact.setPostId(postId);
            interact.setUserId(userId);
            interact.setType(type);
            postInteractMapper.insert(interact);
            updatePostCount(postId, type, 1);
            isActive = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isActive", isActive);

        // 返回最新计数
        Post updated = postMapper.findById(postId);
        result.put("likeCount", updated.getLikeCount());
        result.put("collectCount", updated.getCollectCount());
        result.put("usefulCount", updated.getUsefulCount());
        return result;
    }

    /**
     * 将帖子地点加入行程
     */
    @Transactional
    public void addToRoute(Long userId, Long postId, Long routeId) {
        Post post = postMapper.findById(postId);
        if (post == null) throw new BusinessException(ResultCode.POST_NOT_FOUND);

        // 查找路线最后一天的最后一个景点的排序
        List<RouteScenic> existing = routeScenicMapper.findByRouteId(routeId);
        int maxDay = 1;
        int maxSort = 0;
        for (RouteScenic rs : existing) {
            if (rs.getDayNumber() > maxDay) maxDay = rs.getDayNumber();
            if (rs.getDayNumber() == maxDay && rs.getSortOrder() > maxSort) maxSort = rs.getSortOrder();
        }

        RouteScenic rs = new RouteScenic();
        rs.setRouteId(routeId);
        rs.setScenicId(post.getScenicId() != null ? post.getScenicId() : 0L);
        rs.setDayNumber(maxDay);
        rs.setSortOrder(maxSort + 1);
        rs.setStayDuration(60);
        rs.setMemo("来自帖子: " + (post.getTitle() != null ? post.getTitle() : post.getContent().substring(0, Math.min(20, post.getContent().length()))));
        routeScenicMapper.insert(rs);

        log.info("帖子地点加入行程: postId={}, routeId={}, userId={}", postId, routeId, userId);
    }

    // ==================== 内部方法 ====================

    private String getCountField(int type) {
        return switch (type) {
            case 1 -> "like_count";
            case 2 -> "collect_count";
            case 3 -> "useful_count";
            default -> throw new IllegalArgumentException("未知互动类型: " + type);
        };
    }

    private void updatePostCount(Long postId, int type, int delta) {
        postMapper.updateCount(postId, getCountField(type), delta);
    }

    private PostVO toVO(Post post, Long currentUserId, Map<String, Object> extra) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setAuthorId(post.getAuthorId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImages(post.getImages());
        vo.setVideoUrl(post.getVideoUrl());
        vo.setCity(post.getCity());
        vo.setCityCode(post.getCityCode());
        vo.setLocationTag(post.getLocationTag());
        vo.setIsLocalAuth(post.getIsLocalAuth());
        vo.setScenicId(post.getScenicId());
        vo.setStatus(post.getStatus());
        vo.setLikeCount(post.getLikeCount());
        vo.setCollectCount(post.getCollectCount());
        vo.setUsefulCount(post.getUsefulCount());
        if (post.getCreatedAt() != null) vo.setCreatedAt(post.getCreatedAt().format(DTF));

        // 作者信息
        User author = userMapper.findById(post.getAuthorId());
        if (author != null) {
            vo.setAuthorNickname(author.getNickname());
            vo.setAuthorAvatar(author.getAvatarUrl());
        }

        // 关联景点名
        if (post.getScenicId() != null && post.getScenicId() > 0) {
            Scenic scenic = scenicMapper.findById(post.getScenicId());
            if (scenic != null) vo.setScenicName(scenic.getName());
        }

        return vo;
    }
}
