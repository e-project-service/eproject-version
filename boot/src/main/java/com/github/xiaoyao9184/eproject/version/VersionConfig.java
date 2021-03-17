package com.github.xiaoyao9184.eproject.version;

import com.github.xiaoyao9184.eproject.version.router.ApiVersionUserProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Optional;

import static com.github.xiaoyao9184.eproject.version.router.ApiVersionHandler.USERNAME_PARAM;

/**
 * Created by xy on 2021/3/17.
 */
@Configuration
public class VersionConfig {

    @Bean
    public ApiVersionUserProvider apiVersionUserProvider() {
        return request ->
                Flux.concat(
                        Mono.fromCallable(() -> request.queryParam(USERNAME_PARAM))
                                .filter(Optional::isPresent)
                                .map(Optional::get),
                        request.principal()
                                .map(Principal::getName),
                        ReactiveSecurityContextHolder.getContext()
                                .map(SecurityContext::getAuthentication)
                                .map(Authentication::getName)
                ).next();

    }

}
