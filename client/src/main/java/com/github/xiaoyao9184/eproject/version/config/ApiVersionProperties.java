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
     * forward to version service
     */
    private Forward forward = new Forward();

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

    public Forward getForward() {
        return forward;
    }

    public void setForward(Forward forward) {
        this.forward = forward;
    }

    public List<DownstreamService> getDownstreamServices() {
        return downstreamServices;
    }

    public void setDownstreamServices(List<DownstreamService> downstreamServices) {
        this.downstreamServices = downstreamServices;
    }

    public static class Forward {

        /**
         * all request headers need to forward
         */
        private boolean requestHeadersAll = false;

        /**
         * request headers whitelist to forward
         */
        private List<String> requestHeaders = Arrays.asList("Authorization");

        /**
         * all response headers need to forward
         */
        private boolean responseHeadersAll = true;

        /**
         * response headers whitelist to forward
         */
        private List<String> responseHeaders = new ArrayList<>();

        /**
         * forward user-name of security principal
         */
        private boolean principalName = false;

        /**
         * user
         */
        private String principalNameQueryParam = "user";

        public boolean isRequestHeadersAll() {
            return requestHeadersAll;
        }

        public void setRequestHeadersAll(boolean requestHeadersAll) {
            this.requestHeadersAll = requestHeadersAll;
        }

        public List<String> getRequestHeaders() {
            return requestHeaders;
        }

        public void setRequestHeaders(List<String> requestHeaders) {
            this.requestHeaders = requestHeaders;
        }

        public boolean isResponseHeadersAll() {
            return responseHeadersAll;
        }

        public void setResponseHeadersAll(boolean responseHeadersAll) {
            this.responseHeadersAll = responseHeadersAll;
        }

        public List<String> getResponseHeaders() {
            return responseHeaders;
        }

        public void setResponseHeaders(List<String> responseHeaders) {
            this.responseHeaders = responseHeaders;
        }

        public boolean isPrincipalName() {
            return principalName;
        }

        public void setPrincipalName(boolean principalName) {
            this.principalName = principalName;
        }

        public String getPrincipalNameQueryParam() {
            return principalNameQueryParam;
        }

        public void setPrincipalNameQueryParam(String principalNameQueryParam) {
            this.principalNameQueryParam = principalNameQueryParam;
        }
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
