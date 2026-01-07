package com.example.chat.global.config;

import com.example.chat.global.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(corsConfig()); // 👈 corsConfig() 메서드 호출
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // ⭐️ 1. 모든 경로의 권한을 이곳에서 한 번에 설정합니다.
        http.authorizeHttpRequests(auth -> auth
                // [공개 경로] - 누구나 접근 가능
                .requestMatchers(
                        "/",
                        "/login",
                        "/join",
                        "/chat",          // 👈 /chat 페이지 자체는 공개
                        "/*.html",       // 👈 login.html, join.html, chat.html
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/error",
                        "/api/auth/**",       // 로그인/회원가입 API
                        "/api/users/check", // ID 중복 체크 API
                        "/ws/**"              // WebSocket 접속 경로
                ).permitAll()

                .requestMatchers("/actuator/**").permitAll()

                // [보안 경로] - 인증(로그인)이 반드시 필요한 경로
                .requestMatchers(
                        "/api/**" // 👈 /api/auth/** 를 제외한 모든 API
                ).authenticated()

                // ⭐️ 위에서 지정되지 않은 나머지 모든 요청도 인증을 요구합니다.
                .anyRequest().authenticated()
        );

        // ⭐️ 2. JwtAuthenticationFilter를 모든 요청에 대해 적용
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // ⭐️ 3. 인증 실패 시 처리
        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {

                    // 1. 요청 URI를 확인합니다.
                    String requestUri = request.getRequestURI();

                    // 2. API 요청일 경우 (JS가 보낸 fetch)
                    if (requestUri.startsWith("/api/")) {
                        // /login 리디렉션 대신 401 Unauthorized 응답을 보냅니다.
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    } else {
                        // 3. 브라우저 페이지 이동일 경우 (예: /chat 직접 입력)
                        // 기존처럼 /login 페이지로 리디렉션합니다.
                        response.sendRedirect("/login");
                    }
                })
        );

        return http.build();
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    private Customizer<CorsConfigurer<HttpSecurity>> corsConfig() {
        return (cors) -> cors
                .configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));
                    return configuration;
                });
    }
}

