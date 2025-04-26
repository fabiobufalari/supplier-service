// Path: src/main/java/com/bufalari/supplier/dto/SupplierDTO.java
package com.bufalari.supplier.dto;

import jakarta.validation.Valid; // Import for validating nested DTOs
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for Supplier data transfer. Includes validation rules.
 * DTO para transferência de dados de Fornecedor. Inclui regras de validação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    private Long id; // Read-only in response / Somente leitura na resposta


    @Size(max = 200, message = "Supplier name max length is 200 characters / Nome do fornecedor: tamanho máx 200 caracteres")
    @NotBlank(message = "{supplier.name.notblank}")
    private String name;

    @Size(max = 200, message = "Trade name max length is 200 characters / Nome fantasia: tamanho máx 200 caracteres")
    private String tradeName;

    @Size(max = 50, message = "Business ID max length is 50 characters / ID de negócio: tamanho máx 50 caracteres")
    @NotBlank(message = "{supplier.businessid.notblank}")
    private String businessIdentificationNumber;

    @NotNull(message = "{supplier.address.notnull}")
    @Valid
    private AddressDTO address;

    @Size(max = 100, message = "Contact name max length is 100 characters / Nome do contato: tamanho máx 100 caracteres")
    private String primaryContactName;

    @Size(max = 30, message = "Contact phone max length is 30 characters / Telefone do contato: tamanho máx 30 caracteres")
    private String primaryContactPhone;

    @Email(message = "Invalid contact email format / Formato de e-mail de contato inválido")
    @Size(max = 100, message = "Contact email max length is 100 characters / E-mail do contato: tamanho máx 100 caracteres")
    private String primaryContactEmail;

    @Size(max = 50, message = "Category max length is 50 characters / Categoria: tamanho máx 50 caracteres")
    private String category;

    @Size(max = 100, message = "Bank name max length is 100 characters / Nome do banco: tamanho máx 100 caracteres")
    private String bankName;

    @Size(max = 20, message = "Bank agency max length is 20 characters / Agência bancária: tamanho máx 20 caracteres")
    private String bankAgency;

    @Size(max = 30, message = "Bank account max length is 30 characters / Conta bancária: tamanho máx 30 caracteres")
    private String bankAccount;

    // List of document references (usually added/managed separately)
    // Lista de referências de documentos (geralmente adicionada/gerenciada separadamente)
    private List<String> documentReferences;


}