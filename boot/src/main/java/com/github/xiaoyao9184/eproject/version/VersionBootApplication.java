package com.github.xiaoyao9184.eproject.version;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;

/**
 * Created by xy on 2021/2/3.
 */
@SpringBootApplication
public class VersionBootApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(VersionBootApplication.class);
        app.setDefaultProperties(new HashMap<String, Object>() {{
            put("spring.profiles.default", "dev");
        }});
        app.run(args);
    }

}
