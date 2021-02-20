package com.github.xiaoyao9184.eproject.version.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * Created by xy on 2021/2/3.
 */
@Configuration
@ConfigurationProperties(prefix = "project.version", ignoreUnknownFields = true)
public class ApiVersionProperties {

    /**
     * version service url.
     */
    private String serviceUrl = "";

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
