package com.github.xiaoyao9184.eproject.version.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created by xy on 2021/2/3.
 */
public class ApiVersionDeleteFilter extends ApiVersionFilter {

    public ApiVersionDeleteFilter(WebClient webClient, ApiVersionProperties apiVersionProperties) {
        super(webClient,apiVersionProperties);
    }

    @Override
    HttpStatus code() {
        return HttpStatus.ACCEPTED;
    }

    @Override
    HttpMethod method() {
        return HttpMethod.DELETE;
    }
}
