package com.uisrael.consumogestionactivosapi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration.access-token:900000}")
	private long accessTokenExpirationMs;

	@Value("${jwt.expiration.refresh-token:604800000}")
	private long refreshTokenExpirationMs;

	/**
	 * Genera un token de acceso con duración corta (15 minutos por defecto)
	 */
	public String generateAccessToken(String username) {
		return generateToken(username, accessTokenExpirationMs);
	}

	/**
	 * Genera un token de refresco con duración larga (7 días por defecto)
	 */
	public String generateRefreshToken(String username) {
		return generateToken(username, refreshTokenExpirationMs);
	}

	/**
	 * Genera un token JWT con el username y duración especificada
	 */
	private String generateToken(String username, long expirationMs) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Date now = new Date();
			Date expiryDate = new Date(now.getTime() + expirationMs);

			return Jwts.builder()
					.subject(username)
					.issuedAt(now)
					.expiration(expiryDate)
					.signWith(key)
					.compact();
		} catch (Exception e) {
			logger.error("Error al generar token JWT", e);
			throw new RuntimeException("No se pudo generar el token JWT", e);
		}
	}

	/**
	 * Valida un token JWT
	 */
	public boolean validateToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (ExpiredJwtException e) {
			logger.warn("Token JWT expirado: {}", e.getMessage());
			return false;
		} catch (JwtException | IllegalArgumentException e) {
			logger.warn("Token JWT inválido: {}", e.getMessage());
			return false;
		}
	}

	/**
	 * Extrae el username del token JWT
	 */
	public String getUsernameFromToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();
			return claims.getSubject();
		} catch (JwtException e) {
			logger.error("Error al extraer username del token: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Obtiene la fecha de expiración del token
	 */
	public Date getExpirationFromToken(String token) {
		try {
			SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
			Claims claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();
			return claims.getExpiration();
		} catch (JwtException e) {
			logger.error("Error al extraer expiración del token: {}", e.getMessage());
			return null;
		}
	}

	/**
	 * Obtiene el tiempo de expiración en milisegundos
	 */
	public long getAccessTokenExpiration() {
		return accessTokenExpirationMs;
	}

	public long getRefreshTokenExpiration() {
		return refreshTokenExpirationMs;
	}
}
