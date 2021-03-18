package com.github.xiaoyao9184.eproject.version;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.HashMap;

/**
 * Created by xy on 2021/2/3.
 */
@SpringBootApplication
@EnableEurekaClient
public class VersionGatewayApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(VersionGatewayApplication.class);
        app.setDefaultProperties(new HashMap<String, Object>() {{
            put("spring.profiles.default", "dev");
        }});
        app.run(args);
    }

}
