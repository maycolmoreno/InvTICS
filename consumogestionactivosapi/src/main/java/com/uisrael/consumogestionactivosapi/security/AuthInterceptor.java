package com.uisrael.consumogestionactivosapi.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	 private final SesionUsuario sesionUsuario;

	    public AuthInterceptor(SesionUsuario sesionUsuario) {
	        this.sesionUsuario = sesionUsuario;
	    }

	    @Override
	    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
	            throws Exception {

	        // ✅ Evita que el navegador guarde páginas protegidas (soluciona el "atrás")
	        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	        response.setHeader("Pragma", "no-cache");
	        response.setDateHeader("Expires", 0);

	        String uri = request.getRequestURI();

	        // ✅ Permitir el login sin exigir sesión (por si cambias excludes)
	        if (uri.equals("/login") || uri.equals("/") || uri.equals("/logout")) {
	            return true;
	        }

	        // ✅ Bloqueo por sesión
	        if (!sesionUsuario.isAutenticado()) {
	            response.sendRedirect("/login");
	            return false;
	        }

	        return true;
	    }
}
