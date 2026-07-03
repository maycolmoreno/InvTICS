package com.uisrael.consumogestionactivosapi.security;

import java.util.Collections;
import java.util.Set;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Datos de autenticación del usuario almacenados en la sesión HTTP.
 * No almacena credenciales: guarda los tokens JWT emitidos por el backend.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SesionUsuario {

	/** Margen antes de la expiración real para refrescar proactivamente. */
	private static final long MARGEN_REFRESH_MS = 60_000;

	private String correo;
	private String accessToken;
	private String refreshToken;
	private long accessTokenExpiraEn;
	private String nombreUsuario;
	private String nombre;
	private String departamento;
	private String rol;
	private Integer idUsuario;
	private boolean autenticado;
	private Set<String> modulosPermitidos;

	public SesionUsuario() {
		this.autenticado = false;
		this.modulosPermitidos = Collections.emptySet();
	}

	public void iniciarSesion(String correo, String nombreUsuario, String rol, Integer idUsuario) {
		this.correo = correo;
		this.nombreUsuario = nombreUsuario;
		this.rol = rol;
		this.idUsuario = idUsuario;
		this.autenticado = true;
	}

	/**
	 * Guarda los tokens emitidos por el backend. En un refresh el backend
	 * puede devolver un nuevo refresh token; si viene null se conserva el actual.
	 */
	public void actualizarTokens(String accessToken, String refreshToken, long expiresInMs) {
		this.accessToken = accessToken;
		if (refreshToken != null && !refreshToken.isBlank()) {
			this.refreshToken = refreshToken;
		}
		this.accessTokenExpiraEn = System.currentTimeMillis() + expiresInMs;
	}

	public void cerrarSesion() {
		this.correo = null;
		this.accessToken = null;
		this.refreshToken = null;
		this.accessTokenExpiraEn = 0;
		this.nombreUsuario = null;
		this.nombre = null;
		this.departamento = null;
		this.rol = null;
		this.idUsuario = null;
		this.autenticado = false;
		this.modulosPermitidos = Collections.emptySet();
	}

	public String getCorreo() {
		return correo;
	}

	/**
	 * Devuelve el valor del header Authorization (Bearer xxxxx) para llamadas al backend.
	 */
	public String getAuthorizationHeader() {
		return accessToken != null ? "Bearer " + accessToken : null;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	/** true si el access token venció o está por vencer dentro del margen. */
	public boolean tokenPorVencer() {
		return accessToken != null
				&& System.currentTimeMillis() >= accessTokenExpiraEn - MARGEN_REFRESH_MS;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getRol() {
		return rol;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public boolean isAutenticado() {
		return autenticado;
	}

	public boolean tieneRol(String rolEsperado) {
		return this.rol != null && this.rol.equals(rolEsperado);
	}

	public boolean tieneAlgunRol(String... roles) {
		if (this.rol == null) {
			return false;
		}
		for (String r : roles) {
			if (this.rol.equals(r)) {
				return true;
			}
		}
		return false;
	}

	public Set<String> getModulosPermitidos() {
		return modulosPermitidos;
	}

	public void setModulosPermitidos(Set<String> modulosPermitidos) {
		this.modulosPermitidos = modulosPermitidos != null ? modulosPermitidos : Collections.emptySet();
	}

	public boolean tieneModulo(String codigoModulo) {
		return this.modulosPermitidos != null && this.modulosPermitidos.contains(codigoModulo);
	}

	public boolean tieneAlgunModulo(String... modulos) {
		if (this.modulosPermitidos == null) return false;
		for (String m : modulos) {
			if (this.modulosPermitidos.contains(m)) return true;
		}
		return false;
	}
}
