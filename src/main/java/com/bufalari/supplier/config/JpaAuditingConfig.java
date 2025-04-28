package com.bufalari.supplier.config; // Pacote correto

import com.bufalari.supplier.auditing.AuditorAwareImpl; // Import correto
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
// <<< AJUSTE: Usar ref específico para o serviço >>>
@EnableJpaAuditing(auditorAwareRef = "auditorProviderSupplier")
public class JpaAuditingConfig {

    @Bean
    // <<< AJUSTE: Renomear o método/nome do bean >>>
    public AuditorAware<String> auditorProviderSupplier() {
        return new AuditorAwareImpl();
    }
}