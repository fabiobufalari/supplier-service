// Path: src/main/java/com/bufalari/supplier/auditing/AuditorAwareImpl.java
package com.bufalari.supplier.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;

// NO @Component annotation here / SEM anotação @Component aqui
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // If the filter successfully authenticated, we should have user info
        // Se o filtro autenticou com sucesso, devemos ter info do usuário
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Object principal = authentication.getPrincipal();
            String username;

            if (principal instanceof User) {
               username = ((User) principal).getUsername();
            } else if (principal instanceof String) {
               // Can happen if only username string is set as principal
               // Pode acontecer se apenas a string do username for definida como principal
               username = (String) principal;
            } else {
                 // Should not happen with standard UserDetails, but good to have a fallback
                 // Não deveria acontecer com UserDetails padrão, mas é bom ter um fallback
                 username = "unknown_user";
            }
            return Optional.of(username); // Return the actual username / Retorna o username real
        }

        // Fallback for system processes or unauthenticated access (if allowed by config)
        // Fallback para processos do sistema ou acesso não autenticado (se permitido pela config)
        return Optional.of("system_supplier");
    }
}