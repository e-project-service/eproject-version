package com.github.xiaoyao9184.eproject.version.router;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

/**
 * API version user-name provider
 * Created by xy on 2021/3/17.
 */
public interface ApiVersionUserProvider {

    /**
     * provide user-name
     * @return user-name
     */
    Mono<String> provide(ServerRequest request);
}
