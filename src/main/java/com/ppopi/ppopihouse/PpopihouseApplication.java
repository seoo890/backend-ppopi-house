package com.ppopi.ppopihouse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType; // 추가
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PpopihouseApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PpopihouseApplication.class);

        // [핵심] 웹 애플리케이션 타입을 서블릿(Tomcat)으로 강제 고정합니다.
        app.setWebApplicationType(WebApplicationType.SERVLET);

        app.run(args);
    }
}