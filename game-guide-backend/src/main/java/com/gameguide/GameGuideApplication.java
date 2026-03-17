package com.gameguide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.gameguide.mapper")
@EnableAsync
@EnableScheduling
public class GameGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameGuideApplication.class, args);
    }

}

