package com.huantu.controller;

import com.huantu.common.Result;
import com.huantu.entity.Scenic;
import com.huantu.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通用接口：文件上传、短信、管理工具
 */
@RestController
@RequestMapping("/api/common")
public class CommonController {

    @Autowired
    private AiService aiService;

    /**
     * 手动同步高德景点数据（管理用）
     * GET /api/common/sync-scenic?city=北京
     */
    @GetMapping("/sync-scenic")
    public Result<Integer> syncScenic(@RequestParam String city) {
        List<Scenic> scenics = aiService.fetchFromAmap(city);
        return Result.success("同步完成，拉取" + scenics.size() + "个景点", scenics.size());
    }
}
