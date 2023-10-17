package com.huangch.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * @author huangch
 * @date 2023-10-08
 */
@Configuration
@EnableWebSocket
public class SpringWebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getSpringWebSocketHandler(), "/websocket/server")
                .addInterceptors(getInterceptor()).setAllowedOrigins("*");

        registry.addHandler(getSpringWebSocketHandler(), "/sockjs/server").setAllowedOrigins("*")
                .addInterceptors(getInterceptor()).withSockJS();
    }

    @Bean
    public SpringWebSocketHandler getSpringWebSocketHandler() {
        return new SpringWebSocketHandler();
    }

    @Bean
    public SpringWebSocketHandlerInterceptor getInterceptor() {
        return new SpringWebSocketHandlerInterceptor();
    }
}