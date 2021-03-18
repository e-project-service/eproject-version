package com.github.xiaoyao9184.eproject.version.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyPredicateFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import javax.annotation.PostConstruct;

import static org.springframework.cloud.gateway.support.NameUtils.normalizeRoutePredicateName;

/**
 * Created by xy on 2021/2/3.
 */
@Configuration
@EnableConfigurationProperties({ ApiVersionProperties.class })
public class ApiVersionAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ApiVersionAutoConfiguration.class);

    public ApiVersionAutoConfiguration() {
        if(logger.isDebugEnabled()){
            logger.debug("Init {}", ApiVersionAutoConfiguration.class.getName());
        }
    }

    //TODO use Autowired
    public WebClient webClient(Environment environment){
        WebClient.Builder builder = WebClient.builder();
        Boolean proxySet = environment.getProperty("proxySet", Boolean.class, false);
        String proxyHost = environment.getProperty("http.proxyHost");
        Integer proxyPort = environment.getProperty("http.proxyPort",Integer.class);
        if(proxySet != null && proxySet){
            HttpClient httpClient = HttpClient.create()
                    .tcpConfiguration(tcpClient ->
                            tcpClient.proxy(proxy ->
                                    proxy.type(ProxyProvider.Proxy.HTTP)
                                            .host(proxyHost)
                                            .port(proxyPort)
                            ));
            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
            builder.clientConnector(connector);
        }
        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiVersionPostFilter apiVersionPostFilter(
            ApiVersionPrincipalProvider apiVersionPrincipalProvider,
            ApiVersionProperties apiVersionProperties, Environment environment
    ){
        return new ApiVersionPostFilter(webClient(environment),apiVersionProperties,apiVersionPrincipalProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiVersionPutFilter apiVersionPutFilter(
            ApiVersionPrincipalProvider apiVersionPrincipalProvider,
            ApiVersionProperties apiVersionProperties, Environment environment
    ){
        return new ApiVersionPutFilter(webClient(environment),apiVersionProperties,apiVersionPrincipalProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiVersionPatchFilter apiVersionPatchFilter(
            ApiVersionPrincipalProvider apiVersionPrincipalProvider,
            ApiVersionProperties apiVersionProperties, Environment environment
    ){
        return new ApiVersionPatchFilter(webClient(environment),apiVersionProperties,apiVersionPrincipalProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiVersionDeleteFilter apiVersionDeleteFilter(
            ApiVersionPrincipalProvider apiVersionPrincipalProvider,
            ApiVersionProperties apiVersionProperties, Environment environment
    ){
        return new ApiVersionDeleteFilter(webClient(environment),apiVersionProperties,apiVersionPrincipalProvider);
    }

    @Bean
    public AlwaysReadRequestBody alwaysReadRequestBody(){
        return new AlwaysReadRequestBody();
    }

    @ConditionalOnProperty(name = "spring.cloud.gateway.discovery.locator.enabled")
    @Configuration
    public static class DiscoveryLocatorConfiguration {

        @Autowired
        DiscoveryLocatorProperties discoveryLocatorProperties;

        @PostConstruct
        public void init() {
            PredicateDefinition predicate = new PredicateDefinition();
            predicate.setName(normalizeRoutePredicateName(ReadBodyPredicateFactory.class));
            predicate.addArg("inClass", "\"#{T(String)}\"");
            predicate.addArg("predicate", "\"#{@alwaysReadRequestBody}\"");
            discoveryLocatorProperties.getPredicates().add(predicate);
        }

    }

}
