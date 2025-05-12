package com.bufalari.supplier.converter;

import com.bufalari.supplier.dto.SupplierDTO;
import com.bufalari.supplier.entity.SupplierEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.UUID; // Importar UUID se não estiver presente

/**
 * Converts between SupplierEntity and SupplierDTO.
 * Converte entre SupplierEntity e SupplierDTO.
 */
@Component
@RequiredArgsConstructor
public class SupplierConverter {

    private final AddressConverter addressConverter;

    /**
     * Converts SupplierEntity to SupplierDTO.
     * Converte SupplierEntity para SupplierDTO.
     */
    public SupplierDTO entityToDTO(SupplierEntity entity) {
        if (entity == null) {
            return null;
        }
        return SupplierDTO.builder()
                .id(entity.getId()) // ID é UUID
                .name(entity.getName())
                .tradeName(entity.getTradeName())
                .businessIdentificationNumber(entity.getBusinessIdentificationNumber())
                .address(addressConverter.entityToDTO(entity.getAddress()))
                .primaryContactName(entity.getPrimaryContactName())
                .primaryContactPhone(entity.getPrimaryContactPhone())
                .primaryContactEmail(entity.getPrimaryContactEmail())
                .category(entity.getCategory())
                .bankName(entity.getBankName())
                .bankAgency(entity.getBankAgency())
                .bankAccount(entity.getBankAccount())
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
        SupplierEntity entity = SupplierEntity.builder()
                // ID é UUID, não definir aqui para criação, ou usar o ID do DTO para atualização
                .name(dto.getName())
                .tradeName(dto.getTradeName())
                .businessIdentificationNumber(dto.getBusinessIdentificationNumber())
                .address(addressConverter.dtoToEntity(dto.getAddress()))
                .primaryContactName(dto.getPrimaryContactName())
                .primaryContactPhone(dto.getPrimaryContactPhone())
                .primaryContactEmail(dto.getPrimaryContactEmail())
                .category(dto.getCategory())
                .bankName(dto.getBankName())
                .bankAgency(dto.getBankAgency())
                .bankAccount(dto.getBankAccount())
                .documentReferences(dto.getDocumentReferences() != null ? new ArrayList<>(dto.getDocumentReferences()) : new ArrayList<>())
                .build();
        
        // Se o DTO tem um ID (para atualização), defina-o na entidade
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        return entity;
    }
}