package com.github.xiaoyao9184.eproject.version.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * API version user-name provider
 * Created by xy on 2021/3/17.
 */
public interface ApiVersionPrincipalProvider {

    /**
     * provide user-name
     * @return user-name
     */
    Mono<String> provide(ServerWebExchange exchange, GatewayFilterChain chain);
}
