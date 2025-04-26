// Path: src/main/java/com/bufalari/supplier/service/SupplierService.java
package com.bufalari.supplier.service;

import com.bufalari.supplier.client.AccountsPayableClient; // Import the Feign client (if dependency check is active)
import com.bufalari.supplier.converter.AddressConverter;
import com.bufalari.supplier.converter.SupplierConverter;
import com.bufalari.supplier.dto.SupplierDTO;
import com.bufalari.supplier.entity.SupplierEntity;
import com.bufalari.supplier.exception.OperationNotAllowedException;
import com.bufalari.supplier.exception.ResourceAlreadyExistsException;
import com.bufalari.supplier.exception.ResourceNotFoundException;
import com.bufalari.supplier.repository.SupplierRepository;
import feign.FeignException; // Import FeignException
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service layer for managing Supplier entities.
 * Handles CRUD operations and business logic for suppliers.
 * Camada de serviço para gerenciamento de entidades Supplier.
 * Trata operações CRUD e lógica de negócio para fornecedores.
 */
@Service
@RequiredArgsConstructor // Lombok for constructor injection
@Transactional // Apply transactionality to all public methods by default
public class SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierService.class); // Logger instance

    // Injected dependencies
    private final SupplierRepository supplierRepository;
    private final SupplierConverter supplierConverter;
    private final AddressConverter addressConverter;
    // Inject AccountsPayableClient only if/when dependency check is implemented
    // private final AccountsPayableClient accountsPayableClient;

    /**
     * Creates a new supplier after validating the business ID.
     * Cria um novo fornecedor após validar o ID de negócio.
     *
     * @param supplierDTO DTO containing supplier data.
     * @return The created SupplierDTO.
     * @throws ResourceAlreadyExistsException if a supplier with the same business ID already exists.
     */
    public SupplierDTO createSupplier(SupplierDTO supplierDTO) { // <<<--- MÉTODO CREATE SUPPLIER
        log.info("Attempting to create supplier with Business ID: {}", supplierDTO.getBusinessIdentificationNumber());
        if (supplierRepository.existsByBusinessIdentificationNumber(supplierDTO.getBusinessIdentificationNumber())) {
            String errorMessage = "Supplier with Business ID '" + supplierDTO.getBusinessIdentificationNumber() + "' already exists.";
            log.warn("Supplier creation failed: {}", errorMessage);
            throw new ResourceAlreadyExistsException(errorMessage);
        }

        SupplierEntity entity = supplierConverter.dtoToEntity(supplierDTO);
        // Ensure AddressEmbeddable is set correctly
        if (supplierDTO.getAddress() != null) {
            entity.setAddress(addressConverter.dtoToEntity(supplierDTO.getAddress()));
        } else {
            entity.setAddress(null);
        }

        SupplierEntity savedEntity = supplierRepository.save(entity);
        log.info("Supplier created successfully with ID: {}", savedEntity.getId());
        return supplierConverter.entityToDTO(savedEntity);
    }

    /**
     * Retrieves a supplier by its unique ID.
     * Recupera um fornecedor pelo seu ID único.
     *
     * @param id The ID of the supplier.
     * @return The found SupplierDTO.
     * @throws ResourceNotFoundException if no supplier is found with the given ID.
     */
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) { // <<<--- MÉTODO GET SUPPLIER BY ID
        log.debug("Fetching supplier by ID: {}", id);
        return supplierRepository.findById(id)
                .map(supplierConverter::entityToDTO)
                .orElseThrow(() -> {
                    String errorMessage = "Supplier not found with ID: " + id;
                    log.warn("Supplier retrieval failed: {}", errorMessage);
                    return new ResourceNotFoundException(errorMessage);
                });
    }

    /**
     * Retrieves a list of all suppliers.
     * Recupera uma lista de todos os fornecedores.
     *
     * @return A list of all SupplierDTOs.
     */
    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() { // <<<--- MÉTODO GET ALL SUPPLIERS
        log.debug("Fetching all suppliers.");
        List<SupplierEntity> suppliers = supplierRepository.findAll();
        return suppliers.stream()
                .map(supplierConverter::entityToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing supplier identified by its ID.
     * Atualiza um fornecedor existente identificado pelo seu ID.
     *
     * @param id          The ID of the supplier to update.
     * @param supplierDTO DTO containing updated data.
     * @return The updated SupplierDTO.
     * @throws ResourceNotFoundException      if the supplier is not found.
     * @throws ResourceAlreadyExistsException if the business ID is changed to one that already exists.
     */
    public SupplierDTO updateSupplier(Long id, SupplierDTO supplierDTO) { // <<<--- MÉTODO UPDATE SUPPLIER
        log.info("Attempting to update supplier with ID: {}", id);
        SupplierEntity existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        // Check and update Business ID if necessary
        String newBusinessId = supplierDTO.getBusinessIdentificationNumber();
        if (!Objects.equals(existingSupplier.getBusinessIdentificationNumber(), newBusinessId)) {
            if (supplierRepository.existsByBusinessIdentificationNumber(newBusinessId)) {
                String errorMessage = "Supplier with Business ID '" + newBusinessId + "' already exists.";
                log.warn("Supplier update failed: {}", errorMessage);
                throw new ResourceAlreadyExistsException(errorMessage);
            }
            existingSupplier.setBusinessIdentificationNumber(newBusinessId);
            log.debug("Updated Business ID for supplier {}.", id);
        }

        // Update other fields
        existingSupplier.setName(supplierDTO.getName());
        existingSupplier.setTradeName(supplierDTO.getTradeName());
        if (supplierDTO.getAddress() != null) {
             existingSupplier.setAddress(addressConverter.dtoToEntity(supplierDTO.getAddress()));
        } else {
             existingSupplier.setAddress(null);
        }
        existingSupplier.setPrimaryContactName(supplierDTO.getPrimaryContactName());
        existingSupplier.setPrimaryContactPhone(supplierDTO.getPrimaryContactPhone());
        existingSupplier.setPrimaryContactEmail(supplierDTO.getPrimaryContactEmail());
        existingSupplier.setCategory(supplierDTO.getCategory());
        existingSupplier.setBankName(supplierDTO.getBankName());
        existingSupplier.setBankAgency(supplierDTO.getBankAgency());
        existingSupplier.setBankAccount(supplierDTO.getBankAccount());
        existingSupplier.setDocumentReferences(supplierDTO.getDocumentReferences() != null ? new ArrayList<>(supplierDTO.getDocumentReferences()) : new ArrayList<>());

        SupplierEntity updatedEntity = supplierRepository.save(existingSupplier);
        log.info("Supplier updated successfully with ID: {}", id);
        return supplierConverter.entityToDTO(updatedEntity);
    }

    /**
     * Deletes a supplier by its ID. Placeholder for dependency checks.
     * Deleta um fornecedor pelo seu ID. Espaço reservado para verificação de dependências.
     *
     * @param id The ID of the supplier to delete.
     * @throws ResourceNotFoundException if the supplier is not found.
     * @throws OperationNotAllowedException if the supplier has active dependencies (logic not yet implemented).
     */
    public void deleteSupplier(Long id) { // <<<--- MÉTODO DELETE SUPPLIER
        log.info("Attempting to delete supplier with ID: {}", id);
        if (!supplierRepository.existsById(id)) {
             String errorMessage = "Supplier not found with ID: " + id;
            log.warn("Supplier deletion failed: {}", errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        }

        // --- Placeholder for Dependency Checks ---
        // boolean hasDependencies = checkForActiveDependencies(id); // Implement this check later
        // if (hasDependencies) {
        //     throw new OperationNotAllowedException("Cannot delete supplier ID " + id + " due to active dependencies.");
        // }
        // --- End of Placeholder ---

        supplierRepository.deleteById(id);
        log.info("Supplier deleted successfully with ID: {}", id);
    }

    // Optional: Helper method for dependency checks (implement with Feign client later)
    /*
    private boolean checkForActiveDependencies(Long supplierId) {
        log.debug("Checking for active dependencies for supplier ID: {}", supplierId);
        try {
            // Replace with actual call to accounts-payable-service
            // return accountsPayableClient.hasActivePayablesForSupplier(supplierId);
            return false; // Placeholder - assume no dependencies for now
        } catch (FeignException e) {
             log.error("Failed to check dependencies for supplier ID {} due to Feign exception: Status {}, Message: {}", supplierId, e.status(), e.getMessage(), e);
             // Decide how to handle communication errors - fail safe (prevent deletion) or allow?
             throw new RuntimeException("Could not verify supplier dependencies. Deletion aborted.", e);
        } catch (Exception e) {
            log.error("Unexpected error during dependency check for supplier ID {}: {}", supplierId, e.getMessage(), e);
             throw new RuntimeException("Unexpected error during dependency check. Deletion aborted.", e);
        }
    }
    */
}