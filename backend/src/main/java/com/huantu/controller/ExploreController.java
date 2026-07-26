package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.dto.response.ScenicVO;
import com.huantu.service.AmapService.WeatherInfo;
import com.huantu.service.ExploreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 主页/探索控制器
 */
@RestController
@RequestMapping("/api/explore")
public class ExploreController {

    @Autowired
    private ExploreService exploreService;

    /**
     * 周边热门景点
     */
    @GetMapping("/nearby")
    public Result<List<ScenicVO>> nearby(@RequestParam String city,
                                          @RequestParam(defaultValue = "10") Integer limit) {
        List<ScenicVO> list = exploreService.getNearby(city, limit);
        return Result.success(list);
    }

    /**
     * 天气查询
     */
    @GetMapping("/weather")
    public Result<WeatherInfo> weather(@RequestParam String cityCode) {
        WeatherInfo info = exploreService.getWeather(cityCode);
        if (info == null) {
            return Result.success("天气数据暂不可用", null);
        }
        return Result.success(info);
    }

    /**
     * 城市必玩榜单
     */
    @GetMapping("/rankings")
    public Result<List<ScenicVO>> rankings(@RequestParam String city,
                                            @RequestParam(required = false) String tag,
                                            @RequestParam(defaultValue = "5") Integer limit) {
        List<ScenicVO> list = exploreService.getRankings(city, tag, limit);
        return Result.success(list);
    }

    /**
     * 搜索景点
     */
    @GetMapping("/search")
    public Result<List<ScenicVO>> search(@RequestParam(required = false) String cityCode,
                                          @RequestParam String keyword,
                                          @RequestParam(required = false) String poiType) {
        List<ScenicVO> list = exploreService.search(cityCode, keyword, poiType);
        return Result.success(list);
    }
}
