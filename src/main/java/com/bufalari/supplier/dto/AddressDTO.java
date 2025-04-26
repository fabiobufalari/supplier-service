// Path: src/main/java/com/bufalari/supplier/dto/AddressDTO.java
package com.bufalari.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Address data transfer.
 * DTO para transferência de dados de Endereço.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {

    @NotBlank(message = "Street cannot be blank / Rua não pode ser vazia")
    @Size(max = 255)
    private String street;

    @Size(max = 50)
    private String number;

    @Size(max = 100)
    private String complement;

    @Size(max = 100)
    private String neighbourhood;

    @NotBlank(message = "City cannot be blank / Cidade não pode ser vazia")
    @Size(max = 100)
    private String city;

    @NotBlank(message = "Province/State cannot be blank / Província/Estado não pode ser vazio")
    @Size(max = 100)
    private String province;

    @NotBlank(message = "Postal code cannot be blank / Código postal não pode ser vazio")
    @Size(max = 20)
    private String postalCode;

    @NotBlank(message = "Country cannot be blank / País não pode ser vazio")
    @Size(max = 100)
    private String country;
}