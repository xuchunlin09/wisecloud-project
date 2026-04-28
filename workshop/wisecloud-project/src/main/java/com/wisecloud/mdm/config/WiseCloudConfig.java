package com.wisecloud.mdm.config;

import com.wiseasy.openapi.client.Config;
import com.wiseasy.openapi.client.DefaultOpenApiClient;
import com.wiseasy.openapi.client.OpenApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WiseCloud SDK configuration.
 * Initializes the OpenApiClient bean with AK/SK credentials and sandbox mode.
 */
@Configuration
public class WiseCloudConfig {

    @Value("${wisecloud.access-key-id}")
    private String accessKeyId;

    @Value("${wisecloud.access-key-secret}")
    private String accessKeySecret;

    @Value("${wisecloud.sandbox:true}")
    private boolean sandbox;

    @Bean
    public OpenApiClient openApiClient() {
        Config config = new Config(accessKeyId, accessKeySecret);

        // Enable sandbox mode for development/testing
        if (sandbox) {
            Config.setSandBox(true);
        }

        // HTTP connection pool and timeout settings
        config.setConnectTimeoutMs(10000);   // 10 seconds connect timeout
        config.setReadTimeoutMs(30000);      // 30 seconds read timeout
        config.setMaxConnTotal(200);         // max total connections
        config.setMaxConnPerRoute(50);       // max connections per route

        return new DefaultOpenApiClient(config);
    }
}
