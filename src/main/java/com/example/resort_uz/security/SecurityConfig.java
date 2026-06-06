package com.example.resort_uz.security;

import com.example.resort_uz.config.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ========== SWAGGER ==========
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/api/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()

                        // ========== RASMLAR ==========
                        .requestMatchers("/uploads/**").permitAll()

                        // ========== AUTH ==========
                        .requestMatchers("/auth/**").permitAll()

                        // ========== USER PANEL — GET ==========
                        .requestMatchers(HttpMethod.GET, "/resorts/featured").permitAll()
                        .requestMatchers(HttpMethod.GET, "/resorts/filter").permitAll()
                        .requestMatchers(HttpMethod.GET, "/resorts/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/resorts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/regions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/regions/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/photos/{resortId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/reviews/resort/{resortId}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/amenities").permitAll()
                        .requestMatchers(HttpMethod.GET, "/services/{resortId}").permitAll()

                        // ========== BAND QILISH — mehmon ==========
                        .requestMatchers(HttpMethod.POST, "/bookings").permitAll()
                        .requestMatchers(HttpMethod.GET, "/bookings/active/{resortId}").permitAll()

                        // ========== SHARH — mehmon ==========
                        .requestMatchers(HttpMethod.POST, "/reviews").permitAll()

                        // ========== SUPERADMIN ==========
                        .requestMatchers("/admins/superadmin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/regions/admin/**").hasRole("SUPERADMIN")
                        .requestMatchers("/amenities/admin/**").hasRole("SUPERADMIN")

                        // ========== SUPERADMIN + OWNER ==========
                        .requestMatchers("/admins/me").hasAnyRole("SUPERADMIN", "OWNER")
                        .requestMatchers("/resorts/admin/**").hasAnyRole("SUPERADMIN", "OWNER")
                        .requestMatchers("/photos/admin/**").hasAnyRole("SUPERADMIN", "OWNER")
                        .requestMatchers("/reviews/admin/**").hasAnyRole("SUPERADMIN", "OWNER")
                        .requestMatchers("/bookings/admin/**").hasAnyRole("SUPERADMIN", "OWNER")
                        .requestMatchers("/services/admin/**").hasAnyRole("SUPERADMIN", "OWNER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Barcha originlarga ruxsat — production da Vercel URL qo'shiladi
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}