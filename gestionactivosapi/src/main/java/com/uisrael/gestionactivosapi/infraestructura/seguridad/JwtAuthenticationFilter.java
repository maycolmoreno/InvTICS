package com.uisrael.gestionactivosapi.infraestructura.seguridad;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Autentica peticiones con header "Authorization: Bearer <jwt>".
 * Si no hay Bearer o el token es invalido, la cadena continua y
 * Basic Auth (app movil) sigue funcionando como hasta ahora.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider tokenProvider;
    private final ServicioDetallesUsuario servicioDetallesUsuario;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider,
            ServicioDetallesUsuario servicioDetallesUsuario) {
        this.tokenProvider = tokenProvider;
        this.servicioDetallesUsuario = servicioDetallesUsuario;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String jwt = extraerToken(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String correo = tokenProvider.getUsernameFromToken(jwt);
            if (correo != null) {
                try {
                    UserDetails usuario = servicioDetallesUsuario.loadUserByUsername(correo);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            usuario.getUsername(), null, usuario.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (UsernameNotFoundException e) {
                    logger.debug("Token valido pero usuario no disponible: {}", e.getMessage());
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extraerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
