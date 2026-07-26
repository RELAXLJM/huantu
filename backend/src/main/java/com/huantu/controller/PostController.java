package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.dto.request.PostPublishRequest;
import com.huantu.dto.response.PostVO;
import com.huantu.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 社区帖子控制器
 */
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 发布帖子
     */
    @PostMapping("/publish")
    public Result<PostVO> publish(HttpServletRequest request,
                                   @Valid @RequestBody PostPublishRequest req) {
        Long userId = getUserId(request);
        PostVO vo = postService.publish(userId, req);
        return Result.success("发布成功", vo);
    }

    /**
     * 帖子列表
     */
    @GetMapping("/list")
    public Result<List<PostVO>> list(HttpServletRequest request,
                                      @RequestParam(required = false) String cityCode,
                                      @RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = tryGetUserId(request);
        List<PostVO> list = postService.getList(cityCode, page, pageSize, userId);
        return Result.success(list);
    }

    /**
     * 帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostVO> detail(HttpServletRequest request, @PathVariable Long id) {
        Long userId = tryGetUserId(request);
        PostVO vo = postService.getDetail(id, userId);
        return Result.success(vo);
    }

    /**
     * 互动（点赞/收藏/有用）
     */
    @PostMapping("/{id}/interact")
    public Result<Map<String, Object>> interact(HttpServletRequest request,
                                                 @PathVariable Long id,
                                                 @RequestParam Integer type) {
        Long userId = getUserId(request);
        Map<String, Object> result = postService.interact(userId, id, type);
        return Result.success(result);
    }

    /**
     * 将帖子中的地点加入行程
     */
    @PostMapping("/{id}/add-to-route")
    public Result<Void> addToRoute(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @RequestParam Long routeId) {
        Long userId = getUserId(request);
        postService.addToRoute(userId, id, routeId);
        return Result.success("已加入行程", null);
    }

    // ==================== 工具 ====================

    private Long getUserId(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid instanceof Integer) return ((Integer) uid).longValue();
        return (Long) uid;
    }

    /** 尝试获取用户ID（未登录返回null） */
    private Long tryGetUserId(HttpServletRequest request) {
        try {
            return getUserId(request);
        } catch (Exception e) {
            return null;
        }
    }
}
