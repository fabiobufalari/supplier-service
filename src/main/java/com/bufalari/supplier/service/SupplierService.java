package com.bufalari.supplier.service;

// import com.bufalari.supplier.client.AccountsPayableClient; // Descomentar quando for usar
import com.bufalari.supplier.converter.AddressConverter;
import com.bufalari.supplier.converter.SupplierConverter;
import com.bufalari.supplier.dto.SupplierDTO;
import com.bufalari.supplier.entity.SupplierEntity;
import com.bufalari.supplier.exception.OperationNotAllowedException;
import com.bufalari.supplier.exception.ResourceAlreadyExistsException;
import com.bufalari.supplier.exception.ResourceNotFoundException;
import com.bufalari.supplier.repository.SupplierRepository;
// import feign.FeignException; // Descomentar quando for usar AccountsPayableClient
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID; // <<<--- IMPORT UUID
import java.util.stream.Collectors;

/**
 * Service layer for managing Supplier entities.
 * Handles CRUD operations and business logic for suppliers.
 * Camada de serviço para gerenciamento de entidades Supplier.
 * Trata operações CRUD e lógica de negócio para fornecedores.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;
    private final SupplierConverter supplierConverter;
    private final AddressConverter addressConverter;
    // private final AccountsPayableClient accountsPayableClient; // Para verificação de dependências

    public SupplierDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Attempting to create supplier with Business ID: {}", supplierDTO.getBusinessIdentificationNumber());
        if (supplierDTO.getId() != null) {
            log.warn("ID {} provided for new supplier creation will be ignored.", supplierDTO.getId());
            supplierDTO.setId(null); // Garantir que o ID seja gerado pelo banco/JPA
        }
        if (supplierRepository.existsByBusinessIdentificationNumber(supplierDTO.getBusinessIdentificationNumber())) {
            String errorMessage = "Supplier with Business ID '" + supplierDTO.getBusinessIdentificationNumber() + "' already exists.";
            log.warn("Supplier creation failed: {}", errorMessage);
            throw new ResourceAlreadyExistsException(errorMessage);
        }

        SupplierEntity entity = supplierConverter.dtoToEntity(supplierDTO);
        // O conversor já lida com Address. ID será gerado.
        SupplierEntity savedEntity = supplierRepository.save(entity);
        log.info("Supplier created successfully with ID: {}", savedEntity.getId());
        return supplierConverter.entityToDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(UUID id) { // <<<--- ID é UUID
        log.debug("Fetching supplier by ID: {}", id);
        return supplierRepository.findById(id)
                .map(supplierConverter::entityToDTO)
                .orElseThrow(() -> {
                    String errorMessage = "Supplier not found with ID: " + id;
                    log.warn("Supplier retrieval failed: {}", errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        log.debug("Fetching all suppliers.");
        List<SupplierEntity> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierConverter::entityToDTO)
                .collect(Collectors.toList());
    }

    public SupplierDTO updateSupplier(UUID id, SupplierDTO supplierDTO) { // <<<--- ID é UUID
        log.info("Attempting to update supplier with ID: {}", id);
        SupplierEntity existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Valida se o Business ID está sendo alterado para um que já existe por outro fornecedor
        String newBusinessId = supplierDTO.getBusinessIdentificationNumber();
        if (!Objects.equals(existingSupplier.getBusinessIdentificationNumber(), newBusinessId)) {
            supplierRepository.findByBusinessIdentificationNumber(newBusinessId).ifPresent(otherSupplier -> {
                if (!otherSupplier.getId().equals(existingSupplier.getId())) {
                    String errorMessage = "Supplier with Business ID '" + newBusinessId + "' already exists (ID: " + otherSupplier.getId() + "). Cannot update.";
                    log.warn("Supplier update failed: {}", errorMessage);
                    throw new ResourceAlreadyExistsException(errorMessage);
                }
            });
            existingSupplier.setBusinessIdentificationNumber(newBusinessId);
        }

        existingSupplier.setName(supplierDTO.getName());
        existingSupplier.setTradeName(supplierDTO.getTradeName());
        if (supplierDTO.getAddress() != null) {
             existingSupplier.setAddress(addressConverter.dtoToEntity(supplierDTO.getAddress()));
        } else {
             existingSupplier.setAddress(null); // Permite remover o endereço
        }
        existingSupplier.setPrimaryContactName(supplierDTO.getPrimaryContactName());
        existingSupplier.setPrimaryContactPhone(supplierDTO.getPrimaryContactPhone());
        existingSupplier.setPrimaryContactEmail(supplierDTO.getPrimaryContactEmail());
        existingSupplier.setCategory(supplierDTO.getCategory());
        existingSupplier.setBankName(supplierDTO.getBankName());
        existingSupplier.setBankAgency(supplierDTO.getBankAgency());
        existingSupplier.setBankAccount(supplierDTO.getBankAccount());
        // Garante que a lista de referências de documentos seja uma nova lista (ou vazia)
        existingSupplier.setDocumentReferences(supplierDTO.getDocumentReferences() != null ? new ArrayList<>(supplierDTO.getDocumentReferences()) : new ArrayList<>());

        SupplierEntity updatedEntity = supplierRepository.save(existingSupplier);
        log.info("Supplier updated successfully with ID: {}", id);
        return supplierConverter.entityToDTO(updatedEntity);
    }

    public void deleteSupplier(UUID id) { // <<<--- ID é UUID
        log.info("Attempting to delete supplier with ID: {}", id);
        if (!supplierRepository.existsById(id)) {
             String errorMessage = "Supplier not found with ID: " + id;
            log.warn("Supplier deletion failed: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        // --- Placeholder for Dependency Checks ---
        // boolean hasDependencies = checkForActiveDependencies(id); // Implement this check later
        // if (hasDependencies) {
        //     log.warn("Attempt to delete supplier {} with active dependencies.", id);
        //     throw new OperationNotAllowedException("Cannot delete supplier ID " + id + " due to active dependencies (e.g., active payables).");
        // }
        // --- End of Placeholder ---

        supplierRepository.deleteById(id);
        log.info("Supplier deleted successfully with ID: {}", id);
    }

    // TODO: Implementar lógica de gerenciamento de documentos (upload, get, delete reference)
    // Exemplo de addDocumentReference:
    /*
    public void addDocumentReference(UUID supplierId, String documentReference) {
        SupplierEntity supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + supplierId));
        if (supplier.getDocumentReferences() == null) {
            supplier.setDocumentReferences(new ArrayList<>());
        }
        if (!supplier.getDocumentReferences().contains(documentReference)) {
            supplier.getDocumentReferences().add(documentReference);
            supplierRepository.save(supplier);
            log.info("Added document reference '{}' to supplier ID {}", documentReference, supplierId);
        }
    }
    */

    // Exemplo para checar dependências (adaptar AccountsPayableClient se ele usar UUID para supplierId)
    /*
    private boolean checkForActiveDependencies(UUID supplierId) {
        log.debug("Checking for active dependencies for supplier ID: {}", supplierId);
        try {
            // Se AccountsPayableClient.hasActivePayablesForSupplier esperar Long, é preciso converter UUID para Long.
            // Isso é um problema se não houver mapeamento direto UUID -> Long.
            // O ideal é que accounts-payable-service também use UUID para supplierId.
            // Por agora, vamos assumir que o client espera UUID:
            // return accountsPayableClient.hasActivePayablesForSupplier(supplierId);

            // Se o client AINDA espera Long, esta chamada falhará ou precisará de adaptação:
            // accountsPayableClient.hasActivePayablesForSupplier(supplierId.getMostSignificantBits()); // NÃO FAÇA ISSO, é só um exemplo de como seria problemático

            log.warn("Dependency check with AccountsPayableClient is a STUB. Assuming no dependencies for supplier {}.", supplierId);
            return false; // Placeholder
        } catch (FeignException e) {
             log.error("Failed to check dependencies for supplier ID {} via Feign: Status {}, Message: {}", supplierId, e.status(), e.getMessage(), e);
             throw new RuntimeException("Could not verify supplier dependencies. Deletion aborted.", e);
        } catch (Exception e) {
            log.error("Unexpected error during dependency check for supplier ID {}: {}", supplierId, e.getMessage(), e);
             throw new RuntimeException("Unexpected error during dependency check. Deletion aborted.", e);
        }
    }
    */
}