package com.huantu.service;

import com.huantu.common.ResultCode;
import com.huantu.common.exception.BusinessException;
import com.huantu.dto.request.RouteGenerateRequest;
import com.huantu.dto.response.RouteVO;
import com.huantu.entity.Route;
import com.huantu.entity.RouteScenic;
import com.huantu.entity.Scenic;
import com.huantu.mapper.RouteMapper;
import com.huantu.mapper.RouteScenicMapper;
import com.huantu.mapper.ScenicMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 路线业务逻辑
 */
@Service
public class RouteService {

    private static final Logger log = LoggerFactory.getLogger(RouteService.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private RouteScenicMapper routeScenicMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private AiService aiService;

    /**
     * AI 生成路线
     */
    @Transactional
    public RouteVO generateRoute(Long userId, RouteGenerateRequest req) {
        // 1. 调用AI生成路线计划
        List<RouteVO.DayPlan> dayPlans = aiService.generateRoute(
                req.getDestination(), req.getDays(), req.getCompanionType(), req.getPreference());

        if (dayPlans.isEmpty() || dayPlans.stream().allMatch(d -> d.getScenics().isEmpty())) {
            throw new BusinessException(ResultCode.AI_GENERATE_FAILED,
                    "未能找到" + req.getDestination() + "的相关景点，请换个城市试试");
        }

        // 2. 计算总时长
        int totalDuration = 0;
        for (RouteVO.DayPlan dp : dayPlans) {
            for (RouteVO.ScenicItem item : dp.getScenics()) {
                totalDuration += item.getStayDuration() != null ? item.getStayDuration() : 0;
            }
        }

        // 3. 保存路线
        Route route = new Route();
        route.setUserId(userId);
        route.setTitle(req.getDestination() + req.getDays() + "日游");
        route.setDestination(req.getDestination());
        route.setDays(req.getDays());
        route.setTotalDuration(totalDuration);
        route.setCompanionType(req.getCompanionType());
        route.setBudgetMin(req.getBudgetMin());
        route.setBudgetMax(req.getBudgetMax());
        route.setPreference(req.getPreference());
        route.setStatus(0); // 草稿
        routeMapper.insert(route);

        // 4. 保存路线-景点关联
        List<RouteScenic> routeScenics = new ArrayList<>();
        for (RouteVO.DayPlan dp : dayPlans) {
            int sort = 0;
            for (RouteVO.ScenicItem item : dp.getScenics()) {
                RouteScenic rs = new RouteScenic();
                rs.setRouteId(route.getId());
                rs.setDayNumber(dp.getDayNumber());
                rs.setSortOrder(sort++);
                rs.setStayDuration(item.getStayDuration());
                rs.setTransportTip(item.getTransportTip());
                rs.setMemo(item.getMemo());
                rs.setScenicId(item.getScenicId() != null ? item.getScenicId() : 0L);
                routeScenics.add(rs);
            }
        }

        if (!routeScenics.isEmpty()) {
            routeScenicMapper.batchInsert(routeScenics);
        }

        log.info("路线生成成功: routeId={}, userId={}, destination={}, days={}",
                route.getId(), userId, req.getDestination(), req.getDays());

        return buildRouteVO(route, dayPlans);
    }

    /**
     * 获取路线详情（含时间轴）
     */
    public RouteVO getRouteDetail(Long routeId) {
        Route route = routeMapper.findById(routeId);
        if (route == null) {
            throw new BusinessException(ResultCode.ROUTE_NOT_FOUND);
        }

        // 查询关联景点
        List<RouteScenic> routeScenics = routeScenicMapper.findByRouteId(routeId);
        List<RouteVO.DayPlan> dayPlans = buildDayPlans(routeScenics);

        return buildRouteVO(route, dayPlans);
    }

    /**
     * 我的路线列表
     */
    public List<RouteVO> getMyRoutes(Long userId, Integer status) {
        List<Route> routes = routeMapper.findByUserId(userId, status);
        return routes.stream().map(route -> {
            List<RouteScenic> routeScenics = routeScenicMapper.findByRouteId(route.getId());
            List<RouteVO.DayPlan> dayPlans = buildDayPlans(routeScenics);
            return buildRouteVO(route, dayPlans);
        }).collect(Collectors.toList());
    }

    /**
     * 修改路线
     */
    public RouteVO updateRoute(Long userId, Long routeId, Integer status, String title) {
        Route route = routeMapper.findById(routeId);
        if (route == null) {
            throw new BusinessException(ResultCode.ROUTE_NOT_FOUND);
        }
        if (!route.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ROUTE_PERMISSION_DENIED);
        }

        Route update = new Route();
        update.setId(routeId);
        update.setStatus(status);
        update.setTitle(title);
        routeMapper.updateById(update);

        return getRouteDetail(routeId);
    }

