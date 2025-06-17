package com.volbit.config;

// src/main/java/com/volbit/config/WebSocketConfig.java


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the HTTP endpoint clients will use to initiate the WebSocket (with SockJS fallback)
        registry
                .addEndpoint("/ws")            // e.g. ws://localhost:8080/ws
                .setAllowedOriginPatterns("*") // adjust in prod!
                .withSockJS();                 // fallback for browsers without native WS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix for messages from server → client
        config.enableSimpleBroker("/topic");
        // Prefix for messages from client → server (if you add @MessageMapping handlers later)
        config.setApplicationDestinationPrefixes("/app");
    }
}
