// Path: src/main/java/com/bufalari/supplier/config/MessageSourceConfig.java
package com.bufalari.supplier.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource; // Use Reloadable for flexibility
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Implement WebMvcConfigurer if needed for locale interceptor
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver; // Use AcceptHeaderLocaleResolver

import java.util.List;
import java.util.Locale;

/**
 * Configuration for internationalization (i18n) message handling.
 * Configuração para tratamento de mensagens de internacionalização (i18n).
 */
@Configuration
public class MessageSourceConfig implements WebMvcConfigurer { // Implement if using LocaleChangeInterceptor

    /**
     * Configures the MessageSource bean to load messages from property files.
     * Configura o bean MessageSource para carregar mensagens de arquivos de propriedades.
     * @return MessageSource bean. / Bean MessageSource.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // Points to 'messages/messages*.properties' files in the classpath
        // Aponta para arquivos 'messages/messages*.properties' no classpath
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(60); // Cache messages for 1 minute / Cacheia mensagens por 1 minuto
        messageSource.setUseCodeAsDefaultMessage(true); // Use code if message not found / Usa código se mensagem não encontrada
        return messageSource;
    }

    /**
     * Integrates Bean Validation with the MessageSource for internationalized validation messages.
     * Integra o Bean Validation com o MessageSource para mensagens de validação internacionalizadas.
     * @param messageSource The configured MessageSource. / O MessageSource configurado.
     * @return LocalValidatorFactoryBean bean. / Bean LocalValidatorFactoryBean.
     */
    @Bean
    public LocalValidatorFactoryBean getValidator(MessageSource messageSource) {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource);
        return bean;
    }

     /**
     * Configures the LocaleResolver to determine the user's locale, prioritizing the Accept-Language header.
     * Configura o LocaleResolver para determinar o locale do usuário, priorizando o cabeçalho Accept-Language.
     * @return LocaleResolver bean. / Bean LocaleResolver.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        // Set default locale if Accept-Language header is not present or unsupported
        // Define o locale padrão se o cabeçalho Accept-Language não estiver presente ou não for suportado
        localeResolver.setDefaultLocale(Locale.forLanguageTag("en-CA")); // Default to Canadian English
        // Define supported locales / Define locales suportados
        localeResolver.setSupportedLocales(List.of(Locale.forLanguageTag("en-CA"), Locale.forLanguageTag("pt-BR")));
        return localeResolver;
    }

    // Optional: Configure LocaleChangeInterceptor if you want to allow locale changes via request parameter
    // Opcional: Configure LocaleChangeInterceptor se quiser permitir mudanças de locale via parâmetro de requisição
    /*
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // e.g., /api/suppliers?lang=pt-BR
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
    */
}