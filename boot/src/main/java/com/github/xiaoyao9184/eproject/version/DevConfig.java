package com.github.xiaoyao9184.eproject.version;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

@Profile("dev")
@Configuration
public class DevConfig {

    @Bean
    public WebClient webClient(
            Environment environment, WebClient.Builder builder){
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
}
