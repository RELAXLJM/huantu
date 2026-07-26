package com.huantu.service;

import com.huantu.dto.response.RouteVO;
import com.huantu.entity.Scenic;
import com.huantu.mapper.ScenicMapper;
import com.huantu.service.AmapService.PoiInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 路线生成服务
 *
 * 数据策略：优先本地数据库 → 不够则调高德API拉取并自动存入 → 再不够就返回已有数据
 */
@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private AmapService amapService;

    /** 最少需要的景点数（天数 × 此值） */
    private static final int MIN_SCENICS_PER_DAY = 2;

    /**
     * 生成旅行路线
     */
    public List<RouteVO.DayPlan> generateRoute(String destination, int days,
                                                String companionType, String preference) {
        int minRequired = days * MIN_SCENICS_PER_DAY;

        // 1. 本地数据库搜索
        List<Scenic> scenics = searchLocal(destination);

        // 2. 不够 → 调高德API拉取
        if (scenics.size() < minRequired) {
            log.info("本地{}景点不足({}条)，从高德API拉取...", destination, scenics.size());
            List<Scenic> amapScenics = fetchFromAmap(destination);
            scenics = mergeAndDedup(scenics, amapScenics, destination);
        }

        if (scenics.isEmpty()) {
            log.warn("未找到{}的景点数据", destination);
            return List.of();
        }

        // 3. 按偏好筛选 + 评分排序
        scenics = filterAndSort(scenics, preference, days);

        // 4. 按天分配
        return allocateToDays(scenics, days);
    }

    /**
     * 从高德API拉取景点并自动存入数据库
     */
    public List<Scenic> fetchFromAmap(String city) {
        List<Scenic> allScenics = new ArrayList<>();

        // 搜索多种类型的POI
        String[] searchConfigs = {
            "景点", "风景名胜|公园广场|博物馆|展览馆",
            "美食", "中餐厅|小吃快餐店|火锅|西餐厅",
        };

        for (int i = 0; i < searchConfigs.length; i += 2) {
            String keyword = searchConfigs[i];
            String types = searchConfigs[i + 1];

            List<PoiInfo> pois = amapService.searchPoi(city, keyword, types);
            for (PoiInfo poi : pois) {
                Scenic scenic = new Scenic();
                scenic.setName(poi.getName());
                scenic.setPoiType(mapPoiType(poi.getPoiType()));
                scenic.setLongitude(poi.getLongitude());
                scenic.setLatitude(poi.getLatitude());
                scenic.setAddress(poi.getAddress());
                scenic.setCity(poi.getCityName());
                scenic.setCityCode(poi.getCityCode());
                scenic.setRating(poi.getRating());
                scenic.setImages(poi.getImages());
                allScenics.add(scenic);
            }
        }

        // 批量存入数据库（忽略重复）
        if (!allScenics.isEmpty()) {
            try {
                scenicMapper.insertBatch(allScenics);
                log.info("从高德API拉取并保存了{}个景点到 {}", allScenics.size(), city);
            } catch (Exception e) {
                log.error("保存高德景点失败（可能重复）: {}", e.getMessage());
            }
        }

        return allScenics;
    }

    // ==================== 内部方法 ====================

    /** 本地搜索 */
    private List<Scenic> searchLocal(String destination) {
        List<Scenic> scenics = scenicMapper.search(null, null, destination);
        if (scenics.isEmpty()) {
            scenics = scenicMapper.search(null, null, null);
            scenics = scenics.stream()
                    .filter(s -> s.getCity() != null && s.getCity().contains(destination))
                    .collect(Collectors.toList());
        }
        return scenics;
    }

    /** 合并并去重 */
    private List<Scenic> mergeAndDedup(List<Scenic> local, List<Scenic> amap, String city) {
        Set<String> seen = new HashSet<>();
        List<Scenic> result = new ArrayList<>();

        for (Scenic s : local) {
            if (seen.add(s.getName() + "@" + s.getCity())) {
                result.add(s);
            }
        }
        for (Scenic s : amap) {
            if (seen.add(s.getName() + "@" + s.getCity())) {
                result.add(s);
            }
        }
        return result;
    }

    /** 按偏好筛选并按评分排序 */
    private List<Scenic> filterAndSort(List<Scenic> scenics, String preference, int days) {
        List<Scenic> all = new ArrayList<>(scenics);

        if (preference != null && !preference.isEmpty()) {
            List<Scenic> filtered = scenics.stream()
                    .filter(s -> matchPreference(s.getTag(), s.getPoiType(), preference))
                    .collect(Collectors.toList());

            if (filtered.size() >= days) {
                all = filtered;
            } else if (!filtered.isEmpty()) {
                // 筛选结果不够 → 筛选的排前面，其余补后面
                Set<Long> filteredIds = filtered.stream()
                        .map(Scenic::getId).filter(id -> id != null)
                        .collect(Collectors.toSet());
                List<Scenic> rest = scenics.stream()
                        .filter(s -> s.getId() == null || !filteredIds.contains(s.getId()))
                        .collect(Collectors.toList());
                filtered.addAll(rest);
                all = filtered;
            }
        }

        all.sort((a, b) -> {
            Double ra = a.getRating() != null ? a.getRating() : 0;
            Double rb = b.getRating() != null ? b.getRating() : 0;
            return rb.compareTo(ra);
        });

        return all;
    }

    /** 按天分配景点 */
    private List<RouteVO.DayPlan> allocateToDays(List<Scenic> scenics, int days) {
        int maxPerDay = Math.min(4, Math.max(2, scenics.size() / days + 1));
        List<RouteVO.DayPlan> dayPlans = new ArrayList<>();

        int poiIndex = 0;
        for (int day = 1; day <= days; day++) {
            RouteVO.DayPlan dayPlan = new RouteVO.DayPlan();
            dayPlan.setDayNumber(day);
            List<RouteVO.ScenicItem> items = new ArrayList<>();

            int count = Math.min(maxPerDay, scenics.size() - poiIndex);
            for (int i = 0; i < count && poiIndex < scenics.size(); i++) {
                Scenic scenic = scenics.get(poiIndex);
                RouteVO.ScenicItem item = scenicToItem(scenic);
                item.setStayDuration(estimateStayTime(scenic.getPoiType()));
                if (i > 0) {
                    item.setTransportTip("建议步行或打车前往");
                }
                items.add(item);
                poiIndex++;
            }
            dayPlan.setScenics(items);
            dayPlans.add(dayPlan);
        }
        return dayPlans;
    }

    private boolean matchPreference(String tag, String poiType, String preference) {
        if (preference == null) return true;
        if (preference.contains("美食") || preference.contains("吃货")) {
            return "food".equals(poiType) || (tag != null && tag.contains("美食"));
        }
        if (preference.contains("拍照") || preference.contains("打卡")) {
            return "scenic".equals(poiType) || (tag != null && (tag.contains("夜景") || tag.contains("地标")));
        }
        if (preference.contains("亲子") || preference.contains("遛娃")) {
            return tag != null && (tag.contains("亲子") || tag.contains("动物园"));
        }
        if (preference.contains("情侣") || preference.contains("浪漫")) {
            return tag != null && (tag.contains("浪漫") || tag.contains("夜景"));
        }
        return true;
    }

    /** 高德POI类型 → 内部类型 */
    private String mapPoiType(String amapType) {
        if (amapType == null) return "scenic";
        if (amapType.contains("餐饮") || amapType.contains("餐厅") || amapType.contains("小吃")
                || amapType.contains("火锅") || amapType.contains("咖啡")) {
            return "food";
        }
        if (amapType.contains("购物") || amapType.contains("商业") || amapType.contains("市场")) {
            return "shopping";
        }
        if (amapType.contains("酒店") || amapType.contains("住宿") || amapType.contains("宾馆")) {
            return "hotel";
        }
        return "scenic";
    }

    private int estimateStayTime(String poiType) {
        if (poiType == null) return 60;
        if ("food".equals(poiType)) return 60;
        if ("shopping".equals(poiType)) return 90;
        return 120;
    }

    private RouteVO.ScenicItem scenicToItem(Scenic scenic) {
        RouteVO.ScenicItem item = new RouteVO.ScenicItem();
        item.setScenicId(scenic.getId());
        item.setName(scenic.getName());
        item.setPoiType(scenic.getPoiType());
        item.setAddress(scenic.getAddress());
        item.setLongitude(scenic.getLongitude());
        item.setLatitude(scenic.getLatitude());
        item.setRating(scenic.getRating());
        item.setImages(scenic.getImages());
        item.setPriceInfo(scenic.getPriceInfo());
        return item;
    }
}
