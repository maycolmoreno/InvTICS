package com.uisrael.consumogestionactivosapi.security;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utilidad para operaciones de seguridad relacionadas con contraseñas.
 * 
 * Proporciona métodos para:
 * - Hash de contraseñas (BCrypt - estándar de la industria)
 * - Validación de contraseñas
 * - Generación de contraseñas temporales
 * 
 * ⚠️ IMPORTANTE: NUNCA almacenar contraseñas en texto plano o Base64.
 * Usar BCrypt con al menos 10+ rondas de hasheo.
 */
@Component
public class SecurityUtil {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtil.class);
    
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final int TEMP_PASSWORD_LENGTH = 12;

    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGITS = "23456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+";
    private static final String ALL_PASSWORD_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    /**
     * Hashea una contraseña usando BCrypt.
     * 
     * BCrypt es resistente a ataques de fuerza bruta gracias a:
     * - Rondas configurable de hasheo
     * - Salting automático
     * - Tiempo de cálculo deliberadamente alto
     * 
     * @param plainPassword Contraseña en texto plano
     * @return Contraseña hasheada (safe for storage)
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            logger.warn("Intento de hashear contraseña vacía");
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        
        logger.debug("Hasheando contraseña con BCrypt");
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verifica si una contraseña en texto plano coincide con su hash.
     * 
     * Usa comparación timing-safe para evitar timing attacks.
     * 
     * @param plainPassword Contraseña en texto plano (ingresada por usuario)
     * @param hashedPassword Hash almacenado en BD
     * @return true si coinciden, false si no
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        try {
            if (plainPassword == null || hashedPassword == null) {
                logger.warn("Intento de verificar contraseña null");
                return false;
            }
            
            boolean matches = passwordEncoder.matches(plainPassword, hashedPassword);
            
            if (!matches) {
                logger.warn("Contraseña incorrecta");
            }
            
            return matches;
        } catch (Exception e) {
            logger.error("Error validando contraseña: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Genera una contraseña temporal segura.
     * 
     * Requisitos:
     * - 12 caracteres
     * - Mixtura de mayúsculas, minúsculas, números y símbolos
     * - Fácil de leer (evita caracteres confusos como 0/O, 1/I, l/L)
     * 
     * @return Contraseña temporal (segura pero fácil de escribir)
     */
    public static String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder();
        
        // Garantizar al menos 1 de cada tipo
        password.append(randomChar(UPPERCASE));
        password.append(randomChar(LOWERCASE));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));
        
        // Llenar el resto aleatoriamente
        for (int i = 4; i < TEMP_PASSWORD_LENGTH; i++) {
            password.append(randomChar(ALL_PASSWORD_CHARS));
        }
        
        // Barajar (Fisher-Yates)
        char[] chars = password.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int randomIdx = secureRandom.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[randomIdx];
            chars[randomIdx] = temp;
        }
        
        logger.debug("Contraseña temporal generada");
        return new String(chars);
    }

    private static char randomChar(String source) {
        return source.charAt(secureRandom.nextInt(source.length()));
    }

    /**
     * Valida fortaleza de contraseña.
     * 
     * Requisitos mínimos:
     * - Mínimo 8 caracteres
     * - Al menos 1 mayúscula
     * - Al menos 1 minúscula
     * - Al menos 1 número
     * - Al menos 1 carácter especial (opcional pero recomendado)
     * 
     * @param password Contraseña a validar
     * @return true si es fuerte, false si no
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            logger.warn("Contraseña muy corta (< 8 caracteres)");
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()\\-_=+].*");

        boolean isStrong = hasUppercase && hasLowercase && hasNumber && hasSpecial;

        if (!isStrong) {
            logger.warn("Contraseña débil: Upper={}, Lower={}, Digit={}, Special={}", 
                hasUppercase, hasLowercase, hasNumber, hasSpecial);
        }

        return isStrong;
    }

    /**
     * Sanitiza input del usuario para prevenir inyección.
     * 
     * ⚠️ NOTA: Este es un sanitizado básico.
     * Para protección completa, usar prepared statements en la BD.
     * 
     * @param input Input potencialmente malicioso
     * @return Input sanitizado
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input
            .replace("'", "''")          // Escapar apóstrofos para SQL
            .replace("\"", "&quot;")     // Escapar comillas para HTML
            .replace("<", "&lt;")        // Escapar < para HTML
            .replace(">", "&gt;");       // Escapar > para HTML
    }
}
