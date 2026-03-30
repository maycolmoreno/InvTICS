package com.uisrael.consumogestionactivosapi.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtTokenProvider tokenProvider;
	private final SesionUsuario sesionUsuario;

	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, SesionUsuario sesionUsuario) {
		this.tokenProvider = tokenProvider;
		this.sesionUsuario = sesionUsuario;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = getJwtFromRequest(request);

			if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
				String username = tokenProvider.getUsernameFromToken(jwt);

				if (username != null) {
					Authentication authentication = new UsernamePasswordAuthenticationToken(
							username, null, null);
					SecurityContextHolder.getContext().setAuthentication(authentication);
					logger.debug("Autenticación JWT establecida para usuario: {}", username);
				}
			} else if (sesionUsuario.isAutenticado()) {
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

	/**
	 * Extrae el token JWT del header Authorization
	 */
	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
