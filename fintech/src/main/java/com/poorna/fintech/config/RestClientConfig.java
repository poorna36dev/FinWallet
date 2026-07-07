package com.poorna.fintech.config;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final BrevoProperties properties;

    @Bean
    public RestClient brevoRestClient() {

        return RestClient.builder()

                .baseUrl(properties.getBaseUrl())

                .defaultHeader("api-key", properties.getApiKey())

                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)

                .build();
    }

}
