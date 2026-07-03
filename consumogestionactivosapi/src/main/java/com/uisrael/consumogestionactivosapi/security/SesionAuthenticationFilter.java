package com.uisrael.consumogestionactivosapi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Autentica las peticiones del navegador a partir de la sesión HTTP del BFF.
 * Los tokens JWT del backend viven en {@link SesionUsuario} y solo viajan
 * en las llamadas RestClient hacia la API; el BFF no valida ni emite JWT.
 */
public class SesionAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(SesionAuthenticationFilter.class);

	private final SesionUsuario sesionUsuario;

	public SesionAuthenticationFilter(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			if (sesionUsuario.isAutenticado()) {
				Authentication authentication = new UsernamePasswordAuthenticationToken(
						sesionUsuario.getCorreo(), null, null);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.debug("Autenticación por sesión establecida para usuario: {}", sesionUsuario.getCorreo());
			}
		} catch (Exception ex) {
			logger.error("No se pudo establecer autenticación: {}", ex.getMessage());
		}

		filterChain.doFilter(request, response);
	}
}
