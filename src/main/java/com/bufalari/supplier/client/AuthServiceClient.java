// Path: src/main/java/com/bufalari/supplier/client/AuthServiceClient.java
package com.bufalari.supplier.client;


import com.bufalari.supplier.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for communication with the authentication service.
 * Cliente Feign para comunicação com o serviço de autenticação.
 */
// Ensure the URL property 'auth.service.url' is defined in application.yml
// Garanta que a propriedade URL 'auth.service.url' está definida no application.yml
@FeignClient(name = "auth-service-client-supplier", url = "${auth.service.url}")
public interface AuthServiceClient {

    /**
     * Retrieves user details by username from the authentication service.
     * Busca os detalhes do usuário por nome de usuário no serviço de autenticação.
     * Check the actual endpoint in your auth-service UserController.
     * Verifique o endpoint real no seu UserController do auth-service.
     * @param username The username to search for. / O nome de usuário a ser buscado.
     * @return UserDetailsDTO containing user information. / UserDetailsDTO contendo informações do usuário.
     */
    @GetMapping("/api/users/username/{username}") // <<< VERIFY THIS ENDPOINT / VERIFIQUE ESTE ENDPOINT
    UserDetailsDTO getUserByUsername(@PathVariable("username") String username);

     /**
     * Retrieves user details by UUID from the authentication service. Needed for auditing if storing UUID.
     * Busca os detalhes do usuário por UUID no serviço de autenticação. Necessário para auditoria se armazenar UUID.
     * Check the actual endpoint in your auth-service UserController.
     * Verifique o endpoint real no seu UserController do auth-service.
     * @param userId The UUID of the user. / O UUID do usuário.
     * @return UserDetailsDTO containing user information. / UserDetailsDTO contendo informações do usuário.
     */
     @GetMapping("/api/users/{id}") // <<< VERIFY THIS ENDPOINT / VERIFIQUE ESTE ENDPOINT
     UserDetailsDTO getUserById(@PathVariable("id") String userId); // Pass UUID as String
}