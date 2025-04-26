// Path: src/main/java/com/bufalari/supplier/entity/SupplierEntity.java
package com.bufalari.supplier.entity;

import com.bufalari.supplier.auditing.AuditableBaseEntity; // Import base entity
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList; // Import ArrayList
import java.util.List;

/**
 * Represents a supplier in the system.
 * Representa um fornecedor no sistema.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "suppliers")
public class SupplierEntity extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Legal name of the supplier company.
     * Razão Social do fornecedor.
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Trading name or common name of the supplier.
     * Nome Fantasia ou nome comum do fornecedor.
     */
    @Column(length = 200)
    private String tradeName;

    /**
     * Unique business identifier (e.g., CNPJ in Brazil, Business Number in Canada).
     * Identificador único de negócio (ex: CNPJ no Brasil, Business Number no Canadá).
     */
    @Column(unique = true, length = 50) // Unique constraint for identification number
    private String businessIdentificationNumber;

    /**
     * Supplier's address details.
     * Detalhes do endereço do fornecedor.
     */
    @Embedded // Embed the address information directly
    private AddressEmbeddable address;

    /**
     * Name of the primary contact person at the supplier.
     * Nome da pessoa de contato principal no fornecedor.
     */
    @Column(length = 100) // Increased length for contact name
    private String primaryContactName;

    /**
     * Phone number of the primary contact person.
     * Número de telefone do contato principal.
     */
    @Column(length = 30)
    private String primaryContactPhone;

    /**
     * Email address of the primary contact person.
     * Endereço de e-mail do contato principal.
     */
    @Column(length = 100)
    private String primaryContactEmail;

    /**
     * Category or type of the supplier (e.g., MATERIAL, SERVICE, EQUIPMENT_RENTAL).
     * Categoria ou tipo do fornecedor (ex: MATERIAL, SERVIÇO, ALUGUEL_EQUIPAMENTO).
     */
    @Column(length = 50)
    private String category;

    /**
     * Name of the supplier's bank.
     * Nome do banco do fornecedor.
     */
    @Column(length = 100) // Increased length for bank name
    private String bankName;

    /**
     * Bank agency number.
     * Número da agência bancária.
     */
    @Column(length = 20)
    private String bankAgency;

    /**
     * Bank account number.
     * Número da conta bancária.
     */
    @Column(length = 30)
    private String bankAccount;

    /**
     * List of references (e.g., IDs or URLs) to documents related to the supplier,
     * stored in a separate document management service.
     * Lista de referências (ex: IDs ou URLs) para documentos relacionados ao fornecedor,
     * armazenados em um serviço de gerenciamento de documentos separado.
     */
    @ElementCollection(fetch = FetchType.LAZY) // Fetch documents only when needed
    @CollectionTable(name = "supplier_document_references", joinColumns = @JoinColumn(name = "supplier_id"))
    @Column(name = "document_reference")
    @Builder.Default // Initialize with an empty list when using Builder
    private List<String> documentReferences = new ArrayList<>(); // Initialize to avoid nulls
}