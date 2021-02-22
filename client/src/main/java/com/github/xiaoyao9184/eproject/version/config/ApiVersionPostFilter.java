package com.github.xiaoyao9184.eproject.version.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Created by xy on 2021/2/3.
 */
public class ApiVersionPostFilter extends ApiVersionFilter {
    private static Logger logger = LoggerFactory.getLogger(ApiVersionPostFilter.class);

    public ApiVersionPostFilter(WebClient webClient, ApiVersionProperties apiVersionProperties) {
        super(webClient,apiVersionProperties);
    }

    @Override
    HttpStatus code() {
        return HttpStatus.CREATED;
    }

    @Override
    HttpMethod method() {
        return HttpMethod.POST;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(skip(exchange)){
            return chain.filter(exchange);
        }

        ServerHttpRequest req = exchange.getRequest();
        if(req.getMethod() != method()){
            return chain.filter(exchange);
        }
        logger.debug("Http method matched!");

        //call real api
        return chain.filter(exchange).then(Mono.defer(() -> {
            ServerHttpResponse res = exchange.getResponse();
            //get request body
            String body = exchange.getAttribute("cachedRequestBodyObject");
            if(res.getStatusCode() == code() &&
                    body != null){
                //maybe use response body HATEOAS self url
                URI location = res.getHeaders().getLocation();
                if(location != null){
                    return send(
                            method(),
                            location,
                            exchange.getResponse().getHeaders(),
                            body)
                            .then();
                } else {
                    logger.info("Proxy {} target {} not support 'location' header.",
                            req.getId(),
                            req.getURI().toString());
                }
            } else {
                logger.info("Proxy {} target {} not return '{}' status code.",
                        req.getId(),
                        req.getURI().toString(),
                        this.code().toString());
            }
            return Mono.empty();
        }));
    }
}
