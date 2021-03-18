package com.github.xiaoyao9184.eproject.version.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Created by xy on 2021/2/3.
 */
public class ApiVersionPatchFilter extends ApiVersionFilter {

    public ApiVersionPatchFilter(WebClient webClient, ApiVersionProperties apiVersionProperties,
                                 ApiVersionPrincipalProvider apiVersionPrincipalProvider) {
        super(webClient,apiVersionProperties,apiVersionPrincipalProvider);
    }

    @Override
    HttpStatus code() {
        return HttpStatus.OK;
    }

    @Override
    HttpMethod method() {
        return HttpMethod.PATCH;
    }
}
