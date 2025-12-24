package com.lontsi.rubberduckpulsarconnector.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class AppConfig {


        @Bean
        public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
            http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/actuator/**").permitAll()
                            .anyExchange().authenticated()
                    );


            return http.build();
        }


    private CorsConfigurationSource corsConfigurationSource() {

        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(ServerWebExchange exchange) {
                CorsConfiguration cfg = new CorsConfiguration();

                cfg.setAllowedOriginPatterns(List.of("*"));
                cfg.setAllowCredentials(false);

                cfg.setAllowedHeaders(Collections.singletonList("*"));
                cfg.setAllowedMethods(Collections.singletonList("*"));
                cfg.setExposedHeaders(List.of("Authorization"));
                cfg.setMaxAge(3600L);
                return cfg;
            }


        };
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

