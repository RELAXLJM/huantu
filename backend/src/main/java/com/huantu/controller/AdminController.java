package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.entity.Scenic;
import com.huantu.mapper.PostMapper;
import com.huantu.mapper.RouteMapper;
import com.huantu.mapper.ScenicMapper;
import com.huantu.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RouteMapper routeMapper;

    /**
     * 数据看板
     */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("userCount", userMapper.countAll());
        data.put("postCount", postMapper.countAll());
        data.put("scenicCount", scenicMapper.countAll());
        data.put("routeCount", routeMapper.countAll());
        return Result.success(data);
    }

    /**
     * 删除帖子
     */
    @DeleteMapping("/post/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        postMapper.deleteById(id);
        return Result.success("帖子已删除", null);
    }

    /**
     * 添加景点
     */
    @PostMapping("/scenic")
    public Result<Void> addScenic(@RequestBody Scenic scenic) {
        scenicMapper.insert(scenic);
        return Result.success("景点已添加", null);
    }

    /**
     * 删除景点
     */
    @DeleteMapping("/scenic/{id}")
    public Result<Void> deleteScenic(@PathVariable Long id) {
        scenicMapper.deleteById(id);
        return Result.success("景点已删除", null);
    }

    /**
     * 景点列表（管理用）
     */
    @GetMapping("/scenic/list")
    public Result<java.util.List<Scenic>> scenicList(
            @RequestParam(required = false) String keyword) {
        java.util.List<Scenic> list = scenicMapper.search(null, null, keyword);
        return Result.success(list);
    }
}
