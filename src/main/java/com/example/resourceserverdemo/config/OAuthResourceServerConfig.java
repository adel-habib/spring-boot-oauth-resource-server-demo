package com.example.resourceserverdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

import com.example.resourceserverdemo.filters.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class OAuthResourceServerConfig {

    // Inject URI to the authorization server's JSON Web Key set (RFC 7517)
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    // define a custom JwtAuthenticationConverter to change the authority claim for
    // "scopes" to "roles"
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeHttpRequests((authorize) -> authorize
                            // Make route only accessible to users with role "admin"
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasAuthority("ROLE_admin")
                            // make route only accessible to users who are either an admin or a moderator 
                        .requestMatchers(HttpMethod.GET, "/moderator/**").hasAnyAuthority("ROLE_admin", "ROLE_moderator")
                            // All other routes are accessible to any user with a valid access token
                        .anyRequest().authenticated()
                )
                            // configure the resource server with the custom authentication converter 
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                            // Add our custom JWT filter affter bearer authentication
                .addFilterAfter(new JwtRequestFilter(), BearerTokenAuthenticationFilter.class);
                            // Alternatively to use defaults 
                            // .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
                            
        return http.build();
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
    }

}
