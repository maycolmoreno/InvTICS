package com.uisrael.gestionactivosapi.infraestructura.seguridad;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Emision y validacion de tokens JWT para la autenticacion del BFF.
 * La app movil sigue usando Basic Auth; ambos esquemas conviven.
 */
@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access-token:900000}")
    private long accessTokenExpirationMs;

    @Value("${jwt.expiration.refresh-token:604800000}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpirationMs);
    }

    private String generateToken(String username, long expirationMs) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            logger.debug("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Token JWT invalido: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build()
                    .parseSignedClaims(token).getPayload();
            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("No se pudo extraer el usuario del token: {}", e.getMessage());
            return null;
        }
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpirationMs;
    }
}
