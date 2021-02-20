package com.github.xiaoyao9184.eproject.version.router;

import com.github.xiaoyao9184.eproject.version.document.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * API version
 * Created by xy on 2021/2/3.
 */
public class ApiVersionHandler {

    public static final String API_PATH = "/v1/versions";
    public static final String PATH_VARIABLE = "path";
    public static final String TIME_PARAM = "time";
    private static final String SERVICE_ID_HEADER = "serviceId";

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    public Mono<ServerResponse> saveHistory(ServerRequest request) {
        String path = request.pathVariable(PATH_VARIABLE);

        ApiVersion apiVersion = new ApiVersion();
        apiVersion.setPath(path);
        apiVersion.setMethod(request.methodName());
        apiVersion.setTime(LocalDateTime.now());
        //TODO get username form token
        apiVersion.setUseName("UNKNOWN");

        //get service-id form params
        request.queryParam(SERVICE_ID_HEADER)
                .ifPresent(apiVersion::setServiceId);
        Mono<ApiVersion> result = request.bodyToMono(String.class)
                .doOnNext(apiVersion::setBody)
                //empty also save to mongo
                .then(Mono.defer(() -> reactiveMongoTemplate.save(apiVersion,apiVersion.getPath())));
        return ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(result, ApiVersion.class);
    }


}
