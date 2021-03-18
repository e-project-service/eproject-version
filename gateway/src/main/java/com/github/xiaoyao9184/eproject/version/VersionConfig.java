package com.github.xiaoyao9184.eproject.version;

import com.github.xiaoyao9184.eproject.version.config.ApiVersionPrincipalProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by xy on 2021/3/17.
 */
@Configuration
public class VersionConfig {

    @Bean
    public ApiVersionPrincipalProvider apiVersionPrincipalProvider() {
        return (exchange,chain) ->
                Flux.concat(
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .map(Authentication::getName),
                        Mono.just("none")
                ).next();

    }

}
