package com.github.xiaoyao9184.eproject.version.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.*;

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

    /**
     * downstream service options
     */
    private List<DownstreamService> downstreamServices = new ArrayList<>();

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public List<DownstreamService> getDownstreamServices() {
        return downstreamServices;
    }

    public void setDownstreamServices(List<DownstreamService> downstreamServices) {
        this.downstreamServices = downstreamServices;
    }

    public static class DownstreamService {

        private String name;

        private boolean enable = true;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }
    }
}
