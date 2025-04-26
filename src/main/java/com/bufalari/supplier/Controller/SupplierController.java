// Path: src/main/java/com/bufalari/supplier/Controller/SupplierController.java
package com.bufalari.supplier.Controller;

import com.bufalari.supplier.dto.SupplierDTO;
import com.bufalari.supplier.exception.ResourceNotFoundException; // Make sure this is imported
import com.bufalari.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
// Removed unused import: import java.util.NoSuchElementException;

/**
 * REST controller for managing suppliers. Secured endpoints.
 * Controlador REST para gerenciamento de fornecedores. Endpoints protegidos.
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Endpoints for managing suppliers / Endpoints para gerenciamento de fornecedores")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    // --- CREATE ---
    @Operation(summary = "Create a new supplier", description = "Creates a new supplier record. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Supplier with the same Business ID already exists")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        log.info("Received request to create supplier: {}", supplierDTO.getName());
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSupplier.getId())
                .toUri();
        log.info("Supplier created with ID {} at location {}", createdSupplier.getId(), location);
        return ResponseEntity.created(location).body(createdSupplier);
    }

    // --- READ ---
    @Operation(summary = "Get supplier by ID", description = "Retrieves supplier details. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
         log.debug("Received request to get supplier by ID: {}", id);
         // Service method now throws exception if not found, no need for Optional here
         // O método do serviço agora lança exceção se não encontrar, sem necessidade de Optional aqui
         SupplierDTO supplier = supplierService.getSupplierById(id);
         return ResponseEntity.ok(supplier);
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves a list of all suppliers. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
         log.debug("Received request to get all suppliers");
         List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
         return ResponseEntity.ok(suppliers);
    }

    // --- UPDATE ---
    @Operation(summary = "Update an existing supplier", description = "Updates supplier details. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Supplier with the same Business ID already exists")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
         log.info("Received request to update supplier with ID: {}", id);
         SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
         return ResponseEntity.ok(updatedSupplier);
    }

    // --- DELETE ---
    @Operation(summary = "Delete a supplier", description = "Deletes a supplier. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Conflict - Cannot delete supplier due to dependencies")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
         log.info("Received request to delete supplier with ID: {}", id);
         supplierService.deleteSupplier(id); // Service method handles ResourceNotFound and OperationNotAllowed
         return ResponseEntity.noContent().build();
    }

    // --- DOCUMENT MANAGEMENT ENDPOINTS (STUBS) ---

    @Operation(summary = "Upload supplier document", description = "Uploads a document. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = { /* ... (ApiResponses as before) ... */ })
    @PostMapping(value = "/{supplierId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<String> uploadSupplierDocument(@PathVariable Long supplierId, @RequestParam("file") MultipartFile file) {
        log.info("Received request to upload document for supplier ID: {}", supplierId);
        if (file == null || file.isEmpty()) { // Check for null as well
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty / Arquivo não pode ser vazio");
        }
        // --- STUB ---
        // TODO: Replace with actual call to supplierService.addDocumentReference(supplierId, file);
        String documentReference = "stubbed-doc-ref-" + System.currentTimeMillis();
        log.warn("Document upload STUB called for supplier {}, file '{}'. Reference: {}", supplierId, file.getOriginalFilename(), documentReference);
        // Simulate checking if supplier exists before proceeding (service layer should do this)
        supplierService.getSupplierById(supplierId); // This will throw 404 if supplier doesn't exist
        // --- END STUB ---
        try {
             URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/suppliers/{supplierId}/documents/{docRef}")
                .buildAndExpand(supplierId, documentReference).toUri();
            return ResponseEntity.created(location).body(documentReference);
        } catch (Exception e) {
            log.error("Failed to build location URI for uploaded document for supplier {}: {}", supplierId, e.getMessage(), e);
            // Return 200 OK but log the URI error, as the file *might* have been processed by the stub/service
            return ResponseEntity.ok(documentReference);
        }
    }


    @Operation(summary = "Get supplier document references", description = "Retrieves document references. Requires authentication.")
    @ApiResponses(value = { /* ... (ApiResponses as before) ... */ })
    @GetMapping(value = "/{supplierId}/documents", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getSupplierDocumentReferences(@PathVariable Long supplierId) {
        log.debug("Received request to get document references for supplier ID: {}", supplierId);
        // --- STUB ---
        // TODO: Replace with actual call to supplierService.getDocumentReferences(supplierId);
        SupplierDTO supplier = supplierService.getSupplierById(supplierId); // Ensures supplier exists, throws 404 otherwise
        List<String> references = supplier.getDocumentReferences();
        log.warn("Document references STUB returning references for supplier {}", supplierId);
        // --- END STUB ---
        return ResponseEntity.ok(references); // Returns empty list if no references
    }


    @Operation(summary = "Delete supplier document reference", description = "Removes document association. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = { /* ... (ApiResponses as before) ... */ })
    @DeleteMapping("/{supplierId}/documents/{documentReference}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<Void> deleteSupplierDocumentReference(
            @PathVariable Long supplierId,
            @PathVariable String documentReference) { // documentReference is the ID/URL stored
        log.info("Received request to delete document reference '{}' for supplier ID: {}", documentReference, supplierId);
        // --- STUB ---
        // TODO: Replace with actual call to supplierService.deleteDocumentReference(supplierId, documentReference);
        // Simulate checking if supplier exists
        supplierService.getSupplierById(supplierId); // Throws 404 if supplier not found
        log.warn("Document deletion STUB called for supplier {}, reference {}", supplierId, documentReference);
        // Simulate removing the reference - In real impl, service layer handles this.
        // --- END STUB ---
        return ResponseEntity.noContent().build();
    }
}