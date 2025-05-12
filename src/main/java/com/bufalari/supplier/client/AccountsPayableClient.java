package com.bufalari.supplier.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// Não são necessários outros imports aqui

/**
 * Feign client interface for interacting with the Accounts Payable Service.
 * Interface de cliente Feign para interagir com o Serviço de Contas a Pagar.
 */
@FeignClient(name = "accounts-payable-client-supplier", url = "${payable.service.url}")
public interface AccountsPayableClient {

    /**
     * Checks if there are any active (e.g., non-paid, non-canceled) payables for a given supplier.
     * Calls the corresponding endpoint in the accounts-payable-service.
     *
     * @param supplierId The ID of the supplier to check. Assumed to be Long as per previous definition.
     *                   If accounts-payable-service updates supplierId to UUID, this needs to change.
     * @return true if active payables exist, false otherwise.
     */
    @GetMapping(value = "/payables/exists-active-by-supplier/{supplierId}") // Path relativo
    boolean hasActivePayablesForSupplier(@PathVariable("supplierId") Long supplierId); // <<<--- MANTIDO COMO LONG
}