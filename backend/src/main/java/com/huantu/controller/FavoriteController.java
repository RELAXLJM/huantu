package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.service.FavoriteService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/add")
    public Result<Void> add(HttpServletRequest request,
                             @RequestParam Long targetId,
                             @RequestParam Integer targetType) {
        Long userId = getUserId(request);
        favoriteService.add(userId, targetId, targetType);
        return Result.success("收藏成功", null);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/remove")
    public Result<Void> remove(HttpServletRequest request,
                                @RequestParam Long targetId,
                                @RequestParam Integer targetType) {
        Long userId = getUserId(request);
        favoriteService.remove(userId, targetId, targetType);
        return Result.success("已取消收藏", null);
    }

    /**
     * 收藏列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request,
                                                    @RequestParam(required = false) Integer targetType) {
        Long userId = getUserId(request);
        List<Map<String, Object>> list = favoriteService.getList(userId, targetType);
        return Result.success(list);
    }

    private Long getUserId(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid instanceof Integer) return ((Integer) uid).longValue();
        return (Long) uid;
    }
}
