package com.github.xiaoyao9184.eproject.version.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
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
import java.util.Optional;

/**
 * Created by xy on 2021/2/3.
 */
public abstract class ApiVersionFilter implements GlobalFilter {
    private static Logger logger = LoggerFactory.getLogger(ApiVersionFilter.class);

    private WebClient webClient;
    private ApiVersionProperties apiVersionProperties;
    protected ApiVersionPrincipalProvider apiVersionPrincipalProvider;

    public ApiVersionFilter(WebClient webClient, ApiVersionProperties apiVersionProperties,
                            ApiVersionPrincipalProvider apiVersionPrincipalProvider) {
        this.webClient = webClient;
        this.apiVersionProperties = apiVersionProperties;
        this.apiVersionPrincipalProvider = apiVersionPrincipalProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if(skip(exchange)){
            logger.debug("skip version filter!");
            return chain.filter(exchange);
        }

        //call real api
        return chain.filter(exchange)
                .then(apiVersionPrincipalProvider.provide(exchange, chain))
                .flatMap(user -> {
            ServerHttpRequest req = exchange.getRequest();
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
                            user,
                            this.header(exchange.getRequest().getHeaders(),exchange.getResponse().getHeaders()),
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
        });
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
                                String user,
                                HttpHeaders httpHeaders,
                                String body){
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(this.apiVersionProperties.getServiceUrl())
                .path(location.getPath());
        if(this.apiVersionProperties.getForward().isPrincipalName()){
            //get user name of principal
            builder.queryParam(this.apiVersionProperties.getForward().getPrincipalNameQueryParam(),user);
        }
        URI uri = builder
                .build()
                .toUri();
        return this.webClient.method(method)
                .uri(uri)
                .headers(headers -> headers.putAll(httpHeaders))
                .bodyValue(body)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(String.class));
    }

    protected boolean skip(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        boolean isEnable = apiVersionProperties.getDownstreamServices()
                .stream()
                .filter(ds -> ds.getName() != null)
                .filter(ds -> ds.getName().equalsIgnoreCase(route.getId()))
                .findFirst()
                .map(ApiVersionProperties.DownstreamService::isEnable)
                .orElse(false);

        ServerHttpRequest req = exchange.getRequest();
        boolean matchMethod = req.getMethod() == method();

        return !isEnable || !matchMethod;
    }

    protected HttpHeaders header(HttpHeaders requestHeaders, HttpHeaders responseHeaders){
        HttpHeaders result = new HttpHeaders();

        requestHeaders.entrySet().stream()
                .filter(kv ->
                        this.apiVersionProperties.getForward().isRequestHeadersAll() ||
                        this.apiVersionProperties.getForward().getRequestHeaders().contains(kv.getKey()))
                .forEach(kv -> result.addAll(kv.getKey(),kv.getValue()));

        responseHeaders.entrySet().stream()
                .filter(kv ->
                        this.apiVersionProperties.getForward().isResponseHeadersAll() ||
                        this.apiVersionProperties.getForward().getResponseHeaders().contains(kv.getKey()))
                .forEach(kv -> result.addAll(kv.getKey(),kv.getValue()));

        return result;
    }

    abstract HttpStatus code();

    abstract HttpMethod method();
}
