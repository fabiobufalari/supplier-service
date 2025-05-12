package com.bufalari.supplier.repository;

import com.bufalari.supplier.entity.SupplierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID; // <<<--- IMPORT UUID

/**
 * Spring Data JPA repository for Supplier entities.
 * Repositório Spring Data JPA para entidades Supplier.
 */
@Repository
public interface SupplierRepository extends JpaRepository<SupplierEntity, UUID> { // <<<--- ALTERADO PARA UUID

    /**
     * Finds a supplier by its unique business identification number.
     * Encontra um fornecedor pelo seu número de identificação de negócio único.
     * @param businessIdentificationNumber The business ID number. / O número de ID de negócio.
     * @return An Optional containing the supplier if found. / Um Optional contendo o fornecedor se encontrado.
     */
    Optional<SupplierEntity> findByBusinessIdentificationNumber(String businessIdentificationNumber);

    /**
     * Checks if a supplier exists with the given business identification number.
     * Verifica se existe um fornecedor com o número de identificação de negócio fornecido.
     * @param businessIdentificationNumber The business ID number. / O número de ID de negócio.
     * @return true if exists, false otherwise. / true se existe, false caso contrário.
     */
    boolean existsByBusinessIdentificationNumber(String businessIdentificationNumber);

    /**
     * Checks if a supplier exists with the given name (case-insensitive check might be useful).
     * Verifica se existe um fornecedor com o nome fornecido (verificação case-insensitive pode ser útil).
     * @param name The supplier name. / O nome do fornecedor.
     * @return true if exists, false otherwise. / true se existe, false caso contrário.
     */
    boolean existsByNameIgnoreCase(String name);
}