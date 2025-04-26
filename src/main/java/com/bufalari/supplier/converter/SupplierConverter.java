// Path: src/main/java/com/bufalari/supplier/converter/SupplierConverter.java
package com.bufalari.supplier.converter;

import com.bufalari.supplier.dto.SupplierDTO;
import com.bufalari.supplier.entity.SupplierEntity;
import lombok.RequiredArgsConstructor; // Use Lombok for constructor injection
import org.springframework.stereotype.Component;

import java.util.ArrayList; // Import for initializing list

/**
 * Converts between SupplierEntity and SupplierDTO.
 * Converte entre SupplierEntity e SupplierDTO.
 */
@Component
@RequiredArgsConstructor // Automatically creates constructor for final fields
public class SupplierConverter {

    private final AddressConverter addressConverter; // Inject AddressConverter

    /**
     * Converts SupplierEntity to SupplierDTO.
     * Converte SupplierEntity para SupplierDTO.
     */
    public SupplierDTO entityToDTO(SupplierEntity entity) {
        if (entity == null) {
            return null;
        }
        return SupplierDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .tradeName(entity.getTradeName())
                .businessIdentificationNumber(entity.getBusinessIdentificationNumber())
                .address(addressConverter.entityToDTO(entity.getAddress())) // Convert address
                .primaryContactName(entity.getPrimaryContactName())
                .primaryContactPhone(entity.getPrimaryContactPhone())
                .primaryContactEmail(entity.getPrimaryContactEmail())
                .category(entity.getCategory())
                .bankName(entity.getBankName())
                .bankAgency(entity.getBankAgency())
                .bankAccount(entity.getBankAccount())
                // Return a copy of the list or an empty list if null
                // Retorna uma cópia da lista ou uma lista vazia se for nulo
                .documentReferences(entity.getDocumentReferences() != null ? new ArrayList<>(entity.getDocumentReferences()) : new ArrayList<>())
                .build();
    }

    /**
     * Converts SupplierDTO to SupplierEntity.
     * Converte SupplierDTO para SupplierEntity.
     */
    public SupplierEntity dtoToEntity(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }
        return SupplierEntity.builder()
                .id(dto.getId()) // Keep ID for updates / Mantém ID para atualizações
                .name(dto.getName())
                .tradeName(dto.getTradeName())
                .businessIdentificationNumber(dto.getBusinessIdentificationNumber())
                .address(addressConverter.dtoToEntity(dto.getAddress())) // Convert address
                .primaryContactName(dto.getPrimaryContactName())
                .primaryContactPhone(dto.getPrimaryContactPhone())
                .primaryContactEmail(dto.getPrimaryContactEmail())
                .category(dto.getCategory())
                .bankName(dto.getBankName())
                .bankAgency(dto.getBankAgency())
                .bankAccount(dto.getBankAccount())
                 // Initialize with an empty list if DTO list is null
                 // Inicializa com lista vazia se a lista do DTO for nula
                .documentReferences(dto.getDocumentReferences() != null ? new ArrayList<>(dto.getDocumentReferences()) : new ArrayList<>())
                .build();
    }
}