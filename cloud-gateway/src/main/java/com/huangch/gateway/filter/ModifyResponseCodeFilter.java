package com.huangch.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @author huangch
 * @date 2023-10-07
 */
@Slf4j
@Configuration
public class ModifyResponseCodeFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(exchange.getResponse()) {

            @Override
            public boolean setStatusCode(@Nullable HttpStatusCode status) {
                log.info("请求原HTTP状态:{}", status);
                if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
                    return this.getDelegate().setStatusCode(HttpStatus.BAD_REQUEST);
                }
                return this.getDelegate().setStatusCode(status);
            }
        };

        ServerWebExchange webExchange = exchange.mutate().response(responseDecorator).build();
        return chain.filter(webExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
