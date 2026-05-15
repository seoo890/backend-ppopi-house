package com.ppopi.ppopihouse.hospital.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "kakao.local")
public class KakaoProperties {

    private String restApiKey;
    private String baseUrl;
}