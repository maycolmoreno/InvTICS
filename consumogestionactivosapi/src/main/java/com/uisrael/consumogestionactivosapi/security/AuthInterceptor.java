package com.uisrael.consumogestionactivosapi.security;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

	/**
	 * Módulo requerido por prefijo de ruta. Los prefijos más largos se declaran
	 * primero (LinkedHashMap conserva el orden de evaluación). Las rutas sin
	 * entrada solo exigen sesión autenticada.
	 */
	private static final Map<String, String> MODULO_POR_RUTA = new LinkedHashMap<>();
	static {
		MODULO_POR_RUTA.put("/ubicaciones-tecnicos", "GPS_TIEMPO_REAL");
		MODULO_POR_RUTA.put("/categorias-equipo", "CATEGORIAS");
		MODULO_POR_RUTA.put("/mantenimiento", "MANTENIMIENTO");
		MODULO_POR_RUTA.put("/planificacion", "PLANIFICACION");
		MODULO_POR_RUTA.put("/notificaciones", "NOTIFICACIONES");
		MODULO_POR_RUTA.put("/departamentos", "DEPARTAMENTOS");
		MODULO_POR_RUTA.put("/ubicaciones", "UBICACIONES");
		MODULO_POR_RUTA.put("/inventario", "INVENTARIO");
		MODULO_POR_RUTA.put("/custodios", "CUSTODIOS");
		MODULO_POR_RUTA.put("/custodias", "CUSTODIAS");
		MODULO_POR_RUTA.put("/checklist", "CHECKLIST");
		MODULO_POR_RUTA.put("/reportes", "REPORTES");
		MODULO_POR_RUTA.put("/importar", "IMPORTAR");
		MODULO_POR_RUTA.put("/usuarios", "USUARIOS");
		MODULO_POR_RUTA.put("/equipos", "EQUIPOS");
		MODULO_POR_RUTA.put("/activos", "EQUIPOS");
		MODULO_POR_RUTA.put("/visita", "VISITA_TECNICA");
		MODULO_POR_RUTA.put("/marcas", "MARCAS");
		MODULO_POR_RUTA.put("/cargos", "CARGOS");
		MODULO_POR_RUTA.put("/roles", "ROLES");
	}

	private final SesionUsuario sesionUsuario;

	public AuthInterceptor(SesionUsuario sesionUsuario) {
		this.sesionUsuario = sesionUsuario;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// Evita que el navegador guarde páginas protegidas (soluciona el "atrás")
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		String uri = request.getRequestURI();

		// Permitir el login sin exigir sesión (por si cambias excludes)
		if (uri.equals("/login") || uri.equals("/") || uri.equals("/logout")) {
			return true;
		}

		// Bloqueo por sesión
		if (!sesionUsuario.isAutenticado()) {
			response.sendRedirect("/login");
			return false;
		}

		// Bloqueo por módulo: ocultar un enlace no basta, la URL directa
		// también debe respetar los módulos del rol.
		String moduloRequerido = moduloPara(uri);
		if (moduloRequerido != null && !sesionUsuario.tieneModulo(moduloRequerido)) {
			log.warn("Acceso denegado a {} para {} (falta módulo {})",
					uri, sesionUsuario.getCorreo(), moduloRequerido);
			response.sendRedirect("/inicio");
			return false;
		}

		// EQUIPOS es un módulo compartido con la app móvil: el rol TECNICO lo
		// conserva para el móvil, pero en la web su ámbito es solo mantenimiento.
		if (("EQUIPOS".equals(moduloRequerido)) && sesionUsuario.tieneRol("TECNICO")) {
			log.warn("Acceso denegado a {} para {} (EQUIPOS es solo móvil para TECNICO)",
					uri, sesionUsuario.getCorreo());
			response.sendRedirect("/inicio");
			return false;
		}

		return true;
	}

	private static String moduloPara(String uri) {
		for (Map.Entry<String, String> entrada : MODULO_POR_RUTA.entrySet()) {
			if (uri.equals(entrada.getKey()) || uri.startsWith(entrada.getKey() + "/")) {
				return entrada.getValue();
			}
		}
		return null;
	}
}
