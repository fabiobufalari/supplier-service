// Path: src/main/java/com/bufalari/supplier/dto/UserDetailsDTO.java
package com.bufalari.supplier.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID; // Import UUID

/**
 * DTO for user details received from the authentication service.
 * DTO para os detalhes do usuário recebidos do serviço de autenticação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private UUID id; // Assuming auth service returns UUID / Assumindo que serviço auth retorna UUID
    private String username;
    private String password; // Usually received hashed / Geralmente recebido com hash
    private List<String> roles;
}