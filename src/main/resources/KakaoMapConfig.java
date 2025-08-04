package org.example.hugmeexp.global.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao.map")
public class KakaoMapConfig {

    private String apiKey;
    private String baseUrl = "https://dapi.kakao.com";
    private String geocodingUrl = "/v2/local/search/address.json";
    private String coordToAddressUrl = "/v2/local/geo/coord2address.json";
}