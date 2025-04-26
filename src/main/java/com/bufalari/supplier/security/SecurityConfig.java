// Path: src/main/java/com/bufalari/supplier/config/SecurityConfig.java
package com.bufalari.supplier.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the Supplier Service.
 * Configures JWT authentication, CORS, and endpoint authorization.
 * Configuração de segurança para o Serviço de Fornecedores.
 * Configura autenticação JWT, CORS e autorização de endpoints.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Enable @PreAuthorize etc.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Define public endpoints / Define endpoints públicos
    private static final String[] PUBLIC_MATCHERS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/actuator/**" // Permit Actuator endpoints / Permite endpoints do Actuator
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(PUBLIC_MATCHERS).permitAll() // Public endpoints / Endpoints públicos
                // Define specific auth rules for supplier endpoints / Define regras de auth específicas para endpoints de fornecedor
                .requestMatchers(HttpMethod.GET, "/api/suppliers/**").authenticated() // Allow any authenticated user to read / Permite qualquer usuário autenticado ler
                .requestMatchers(HttpMethod.POST, "/api/suppliers").hasAnyRole("ADMIN", "MANAGER", "PURCHASING") // Example roles for creation / Exemplo de roles para criação
                .requestMatchers(HttpMethod.PUT, "/api/suppliers/**").hasAnyRole("ADMIN", "MANAGER", "PURCHASING") // Example roles for update / Exemplo de roles para atualização
                .requestMatchers(HttpMethod.DELETE, "/api/suppliers/**").hasRole("ADMIN") // Example role for deletion / Exemplo de role para deleção
                // Require authentication for any other request not explicitly matched
                // Exige autenticação para qualquer outra requisição não explicitamente definida
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions / Sessões stateless
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter / Adiciona filtro JWT

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANT: Restrict in production! / IMPORTANTE: Restringir em produção!
        configuration.setAllowedOrigins(List.of("*")); // Allow all origins for now / Permite todas origens por enquanto
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply CORS to all paths / Aplica CORS a todos os caminhos
        return source;
    }
}