// Path: src/main/java/com/bufalari/supplier/converter/AddressConverter.java
package com.bufalari.supplier.converter;

import com.bufalari.supplier.dto.AddressDTO;
import com.bufalari.supplier.entity.AddressEmbeddable; // Use Embeddable
import org.springframework.stereotype.Component;

/**
 * Converts between AddressEmbeddable and AddressDTO.
 * Converte entre AddressEmbeddable e AddressDTO.
 */
@Component
public class AddressConverter {

    /**
     * Converts AddressEmbeddable entity to AddressDTO.
     * Converte entidade AddressEmbeddable para AddressDTO.
     */
    public AddressDTO entityToDTO(AddressEmbeddable entity) {
        if (entity == null) {
            return null;
        }
        return AddressDTO.builder()
                .street(entity.getStreet())
                .number(entity.getNumber())
                .complement(entity.getComplement())
                .neighbourhood(entity.getNeighbourhood())
                .city(entity.getCity())
                .province(entity.getProvince())
                .postalCode(entity.getPostalCode())
                .country(entity.getCountry())
                .build();
    }

    /**
     * Converts AddressDTO to AddressEmbeddable entity.
     * Converte AddressDTO para entidade AddressEmbeddable.
     */
    public AddressEmbeddable dtoToEntity(AddressDTO dto) {
        if (dto == null) {
            return null;
        }
        AddressEmbeddable entity = new AddressEmbeddable(); // Embeddable usually doesn't have builder
        entity.setStreet(dto.getStreet());
        entity.setNumber(dto.getNumber());
        entity.setComplement(dto.getComplement());
        entity.setNeighbourhood(dto.getNeighbourhood());
        entity.setCity(dto.getCity());
        entity.setProvince(dto.getProvince());
        entity.setPostalCode(dto.getPostalCode());
        entity.setCountry(dto.getCountry());
        return entity;
    }
}