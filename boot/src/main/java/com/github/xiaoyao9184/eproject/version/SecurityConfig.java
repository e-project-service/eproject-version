package com.github.xiaoyao9184.eproject.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by xy on 2021/3/12.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    PathMappedEndpoints pathMappedEndpoints;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {
        //noinspection unchecked
//        http.apply(new OAuth2ClientAuthorizedConfigurer().defaultSuccessUrl("/"));
        // @formatter:off
        http
                .oauth2Client()
                    .and()
                .oauth2Login()
                    .and()
                .oauth2ResourceServer()
                    .jwt();

        http
                .authorizeExchange()
                    .pathMatchers(pathMappedEndpoints.getBasePath() + "/**")
                        .hasAnyAuthority("SCOPE_actuate.admin", "ROLE_DEV", "ROLE_ACTUATOR")
                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .pathMatchers("/").permitAll()
                    .pathMatchers(HttpMethod.GET, "/v1/versions/**")
                        .hasAnyAuthority("SCOPE_version.admin", "SCOPE_version.read", "ROLE_DEV")
                    .pathMatchers(HttpMethod.POST, "/v1/versions/**")
                        .hasAnyAuthority("SCOPE_version.admin", "SCOPE_version.write", "ROLE_DEV")
                    .pathMatchers(HttpMethod.PUT, "/v1/versions/**")
                        .hasAnyAuthority("SCOPE_version.admin", "SCOPE_version.write", "ROLE_DEV")
                    .pathMatchers(HttpMethod.PATCH, "/v1/versions/**")
                        .hasAnyAuthority("SCOPE_version.admin", "SCOPE_version.write", "ROLE_DEV")
                    .pathMatchers(HttpMethod.DELETE, "/v1/versions/**")
                        .hasAnyAuthority("SCOPE_version.admin", "SCOPE_version.write", "ROLE_DEV")
                    .anyExchange().authenticated()
                    .and()
                .cors().and()
                .csrf().disable()
                .headers().frameOptions().disable().and()
                .httpBasic();
        // @formatter:on
        return http.build();
    }


    @Configuration
    public static class GlobalUserConfig {

        private static final String NOOP_PASSWORD_PREFIX = "{noop}";

        private static final Pattern PASSWORD_ALGORITHM_PATTERN = Pattern.compile("^\\{.+}.*$");

        private static final Log logger = LogFactory.getLog(GlobalUserConfig.class);

        @Bean
        public MapReactiveUserDetailsService reactiveUserDetailsService(
                SecurityProperties properties,
                ObjectProvider<PasswordEncoder> passwordEncoder) {
            SecurityProperties.User user = properties.getUser();
            UserDetails userDetails = getUserDetails(user, getOrDeducePassword(user, passwordEncoder.getIfAvailable()));
            return new MapReactiveUserDetailsService(userDetails);
        }

        private UserDetails getUserDetails(SecurityProperties.User user, String password) {
            List<String> roles = user.getRoles();
            return User.withUsername(user.getName()).password(password).roles(StringUtils.toStringArray(roles)).build();
        }

        private String getOrDeducePassword(SecurityProperties.User user, PasswordEncoder encoder) {
            String password = user.getPassword();
            if (user.isPasswordGenerated()) {
                logger.info(String.format("%n%nUsing generated security password: %s%n", user.getPassword()));
            }
            if (encoder != null || PASSWORD_ALGORITHM_PATTERN.matcher(password).matches()) {
                return password;
            }
            return NOOP_PASSWORD_PREFIX + password;
        }
    }

}
