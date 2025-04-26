// Path: src/main/java/com/bufalari/supplier/security/CustomUserDetailsService.java
package com.bufalari.supplier.service;


import com.bufalari.supplier.client.AuthServiceClient;
import com.bufalari.supplier.dto.UserDetailsDTO;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Service to load user-specific data by calling the Authentication Service.
 * Used by Spring Security during JWT validation.
 * Serviço para carregar dados específicos do usuário chamando o Serviço de Autenticação.
 * Usado pelo Spring Security durante a validação do JWT.
 */
@Service("supplierUserDetailsService") // Give it a specific name if needed
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final AuthServiceClient authServiceClient;

    public CustomUserDetailsService(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    /**
     * Loads the user's details by username from the authentication service.
     * Carrega os detalhes do usuário pelo nome de usuário do serviço de autenticação.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user details for username: {}", username);
        UserDetailsDTO userDetailsDTO;
        try {
            userDetailsDTO = authServiceClient.getUserByUsername(username);

            if (userDetailsDTO == null) {
                log.warn("User details DTO is null for username: {}", username);
                throw new UsernameNotFoundException("User not found via authentication service: " + username);
            }

            log.info("Successfully loaded user details via auth service for username: {}", username);
            return new User(
                    userDetailsDTO.getUsername(),
                    userDetailsDTO.getPassword() != null ? userDetailsDTO.getPassword() : "", // Provide empty if null
                    userDetailsDTO.getRoles() != null ?
                        userDetailsDTO.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                            .collect(Collectors.toList())
                        : Collections.emptyList()
            );

        } catch (FeignException.NotFound e) {
            log.warn("User not found via auth service for username: {}. Feign status: 404", username);
            throw new UsernameNotFoundException("User not found: " + username, e);
        } catch (FeignException e) {
            log.error("Feign error calling authentication service for username: {}. Status: {}, Message: {}", username, e.status(), e.getMessage());
            throw new UsernameNotFoundException("Failed to load user details (auth service communication error) for user: " + username, e);
        } catch (Exception e) {
            log.error("Unexpected error loading user details for username: {}", username, e);
            throw new UsernameNotFoundException("Unexpected error loading user details for user: " + username, e);
        }
    }
}