package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.dto.request.RouteGenerateRequest;
import com.huantu.dto.response.RouteVO;
import com.huantu.service.RouteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 路线控制器
 */
@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteService routeService;

    /**
     * AI 生成路线
     */
    @PostMapping("/generate")
    public Result<RouteVO> generate(HttpServletRequest request,
                                     @Valid @RequestBody RouteGenerateRequest req) {
        Long userId = getUserId(request);
        RouteVO routeVO = routeService.generateRoute(userId, req);
        return Result.success("路线生成成功", routeVO);
    }

    /**
     * 路线列表（可按状态筛选）
     */
    @GetMapping("/list")
    public Result<List<RouteVO>> list(HttpServletRequest request,
                                       @RequestParam(required = false) Integer status) {
        Long userId = getUserId(request);
        List<RouteVO> routes = routeService.getMyRoutes(userId, status);
        return Result.success(routes);
    }

    /**
     * 路线详情
     */
    @GetMapping("/{id}")
    public Result<RouteVO> detail(@PathVariable Long id) {
        RouteVO routeVO = routeService.getRouteDetail(id);
        return Result.success(routeVO);
    }

    /**
     * 修改路线（保存/结束/改名）
     */
    @PutMapping("/{id}")
    public Result<RouteVO> update(HttpServletRequest request,
                                   @PathVariable Long id,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String title) {
        Long userId = getUserId(request);
        RouteVO routeVO = routeService.updateRoute(userId, id, status, title);
        return Result.success("修改成功", routeVO);
    }

    /**
     * 删除路线
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long userId = getUserId(request);
        routeService.deleteRoute(userId, id);
        return Result.success("删除成功", null);
    }

    /**
     * 调整景点顺序
     */
    @PutMapping("/{id}/reorder")
    public Result<RouteVO> reorder(HttpServletRequest request,
                                    @PathVariable Long id,
                                    @RequestBody List<RouteService.ReorderItem> items) {
        Long userId = getUserId(request);
        RouteVO routeVO = routeService.reorderScenics(userId, id, items);
        return Result.success("排序已更新", routeVO);
    }

    private Long getUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("userId");
        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }
        return (Long) userId;
    }
}
