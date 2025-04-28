package com.bufalari.supplier.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
// HashMap, Map, Function imports are needed if generating tokens here
import java.util.function.Function;

/**
 * Utility for handling JWT tokens, using a configured secret key.
 * Utilitário para manipulação de tokens JWT, usando uma chave secreta configurada.
 */
@Component
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    // Inject the secret key from application properties using the CORRECT key name
    // Injeta a chave secreta das propriedades da aplicação usando o nome CORRETO da chave
    @Value("${security.jwt.token.secret-key}")
    private String configuredSecretKey;

    private SecretKey secretKey;

    /**
     * Initializes the SecretKey after properties injection.
     * Inicializa a SecretKey após a injeção das propriedades.
     */
    @PostConstruct
    public void init() {
        if (configuredSecretKey == null || configuredSecretKey.isBlank()) {
            log.error("JWT secret key is not configured properly in application properties (jwt.secret).");
            throw new IllegalStateException("JWT secret key must be configured.");
        }
        try {
            this.secretKey = Keys.hmacShaKeyFor(configuredSecretKey.getBytes(StandardCharsets.UTF_8));
            log.info("JWT Secret Key initialized successfully for Supplier Service."); // Adjusted log message
        } catch (Exception e) {
            log.error("Error initializing JWT Secret Key from configured value.", e);
            throw new RuntimeException("Failed to initialize JWT Secret Key", e);
        }
    }

    // ... (Restante da classe JwtUtil permanece igual - extractUsername, extractExpiration, etc.)

    /**
     * Extracts the username (subject) from the token.
     * Extrai o nome de usuário (subject) do token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from the token.
     * Extrai a data de expiração do token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the token using a claims resolver function.
     * Extrai uma claim específica do token usando uma função resolvedora de claims.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parses the token and extracts all claims. Handles potential exceptions.
     * Faz o parse do token e extrai todas as claims. Trata exceções potenciais.
     */
    private Claims extractAllClaims(String token) throws JwtException {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(this.secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.warn("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.warn("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            log.warn("JWT signature validation failed: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("JWT token argument validation failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Checks if the token is expired.
     * Verifica se o token está expirado.
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            log.warn("Could not determine expiration due to other JWT exception: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validates the token against UserDetails (username match and expiration).
     * Valida o token em relação ao UserDetails (correspondência de nome de usuário e expiração).
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            if (userDetails == null) {
                log.warn("UserDetails object provided for validation is null for token subject: {}", username);
                return false;
            }
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException e) {
            return false;
        }
    }

    // Token Generation methods removed as this service only validates
}