package com.orainge.union_message_service.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "websocket-client")
@Data
public class WebsocketClientConfig {
    private String id;
    private String securityKey;
}
