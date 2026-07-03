package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.uisrael.gestionactivosapi.infraestructura.seguridad.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())

			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/setup/**").permitAll()
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/autenticacion/**").permitAll()


				.requestMatchers(HttpMethod.GET, "/api/equipos/**").hasAnyRole("ADMINISTRADOR", "TECNICO", "AUDITOR")
				.requestMatchers(HttpMethod.POST, "/api/equipos/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers(HttpMethod.PUT, "/api/equipos/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers(HttpMethod.DELETE, "/api/equipos/**").hasAnyRole("ADMINISTRADOR", "TECNICO")

				.requestMatchers("/api/marcas/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/categorias-equipo/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/custodias/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/custodios/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/tickets/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/mantenimiento/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/orden/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/notificaciones/**").hasAnyRole("ADMINISTRADOR", "TECNICO")

				// UBICACIONES TECNICOS (GPS)
				.requestMatchers(HttpMethod.GET, "/api/ubicaciones-tecnicos/tiempo-real").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.POST, "/api/ubicaciones-tecnicos/consentimiento").hasRole("TECNICO")
				.requestMatchers(HttpMethod.POST, "/api/ubicaciones-tecnicos").hasRole("TECNICO")

				.requestMatchers("/api/visita/**").hasAnyRole("ADMINISTRADOR", "TECNICO", "AUDITOR")
				.requestMatchers("/api/historial/**").hasAnyRole("ADMINISTRADOR", "TECNICO", "AUDITOR")
				.requestMatchers("/api/actividades-checklist/**").hasAnyRole("ADMINISTRADOR", "TECNICO", "AUDITOR")

				.requestMatchers(HttpMethod.GET, "/api/departamentos/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers(HttpMethod.GET, "/api/ubicaciones/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers(HttpMethod.GET, "/api/cargos/**").hasAnyRole("ADMINISTRADOR", "TECNICO")


				.requestMatchers(HttpMethod.POST, "/api/departamentos/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.PUT, "/api/departamentos/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.DELETE, "/api/departamentos/**").hasRole("ADMINISTRADOR")

				.requestMatchers(HttpMethod.POST, "/api/ubicaciones/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.PUT, "/api/ubicaciones/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.DELETE, "/api/ubicaciones/**").hasRole("ADMINISTRADOR")

				.requestMatchers(HttpMethod.POST, "/api/cargos/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.PUT, "/api/cargos/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.DELETE, "/api/cargos/**").hasRole("ADMINISTRADOR")

				.requestMatchers(HttpMethod.GET, "/api/usuarios/**").hasAnyRole("ADMINISTRADOR", "TECNICO")
				.requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")
				.requestMatchers("/api/sync/**").hasRole("ADMINISTRADOR")
				.requestMatchers("/api/roles/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.GET, "/api/modulos/codigos-por-rol").authenticated()
				.requestMatchers("/api/modulos/**").hasRole("ADMINISTRADOR")


				.requestMatchers(HttpMethod.GET, "/api/reportes/**").hasAnyRole("ADMINISTRADOR", "TECNICO", "AUDITOR")

				.requestMatchers(HttpMethod.POST, "/api/reportes/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.PUT, "/api/reportes/**").hasRole("ADMINISTRADOR")
				.requestMatchers(HttpMethod.DELETE, "/api/reportes/**").hasRole("ADMINISTRADOR")

				.anyRequest().authenticated()
			)

			.httpBasic(basic -> basic.realmName("API Gestión de Activos"))
			// JWT (BFF) convive con Basic Auth (app movil): si hay Bearer valido
			// autentica antes; si no, la cadena sigue y Basic opera igual que antes.
			.addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class);

		return http.build();
	}
}
