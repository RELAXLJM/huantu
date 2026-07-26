package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.service.FootprintService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 足迹控制器
 */
@RestController
@RequestMapping("/api/footprint")
public class FootprintController {

    @Autowired
    private FootprintService footprintService;

    /**
     * 打卡
     */
    @PostMapping("/checkin")
    public Result<Void> checkin(HttpServletRequest request, @RequestParam Long scenicId) {
        Long userId = getUserId(request);
        footprintService.addFootprint(userId, scenicId);
        return Result.success("打卡成功", null);
    }

    /**
     * 足迹列表
     */
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> list(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(footprintService.getFootprints(userId));
    }

    /**
     * 足迹地图（去过的城市）
     */
    @GetMapping("/map")
    public Result<List<Map<String, Object>>> map(HttpServletRequest request) {
        Long userId = getUserId(request);
        return Result.success(footprintService.getFootprintMap(userId));
    }

    private Long getUserId(HttpServletRequest request) {
        Object uid = request.getAttribute("userId");
        if (uid instanceof Integer) return ((Integer) uid).longValue();
        return (Long) uid;
    }
}
