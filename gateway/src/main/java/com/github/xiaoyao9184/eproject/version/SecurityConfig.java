package com.github.xiaoyao9184.eproject.version;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Created by xy on 2021/3/12.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Autowired
    PathMappedEndpoints pathMappedEndpoints;

    @Autowired
    OAuth2ClientProperties oAuth2ClientProperties;

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
                    .jwt()
                        .jwtAuthenticationConverter(
                                new ReactiveJwtAuthenticationConverterAdapter(
                                        new OAuth2ClientProviderUserNameAttributeJwtAuthenticationConverter(oAuth2ClientProperties)
                                ));

        http
                .authorizeExchange()
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

    /**
     * Support use OAuth Provider's UserNameAttribute for JwtAuthentication name
     * Multiple providers use jwt claim 'origin' to determine uniqueness
     */
    public static class OAuth2ClientProviderUserNameAttributeJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

        private OAuth2ClientProperties oAuth2ClientProperties;
        private Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter
                = new JwtGrantedAuthoritiesConverter();

        public OAuth2ClientProviderUserNameAttributeJwtAuthenticationConverter(OAuth2ClientProperties oAuth2ClientProperties) {
            this.oAuth2ClientProperties = oAuth2ClientProperties;
        }

        private String getUserNameAttribute(Jwt jwt){
            return Optional.ofNullable(jwt.getClaimAsString("origin"))
                    .map(origin -> oAuth2ClientProperties.getProvider().get(origin))
                    .map(OAuth2ClientProperties.Provider::getUserNameAttribute)
                    .map(jwt::getClaimAsString)
                    .orElse(jwt.getSubject());
        }

        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {
            Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
            return new JwtAuthenticationToken(jwt, authorities, getUserNameAttribute(jwt));
        }

        @Deprecated
        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
            return this.jwtGrantedAuthoritiesConverter.convert(jwt);
        }

        public void setJwtGrantedAuthoritiesConverter(Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter) {
            Assert.notNull(jwtGrantedAuthoritiesConverter, "jwtGrantedAuthoritiesConverter cannot be null");
            this.jwtGrantedAuthoritiesConverter = jwtGrantedAuthoritiesConverter;
        }
    }

}
