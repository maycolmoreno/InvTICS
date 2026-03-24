# SECURITY_IMPROVEMENT_PLAN.md

## Plan de Mejora de Seguridad - consumogestionactivosapi

**Versión:** 1.0  
**Fecha:** 2026-03-24  
**Estado:** Documentación (Implementación Futura)

---

## 1. Evaluación Actual

### Problemas Identificados

#### 🔴 **Basic Authentication (INSEGURO)**
- **Estado Actual:** Uso de Basic Auth (credenciales en Base64 en cada request)
- **Riesgo:** Las credenciales viajan en CADA petición HTTP
- **Impacto:** 
  - Exposición de contraseñas si no se usa HTTPS
  - Difícil de revocar sin cambiar contraseña
  - No hay expiración de sesión

#### 🔴 **Sin Control de Acceso (Sin Autorización)**
- Todos los endpoints públicos
- No hay validación de roles/permisos
- No hay diferenciación entre admin/usuario

#### 🟡 **Validación Insuficiente**
- No hay validación centralizada (@Valid)
- Inputs del usuario sin sanitizar
- Riesgo de inyección SQL

#### 🟡 **Logging de Seguridad Deficiente**
- Intentos de acceso no registrados
- Errores de autenticación no auditados

---

## 2. Recomendaciones Técnicas

### Opción A: JWT (Recomendada para APIs REST)

**Ventajas:**
- ✅ Stateless (mejor escalabilidad)
- ✅ Pueden expirar automáticamente
- ✅ Más seguro que Basic Auth
- ✅ Mejor para aplicaciones móviles

**Desventajas:**
- ❌ Más complejo de implementar
- ❌ Requiere logout manual en servidor

**Tokens:**
```
ACCESO:
   - Corta duración (15-30 min)
   - Valida en cada request

REFRESH:
   - Larga duración (7-30 días)
   - Se usa para obtener nuevo token de acceso
   - Almacenado seguro en servidor
```

### Opción B: OAuth 2.0 (Recomendada para Empresas)

**Ventajas:**
- ✅ Estándar industrial
- ✅ Delegación de autenticación
- ✅ Integración con Google, Microsoft, etc.
- ✅ Muy seguro

**Desventajas:**
- ❌ Más complejo de implementar
- ❌ Requiere servidor adicional

### Opción C: Session-based (NO recomendada para SPAs/Móviles)

**Ventajas:**
- ✅ Simple de implementar
- ✅ Stateful (mejor control)

**Desventajas:**
- ❌ Menos escalable
- ❌ No funciona bien con aplicaciones distribuidas

---

## 3. Plan B: Implementar JWT Refresh Rotation

### Fase 1: Agregación de Dependencias

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>

<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Fase 2: Crear Servicio de JWT

```java
// src/main/java/com/uisrael/consumogestionactivosapi/security/JwtTokenProvider.java

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration.access-token:900000}") // 15 minutos
    private long accessTokenExpiration;

    @Value("${jwt.expiration.refresh-token:604800000}") // 7 días
    private long refreshTokenExpiration;

    // Generar token de acceso
    public String generateAccessToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Generar token de refresh
    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Validar token
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    // Extraer usuario del token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
```

### Fase 3: Aplicar Seguridad a Controladores

```java
// src/main/java/com/uisrael/consumogestionactivosapi/config/SecurityConfig.java

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/auth/login", "/auth/refresh").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }
}
```

### Fase 4: Endpoint de Login

```java
// En AuthControlador

@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginRequest request) {
    // 1. Validar credenciales contra BD
    // 2. Generar JWT access token
    // 3. Generar JWT refresh token (guardar en BD)
    // 4. Retornar ambos tokens
    
    Map<String, String> response = new HashMap<>();
    response.put("accessToken", accessToken);
    response.put("refreshToken", refreshToken);
    response.put("type", "Bearer");
    response.put("expiresIn", "900000");
    
    return ResponseEntity.ok(response);
}

@PostMapping("/refresh")
public ResponseEntity<?> refresh(@RequestHeader("Authorization") String token) {
    // 1. Validar refresh token
    // 2. Generar nuevo access token
    // 3. Retornar token
    
    return ResponseEntity.ok(newAccessToken);
}
```

### Fase 5: Configuración en application.yml

```yaml
jwt:
  secret: your-super-secret-key-change-in-production
  expiration:
    access-token: 900000      # 15 minutos
    refresh-token: 604800000  # 7 días
```

---

## 4. Pasos de Implementación (Orden Recomendado)

| # | Fase | Duración | Complejidad |
|---|------|----------|-------------|
| 1 | Agregar dependencias JWT y Spring Security | 0.5h | ⚠️ Baja |
| 2 | Crear JwtTokenProvider | 1.5h | ⚠️ Baja |
| 3 | Crear SecurityConfig | 1h | ⚠️ Baja |
| 4 | Implementar endpoints /login /refresh | 1.5h | 🔴 Media |
| 5 | Crear tabla de tokens recibidos (blacklist) | 1h | 🔴 Media |
| 6 | Agregar autenticación a controladores | 2h | 🟡 Alta |
| 7 | Pruebas unitarias e integración | 2h | 🟡 Alta |
| 8 | Documentación de API (Swagger) | 1h | ⚠️ Baja |

**Total Estimado:** 10 horas

---

## 5. Mejoras Adicionales (Futuras)

### 5.1 Protección CSRF
```java
// En SecurityConfig
http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
```

### 5.2 Rate Limiting
```xml
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-spring-boot-starter</artifactId>
    <version>7.6.0</version>
</dependency>
```

### 5.3 Hashing de Contraseñas (IMPORTANTE)
```java
// En lugar de guardar contraseñas en texto plano:
@Component
public class PasswordHasher {
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public boolean checkPassword(String password, String hash) {
        return BCrypt.checkpw(password, hash);
    }
}
```

### 5.4 CORS Configuración
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        // ... más configuración
    }
}
```

---

## 6. Checklist de Seguridad

- [ ] Cambiar de Basic Auth a JWT
- [ ] Implementar refresh token rotation
- [ ] Agregar validación @Valid en DTOs
- [ ] Implementar autorización por roles
- [ ] Crear endpoint de logout (blacklist de tokens)
- [ ] HTTPS en producción
- [ ] Keys de JWT en variables de entorno
- [ ] Auditoría de acceso (logs)
- [ ] Rate limiting
- [ ] CORS configurado correctamente
- [ ] Documentación en Swagger/OpenAPI

---

## 7. Referencias

- [JWT Introduction](https://jwt.io/)
- [JJWT Library](https://github.com/jwtk/jjwt)
- [Spring Security Guide](https://spring.io/projects/spring-security)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)

---

**Próximos Pasos:**
1. Review este plan en equipo
2. Decidir entre JWT vs OAuth2
3. Crear rama feature/security-jwt
4. Implementar fases 1-3 ("quick wins")
5. Testing exhaustivo
6. Deploy a staging
7. Validar en producción
