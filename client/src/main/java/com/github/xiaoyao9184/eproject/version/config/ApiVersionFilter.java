package com.github.xiaoyao9184.eproject.version.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * Created by xy on 2021/2/3.
 */
public abstract class ApiVersionFilter implements GlobalFilter {
    private static Logger logger = LoggerFactory.getLogger(ApiVersionFilter.class);

    private WebClient webClient;
    private ApiVersionProperties apiVersionProperties;

    public ApiVersionFilter(WebClient webClient, ApiVersionProperties apiVersionProperties) {
        this.webClient = webClient;
        this.apiVersionProperties = apiVersionProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
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
                URI location = req.getURI();
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

    /**
     * call version-service
     * @param method
     * @param location
     * @param httpHeaders
     * @param body
     * @return
     */
    protected Mono<String> send(HttpMethod method,
                                URI location,
                                HttpHeaders httpHeaders,
                                String body){
        URI uri = UriComponentsBuilder.fromUriString(this.apiVersionProperties.getServiceUrl())
                .path(location.getPath())
                .build()
                .toUri();
        return this.webClient.method(method)
                .uri(uri)
                .headers(headers -> headers.putAll(httpHeaders))
                .bodyValue(body)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    abstract HttpStatus code();

    abstract HttpMethod method();
}
