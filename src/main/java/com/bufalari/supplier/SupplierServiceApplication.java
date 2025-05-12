package com.bufalari.supplier;

import io.swagger.v3.oas.annotations.OpenAPIDefinition; // Para Swagger
import io.swagger.v3.oas.annotations.info.Info;         // Para Swagger
import io.swagger.v3.oas.annotations.servers.Server;     // Para Swagger
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
// import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Auditoria já está em JpaAuditingConfig

/**
 * Main application class for the Supplier Service.
 * Classe principal da aplicação para o Serviço de Fornecedores.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.bufalari.supplier.client")
// A anotação @EnableJpaAuditing está na classe JpaAuditingConfig, o que é uma boa prática.
// Se não estivesse lá, seria adicionada aqui.
@OpenAPIDefinition( // Adicionar para documentação Swagger/OpenAPI
    info = @Info(
        title = "Supplier Service API",
        version = "v1.0",
        description = "API for managing suppliers and their related information."
    ),
    servers = { @Server(url = "/", description = "Default Server URL") }
)
public class SupplierServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplierServiceApplication.class, args);
	}

}