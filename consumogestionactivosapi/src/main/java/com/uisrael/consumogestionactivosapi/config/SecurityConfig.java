package com.uisrael.consumogestionactivosapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import com.uisrael.consumogestionactivosapi.security.SesionAuthenticationFilter;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final SesionUsuario sesionUsuario;

	public SecurityConfig(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SesionAuthenticationFilter sesionAuthenticationFilter() {
		return new SesionAuthenticationFilter(sesionUsuario);
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
		requestHandler.setCsrfRequestAttributeName("_csrf");

		http
			.csrf(csrf -> csrf
				.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
				.csrfTokenRequestHandler(requestHandler)
				.ignoringRequestMatchers("/api/**")
			)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
			.authorizeHttpRequests(authz -> authz
				// Rutas públicas - sin autenticación requerida
				.requestMatchers("/", "/login", "/setup").permitAll()
				.requestMatchers("/auth/login", "/auth/refresh", "/auth/setup").permitAll()

				// Recursos estáticos
				.requestMatchers("/static/**", "/templates/**", "/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
				
				// Todas las demás rutas requieren autenticación
				.anyRequest().authenticated()
			)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					response.sendRedirect("/login");
				})
			)
			.addFilterBefore(sesionAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
