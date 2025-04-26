// Path: src/main/java/com/bufalari/supplier/SupplierServiceApplication.java
package com.bufalari.supplier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing; // Enable auditing here or in a config class

/**
 * Main application class for the Supplier Service.
 * Classe principal da aplicação para o Serviço de Fornecedores.
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.bufalari.supplier.client")
public class SupplierServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SupplierServiceApplication.class, args);
	}

}