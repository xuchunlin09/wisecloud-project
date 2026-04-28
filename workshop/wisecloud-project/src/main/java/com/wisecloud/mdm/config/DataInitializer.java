package com.wisecloud.mdm.config;

import com.wisecloud.mdm.entity.User;
import com.wisecloud.mdm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDefaultAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@wisecloud.com");
                admin.setPasswordHash(passwordEncoder.encode("admin"));
                userRepository.save(admin);
                log.info("默认管理员账号已创建: admin / admin");
            }
        };
    }
}
