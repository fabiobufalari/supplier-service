package com.bufalari.supplier.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestParam; // Removido
// import java.util.List; // Removido

/**
 * Feign client interface for interacting with the Accounts Payable Service.
 * Interface de cliente Feign para interagir com o Serviço de Contas a Pagar.
 */
@FeignClient(name = "accounts-payable-client-supplier", url = "${payable.service.url}") // Nome único, URL correta via application.yml
public interface AccountsPayableClient {

    /**
     * Checks if there are any active (e.g., non-paid, non-canceled) payables for a given supplier.
     * Calls the corresponding endpoint in the accounts-payable-service.
     * Verifica se existem contas a pagar ativas (ex: não pagas, não canceladas) para um determinado fornecedor.
     * Chama o endpoint correspondente no accounts-payable-service.
     *
     * @param supplierId The ID of the supplier to check. / O ID do fornecedor a ser verificado.
     * @return true if active payables exist, false otherwise. / true se existirem contas a pagar ativas, false caso contrário.
     */
    // <<< AJUSTE NO PATH e ASSINATURA >>>
    // Path relativo à URL base (http://localhost:8088/api)
    @GetMapping(value = "/payables/exists-active-by-supplier/{supplierId}")
    boolean hasActivePayablesForSupplier(@PathVariable("supplierId") Long supplierId);

    // Método default removido, pois a chamada direta agora é simples.
}