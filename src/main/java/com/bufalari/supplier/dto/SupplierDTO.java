package com.bufalari.supplier.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema; // Para anotações do Swagger

import java.util.List;
import java.util.UUID; // <<<--- IMPORT UUID

/**
 * DTO for Supplier data transfer. Includes validation rules.
 * DTO para transferência de dados de Fornecedor. Inclui regras de validação.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {

    @Schema(description = "Unique identifier of the supplier (UUID)", example = "d290f1ee-6c54-4b01-90e6-d701748f0851", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id; // <<<--- TIPO ALTERADO PARA UUID

    @Size(max = 200, message = "{supplier.name.size}") // Ajustado para mensagem genérica de tamanho
    @NotBlank(message = "{supplier.name.notblank}")
    @Schema(description = "Legal name of the supplier", example = "Constructora ABC Ltda.")
    private String name;

    @Size(max = 200, message = "{supplier.tradename.size}")
    @Schema(description = "Trading name of the supplier", example = "ABC Construções")
    private String tradeName;

    @Size(max = 50, message = "{supplier.businessid.size}")
    @NotBlank(message = "{supplier.businessid.notblank}")
    @Schema(description = "Business Identification Number (e.g., CNPJ, EIN)", example = "12.345.678/0001-99")
    private String businessIdentificationNumber;

    @NotNull(message = "{supplier.address.notnull}")
    @Valid
    @Schema(description = "Supplier's address details")
    private AddressDTO address;

    @Size(max = 100, message = "{supplier.contactname.size}")
    @Schema(description = "Primary contact person's name", example = "João Silva")
    private String primaryContactName;

    @Size(max = 30, message = "{supplier.contactphone.size}")
    @Schema(description = "Primary contact person's phone number", example = "+55 (11) 98765-4321")
    private String primaryContactPhone;

    @Email(message = "{supplier.contactemail.invalid}")
    @Size(max = 100, message = "{supplier.contactemail.size}")
    @Schema(description = "Primary contact person's email", example = "contato@abcconstrucoes.com.br")
    private String primaryContactEmail;

    @Size(max = 50, message = "{supplier.category.size}")
    @Schema(description = "Supplier category (e.g., MATERIAL, SERVICE)", example = "MATERIAL")
    private String category;

    @Size(max = 100, message = "{supplier.bankname.size}")
    @Schema(description = "Supplier's bank name", example = "Banco XYZ")
    private String bankName;

    @Size(max = 20, message = "{supplier.bankagency.size}")
    @Schema(description = "Supplier's bank agency number", example = "0123")
    private String bankAgency;

    @Size(max = 30, message = "{supplier.bankaccount.size}")
    @Schema(description = "Supplier's bank account number", example = "45678-9")
    private String bankAccount;

    @Schema(description = "List of document references (URLs or identifiers)")
    private List<String> documentReferences;
}