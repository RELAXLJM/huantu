package com.huantu.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 配置：Mapper 扫描
 */
@Configuration
@MapperScan("com.huantu.mapper")
public class MyBatisConfig {
}
