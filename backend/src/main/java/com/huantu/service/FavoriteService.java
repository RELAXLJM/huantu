package com.huantu.service;

import com.huantu.common.ResultCode;
import com.huantu.common.exception.BusinessException;
import com.huantu.dto.response.PostVO;
import com.huantu.dto.response.RouteVO;
import com.huantu.dto.response.ScenicVO;
import com.huantu.entity.Favorite;
import com.huantu.entity.Scenic;
import com.huantu.mapper.FavoriteMapper;
import com.huantu.mapper.ScenicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏业务逻辑
 */
@Service
public class FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private RouteService routeService;

    @Autowired
    private ScenicMapper scenicMapper;

    /**
     * 添加收藏
     */
    public void add(Long userId, Long targetId, Integer targetType) {
        Favorite exist = favoriteMapper.findByUserAndTarget(userId, targetId, targetType);
        if (exist != null) {
            throw new BusinessException(ResultCode.FAVORITE_ALREADY_EXISTS);
        }

        Favorite fav = new Favorite();
        fav.setUserId(userId);
        fav.setTargetId(targetId);
        fav.setTargetType(targetType);
        favoriteMapper.insert(fav);
    }

    /**
     * 取消收藏
     */
    public void remove(Long userId, Long targetId, Integer targetType) {
        Favorite exist = favoriteMapper.findByUserAndTarget(userId, targetId, targetType);
        if (exist == null) {
            throw new BusinessException(ResultCode.FAVORITE_NOT_FOUND);
        }
        favoriteMapper.deleteById(exist.getId());
    }

    /**
     * 收藏列表
     */
    public List<Map<String, Object>> getList(Long userId, Integer targetType) {
        List<Favorite> favorites = favoriteMapper.findByUser(userId, targetType);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Favorite fav : favorites) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", fav.getId());
            item.put("targetId", fav.getTargetId());
            item.put("targetType", fav.getTargetType());
            item.put("createdAt", fav.getCreatedAt() != null ? fav.getCreatedAt().toString() : null);

            // 根据类型加载目标详情
            try {
                switch (fav.getTargetType()) {
                    case 1 -> // 路线
                        item.put("route", routeService.getRouteDetail(fav.getTargetId()));
                    case 3 -> { // 景点
                        Scenic scenic = scenicMapper.findById(fav.getTargetId());
                        if (scenic != null) {
                            Map<String, Object> s = new HashMap<>();
                            s.put("id", scenic.getId());
                            s.put("name", scenic.getName());
                            s.put("address", scenic.getAddress());
                            s.put("rating", scenic.getRating());
                            s.put("images", scenic.getImages());
                            item.put("scenic", s);
                        }
                    }
                }
            } catch (Exception e) {
                item.put("error", "目标数据不可用");
            }

            result.add(item);
        }
        return result;
    }
}
