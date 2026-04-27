package com.ppopi.ppopihouse.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public OpenAPI ppopiHouseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("뽀삐의 집 API")
                        .description("뽀삐의 집 캡스톤 프로젝트 API 문서")
                        .version("v1.0.0"));
    }
}
