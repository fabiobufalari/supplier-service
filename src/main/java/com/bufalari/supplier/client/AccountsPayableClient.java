package com.bufalari.supplier.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam; // Import RequestParam

import java.util.List;

/**
 * Feign client interface for interacting with the Accounts Payable Service.
 * Interface de cliente Feign para interagir com o Serviço de Contas a Pagar.
 */
// Define a name and the URL property for the service
// Define um nome e a propriedade URL para o serviço
@FeignClient(name = "accounts-payable-client", url = "${payable.service.url}")
public interface AccountsPayableClient {

    /**
     * Checks if there are any active (e.g., non-paid, non-canceled) payables for a given supplier.
     * Verifica se existem contas a pagar ativas (ex: não pagas, não canceladas) para um determinado fornecedor.
     *
     * NOTE: The exact endpoint path and parameters depend on the actual implementation of the accounts-payable-service.
     * NOTA: O caminho exato do endpoint e os parâmetros dependem da implementação real do accounts-payable-service.
     *
     * @param supplierId The ID of the supplier to check. / O ID do fornecedor a ser verificado.
     * @param status List of statuses to consider 'inactive' (optional). / Lista de status a serem considerados 'inativos' (opcional).
     * @return true if active payables exist, false otherwise. / true se existirem contas a pagar ativas, false caso contrário.
     */
    @GetMapping(value = "/api/payables/exists-active-by-supplier/{supplierId}") // Example endpoint path / Exemplo de caminho de endpoint
    boolean hasActivePayablesForSupplier(@PathVariable("supplierId") Long supplierId,
                                         @RequestParam(value="excludeStatus", required = false) List<String> excludeStatus); // Example param

    // Default method if the endpoint just returns a boolean based on supplierId
    // Método padrão se o endpoint apenas retorna um booleano baseado no supplierId
    default boolean hasActivePayablesForSupplier(Long supplierId) {
         // By default, check for existence excluding PAID and CANCELED statuses (adjust as needed)
         // Por padrão, verifica a existência excluindo status PAGO e CANCELADO (ajuste conforme necessário)
        return hasActivePayablesForSupplier(supplierId, List.of("PAID", "CANCELED"));
    }
}