    /**
     * 删除路线
     */
    @Transactional
    public void deleteRoute(Long userId, Long routeId) {
        Route route = routeMapper.findById(routeId);
        if (route == null) {
            throw new BusinessException(ResultCode.ROUTE_NOT_FOUND);
        }
        if (!route.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ROUTE_PERMISSION_DENIED);
        }

        routeScenicMapper.deleteByRouteId(routeId);
        routeMapper.deleteById(routeId);
        log.info("路线已删除: routeId={}", routeId);
    }

    /**
     * 重新排序景点
     */
    @Transactional
    public RouteVO reorderScenics(Long userId, Long routeId, List<ReorderItem> items) {
        Route route = routeMapper.findById(routeId);
        if (route == null) {
            throw new BusinessException(ResultCode.ROUTE_NOT_FOUND);
        }
        if (!route.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.ROUTE_PERMISSION_DENIED);
        }

        for (ReorderItem item : items) {
            RouteScenic rs = new RouteScenic();
            rs.setId(item.getId());
            rs.setDayNumber(item.getDayNumber());
            rs.setSortOrder(item.getSortOrder());
            routeScenicMapper.updateById(rs);
        }

        return getRouteDetail(routeId);
    }

    // ==================== 工具方法 ====================

    /**
     * 构建 RouteVO
     */
    private RouteVO buildRouteVO(Route route, List<RouteVO.DayPlan> dayPlans) {
        RouteVO vo = new RouteVO();
        vo.setId(route.getId());
        vo.setTitle(route.getTitle());
        vo.setCoverUrl(route.getCoverUrl());
        vo.setDestination(route.getDestination());
        vo.setCityCode(route.getCityCode());
        vo.setDays(route.getDays());
        vo.setTotalDuration(route.getTotalDuration());
        vo.setCompanionType(route.getCompanionType());
        vo.setBudgetMin(route.getBudgetMin());
        vo.setBudgetMax(route.getBudgetMax());
        vo.setPreference(route.getPreference());
        vo.setStatus(route.getStatus());
        if (route.getCreatedAt() != null) {
            vo.setCreatedAt(route.getCreatedAt().format(DTF));
        }
        vo.setDayPlans(dayPlans);
        return vo;
    }

    /**
     * 根据关联数据构建每天的行程计划
     */
    private List<RouteVO.DayPlan> buildDayPlans(List<RouteScenic> routeScenics) {
        if (routeScenics.isEmpty()) {
            return List.of();
        }

        // 查询所有景点信息
        List<Long> scenicIds = routeScenics.stream()
                .map(RouteScenic::getScenicId)
                .filter(id -> id > 0)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Scenic> scenicMap = Map.of();
        if (!scenicIds.isEmpty()) {
            List<Scenic> scenics = scenicMapper.findByIds(scenicIds);
            scenicMap = scenics.stream().collect(Collectors.toMap(Scenic::getId, s -> s));
        }

        // 按天数分组
        Map<Integer, List<RouteScenic>> grouped = routeScenics.stream()
                .collect(Collectors.groupingBy(RouteScenic::getDayNumber));

        List<RouteVO.DayPlan> dayPlans = new ArrayList<>();
        for (Map.Entry<Integer, List<RouteScenic>> entry : grouped.entrySet()) {
            RouteVO.DayPlan dp = new RouteVO.DayPlan();
            dp.setDayNumber(entry.getKey());
            List<RouteVO.ScenicItem> items = new ArrayList<>();

            for (RouteScenic rs : entry.getValue()) {
                RouteVO.ScenicItem item = new RouteVO.ScenicItem();
                item.setStayDuration(rs.getStayDuration());
                item.setTransportTip(rs.getTransportTip());
                item.setMemo(rs.getMemo());

                Scenic scenic = scenicMap.get(rs.getScenicId());
                if (scenic != null) {
                    item.setScenicId(scenic.getId());
                    item.setName(scenic.getName());
                    item.setPoiType(scenic.getPoiType());
                    item.setAddress(scenic.getAddress());
                    item.setLongitude(scenic.getLongitude());
                    item.setLatitude(scenic.getLatitude());
                    item.setRating(scenic.getRating());
                    item.setImages(scenic.getImages());
                    item.setPriceInfo(scenic.getPriceInfo());
                }
                items.add(item);
            }
            dp.setScenics(items);
            dayPlans.add(dp);
        }

        dayPlans.sort((a, b) -> a.getDayNumber().compareTo(b.getDayNumber()));
        return dayPlans;
    }

    /**
     * 排序请求项
     */
    public static class ReorderItem {
        private Long id;
        private Integer dayNumber;
        private Integer sortOrder;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Integer getDayNumber() { return dayNumber; }
        public void setDayNumber(Integer dayNumber) { this.dayNumber = dayNumber; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }
}
