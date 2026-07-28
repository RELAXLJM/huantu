package com.huantu.config;

import com.huantu.entity.User;
import com.huantu.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 自动创建管理员账号（启动时执行）
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(String... args) {
        User exist = userMapper.findByPhone("admin");
        if (exist == null) {
            User admin = new User();
            admin.setPhone("admin");
            admin.setNickname("管理员");
            admin.setPasswordHash(new BCryptPasswordEncoder().encode("123456"));
            admin.setStatus(1);
            userMapper.insert(admin);
            log.info("管理员账号已创建: admin / 123456");
        } else {
            log.info("管理员账号已存在");
        }
    }
}
