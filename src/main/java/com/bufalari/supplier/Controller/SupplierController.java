package com.bufalari.supplier.Controller;

import com.bufalari.supplier.dto.SupplierDTO;
// import com.bufalari.supplier.exception.ResourceNotFoundException; // Não é mais capturada diretamente aqui
import com.bufalari.supplier.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; // Para anotações de parâmetro
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
import org.springframework.web.server.ResponseStatusException; // Para exceções HTTP diretas
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID; // <<<--- IMPORT UUID

/**
 * REST controller for managing suppliers. Secured endpoints.
 * Controlador REST para gerenciamento de fornecedores. Endpoints protegidos.
 */
@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "Endpoints for managing suppliers / Endpoints para gerenciamento de fornecedores")
@SecurityRequirement(name = "bearerAuth") // Aplica segurança JWT a todos os endpoints nesta classe
public class SupplierController {

    private static final Logger log = LoggerFactory.getLogger(SupplierController.class);
    private final SupplierService supplierService;

    @Operation(summary = "Create a new supplier", description = "Creates a new supplier record. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supplier created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data (validation error)"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden (insufficient role)"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., supplier with the same Business ID already exists)")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<SupplierDTO> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        log.info("Received request to create supplier: {}", supplierDTO.getName());
        SupplierDTO createdSupplier = supplierService.createSupplier(supplierDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdSupplier.getId()) // ID agora é UUID
                .toUri();
        log.info("Supplier created with ID {} at location {}", createdSupplier.getId(), location);
        return ResponseEntity.created(location).body(createdSupplier);
    }

    @Operation(summary = "Get supplier by ID", description = "Retrieves supplier details. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SupplierDTO> getSupplierById(
            @Parameter(description = "ID of the supplier to retrieve (UUID format)") @PathVariable UUID id) { // <<<--- ID é UUID
         log.debug("Received request to get supplier by ID: {}", id);
         SupplierDTO supplier = supplierService.getSupplierById(id); // Service lança ResourceNotFoundException
         return ResponseEntity.ok(supplier);
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves a list of all suppliers. Requires authentication.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
         log.debug("Received request to get all suppliers");
         List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
         return ResponseEntity.ok(suppliers);
    }

    @Operation(summary = "Update an existing supplier", description = "Updates supplier details. Requires ADMIN, MANAGER or PURCHASING role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supplier updated successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = SupplierDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., Business ID belongs to another supplier)")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<SupplierDTO> updateSupplier(
            @Parameter(description = "ID of the supplier to update (UUID format)") @PathVariable UUID id, // <<<--- ID é UUID
            @Valid @RequestBody SupplierDTO supplierDTO) {
         log.info("Received request to update supplier with ID: {}", id);
         // Garantir que o ID no DTO, se presente, corresponda ao ID do path, ou ignorá-lo.
         if (supplierDTO.getId() != null && !supplierDTO.getId().equals(id)) {
             log.warn("Mismatch between path ID ({}) and DTO ID ({}). Using path ID.", id, supplierDTO.getId());
             // Você pode optar por lançar um Bad Request aqui ou apenas usar o ID do path.
         }
         // O SupplierService usará o 'id' do path para buscar e atualizar.
         SupplierDTO updatedSupplier = supplierService.updateSupplier(id, supplierDTO);
         return ResponseEntity.ok(updatedSupplier);
    }

    @Operation(summary = "Delete a supplier", description = "Deletes a supplier. Requires ADMIN role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (e.g., cannot delete supplier due to active payables)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSupplier(
            @Parameter(description = "ID of the supplier to delete (UUID format)") @PathVariable UUID id) { // <<<--- ID é UUID
         log.info("Received request to delete supplier with ID: {}", id);
         supplierService.deleteSupplier(id); // Service lida com exceções
         return ResponseEntity.noContent().build();
    }


    // --- DOCUMENT MANAGEMENT ENDPOINTS (STUBS - adapt IDs to UUID) ---

    @Operation(summary = "Upload supplier document", description = "Uploads a document and associates it with a supplier. Requires ADMIN, MANAGER or PURCHASING role. (STUB)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Document reference created"),
        @ApiResponse(responseCode = "400", description = "Invalid file or supplier ID"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @PostMapping(value = "/{supplierId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<String> uploadSupplierDocument(
            @Parameter(description = "ID of the supplier (UUID format)") @PathVariable UUID supplierId, // <<<--- ID é UUID
            @RequestParam("file") MultipartFile file) {
        log.info("Received request to upload document for supplier ID: {}", supplierId);
        if (file == null || file.isEmpty()) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File cannot be empty / Arquivo não pode ser vazio");
        }
        // STUB - Chamar supplierService.getSupplierById(supplierId) para verificar se o fornecedor existe.
        supplierService.getSupplierById(supplierId); // Lança 404 se não existir
        String documentReference = "stubbed-doc-ref-for-" + supplierId + "-" + System.currentTimeMillis();
        // supplierService.addDocumentReference(supplierId, documentReference); // Chamada real
        log.warn("Document upload STUB called for supplier {}, file '{}'. Reference: {}", supplierId, file.getOriginalFilename(), documentReference);
        
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/suppliers/{supplierId}/documents/{docRef}") // Assumindo que docRef é único ou um ID
            .buildAndExpand(supplierId, documentReference).toUri();
        return ResponseEntity.created(location).body(documentReference);
    }

    @Operation(summary = "Get supplier document references", description = "Retrieves document references for a supplier. Requires authentication. (STUB)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Document references retrieved"),
        @ApiResponse(responseCode = "404", description = "Supplier not found")
    })
    @GetMapping(value = "/{supplierId}/documents", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<String>> getSupplierDocumentReferences(
            @Parameter(description = "ID of the supplier (UUID format)") @PathVariable UUID supplierId) { // <<<--- ID é UUID
        log.debug("Received request to get document references for supplier ID: {}", supplierId);
        SupplierDTO supplier = supplierService.getSupplierById(supplierId); // Garante que o fornecedor existe
        List<String> references = supplier.getDocumentReferences();
        if (references == null) {
            references = List.of(); // Retorna lista vazia se for nulo
        }
        log.warn("Document references STUB returning {} references for supplier {}", references.size(), supplierId);
        return ResponseEntity.ok(references);
    }

    @Operation(summary = "Delete supplier document reference", description = "Removes a document association from a supplier. Requires ADMIN, MANAGER or PURCHASING role. (STUB)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Document reference deleted"),
        @ApiResponse(responseCode = "404", description = "Supplier or document reference not found")
    })
    @DeleteMapping("/{supplierId}/documents/{documentReference}") // Assumir que documentReference é o ID/URL exato
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'PURCHASING')")
    public ResponseEntity<Void> deleteSupplierDocumentReference(
            @Parameter(description = "ID of the supplier (UUID format)") @PathVariable UUID supplierId, // <<<--- ID é UUID
            @Parameter(description = "The exact document reference to delete") @PathVariable String documentReference) {
        log.info("Received request to delete document reference '{}' for supplier ID: {}", documentReference, supplierId);
        // STUB - Chamar supplierService.getSupplierById(supplierId) para verificar
        supplierService.getSupplierById(supplierId);
        // supplierService.deleteDocumentReference(supplierId, documentReference); // Chamada real
        log.warn("Document deletion STUB called for supplier {}, reference {}", supplierId, documentReference);
        return ResponseEntity.noContent().build();
    }
}