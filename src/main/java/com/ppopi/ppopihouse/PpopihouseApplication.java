package com.ppopi.ppopihouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PpopihouseApplication {

    public static void main(String[] args) {
        SpringApplication.run(PpopihouseApplication.class, args);
    }

}
