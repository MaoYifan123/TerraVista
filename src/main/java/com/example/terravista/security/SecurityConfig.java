package com.example.terravista.security;

import com.example.terravista.service.UserServiceImpl;
import com.example.terravista.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

        @Autowired
        private UserServiceImpl userServiceImpl;

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Autowired
        private JwtUtil jwtUtil;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .cors(cors -> {
                        })
                        .csrf(csrf -> csrf.disable())
                        .exceptionHandling(exception -> exception
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/user/login",
                                        "/user/register")
                                .permitAll()
                                .requestMatchers("/poi/all").permitAll()
                                // .requestMatchers("/poi/search").permitAll()
                                .requestMatchers("/poi/**").permitAll()
                                .requestMatchers("/public/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                                // 仅允许用户、管理员登陆后访问
                                // .requestMatchers("/ai/**").hasAnyRole("USER", "ADMIN")
                                .requestMatchers("/ai/**").permitAll()
                                .anyRequest().authenticated())
                        .addFilter(new JwtAuthenticationFilter(
                                authenticationManager(http
                                        .getSharedObject(AuthenticationConfiguration.class)),
                                jwtUtil))
                        .addFilter(new JwtAuthorizationFilter(
                                authenticationManager(http
                                        .getSharedObject(AuthenticationConfiguration.class)),
                                userServiceImpl,
                                jwtUtil));

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}