package com.github.xiaoyao9184.eproject.version.router;

import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.github.xiaoyao9184.eproject.version.router.ApiVersionHandler.API_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

/**
 * API version
 * Created by xy on 2021/2/3.
 */
@Configuration
public class ApiVersionRouterConfig {

    @Bean
    public ApiVersionHandler apiVersionHandler(){
        return new ApiVersionHandler();
    }

    @Bean
    public RouterFunction<ServerResponse> routerOfApiVersion(
            WebFluxProperties webFluxProperties,
            ApiVersionHandler apiVersionHandler
    ) {
        String basePath = webFluxProperties.getBasePath();
        return RouterFunctions.route()
                //POST,PUT,PATH,DELETE request，mean save one history of API version
                //POST request path not target collection，
                //Because the Header Location has been extracted from the Client to get the specific URI
                .POST(basePath + API_PATH + "/{*path}",
                        accept(MediaType.ALL),
                        apiVersionHandler::saveHistory)
                .PUT(basePath + API_PATH + "/{*path}",
                        accept(MediaType.ALL),
                        apiVersionHandler::saveHistory)
                .PATCH(basePath + API_PATH + "/{*path}",
                        accept(MediaType.ALL),
                        apiVersionHandler::saveHistory)
                .DELETE(basePath + API_PATH + "/{*path}",
                        accept(MediaType.ALL),
                        apiVersionHandler::saveHistory)
                .build();
    }


}
