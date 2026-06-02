package com.tradelia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.tradelia.config.CorsProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties.class)
public class TradeliaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeliaApplication.class, args);
    }
}
