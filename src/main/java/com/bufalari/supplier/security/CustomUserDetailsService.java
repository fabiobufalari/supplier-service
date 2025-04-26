package com.bufalari.supplier.security;


import com.bufalari.supplier.client.AuthServiceClient;
import com.bufalari.supplier.dto.UserDetailsDTO;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Serviço para carregar detalhes do usuário do serviço de autenticação.
 * Service to load user details from the authentication service.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthServiceClient authServiceClient;

    public CustomUserDetailsService(AuthServiceClient authServiceClient) {
        this.authServiceClient = authServiceClient;
    }

    /**
     * Carrega os detalhes do usuário pelo nome de usuário.
     * Loads user details by username.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UserDetailsDTO userDetailsDTO = authServiceClient.getUserByUsername(username);
            if (userDetailsDTO == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }

            return new User(
                    userDetailsDTO.getUsername(),
                    userDetailsDTO.getPassword(),
                    userDetailsDTO.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList())
            );
        } catch (Exception e) {
            throw new UsernameNotFoundException("Failed to load user: " + username, e);
        }
    }
}