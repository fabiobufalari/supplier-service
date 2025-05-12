package com.bufalari.supplier.entity;

import com.bufalari.supplier.auditing.AuditableBaseEntity;
import jakarta.persistence.*;
import lombok.*;
// import org.hibernate.annotations.GenericGenerator; // Não mais necessário para GenerationType.UUID

import java.util.ArrayList;
import java.util.List;
import java.util.Objects; // Importar para equals/hashCode
import java.util.UUID;    // <<<--- IMPORT UUID

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
@Table(name = "suppliers", indexes = { // Adicionar índices para campos buscados frequentemente
        @Index(name = "idx_supplier_name", columnList = "name"),
        @Index(name = "idx_supplier_business_id", columnList = "businessIdentificationNumber", unique = true)
})
public class SupplierEntity extends AuditableBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // <<<--- ALTERADO PARA GERAR UUID
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid") // <<<--- Definir columnDefinition
    private UUID id; // <<<--- TIPO ALTERADO PARA UUID

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
    @Column(unique = true, length = 50)
    private String businessIdentificationNumber;

    /**
     * Supplier's address details.
     * Detalhes do endereço do fornecedor.
     */
    @Embedded
    private AddressEmbeddable address;

    /**
     * Name of the primary contact person at the supplier.
     * Nome da pessoa de contato principal no fornecedor.
     */
    @Column(length = 100)
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
    @Column(length = 100)
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
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "supplier_document_references",
                     joinColumns = @JoinColumn(name = "supplier_id", foreignKey = @ForeignKey(name = "fk_suppdocref_supplier"))) // Adicionar FK
    @Column(name = "document_reference", length = 500) // Aumentar tamanho se for URL
    @Builder.Default
    private List<String> documentReferences = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierEntity that = (SupplierEntity) o;
        return Objects.equals(id, that.id); // Compara apenas pelo ID se for uma entidade persistida
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash apenas pelo ID
    }
}