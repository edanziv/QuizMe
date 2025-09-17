package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) //turns off Cross-Site Request Forgery protection
                .cors(cors -> cors.configure(http)) //sets up Cross-Origin Resource Sharing
                .authorizeHttpRequests(auth -> auth //allows any HTTP request without authentication or authorization
                        .anyRequest().permitAll());
        return http.build();
    }
}