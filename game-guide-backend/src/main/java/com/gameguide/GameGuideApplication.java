package com.gameguide;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gameguide.mapper")
public class GameGuideApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameGuideApplication.class, args);
    }

}

