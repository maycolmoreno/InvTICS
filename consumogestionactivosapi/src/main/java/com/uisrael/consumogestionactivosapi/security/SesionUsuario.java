package com.uisrael.consumogestionactivosapi.security;

import java.util.Base64;
import java.util.Collections;
import java.util.Set;
import java.nio.charset.StandardCharsets;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 * Datos de autenticación del usuario almacenados en la sesión HTTP.
 * No almacena la contraseña en texto plano; usa un token Basic Auth codificado.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SesionUsuario {

	private String correo;
	private String basicAuthToken;
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

	public void iniciarSesion(String correo, String contrasena, String nombreUsuario, String rol) {
		this.correo = correo;
		this.basicAuthToken = codificarBasicAuth(correo, contrasena);
		this.nombreUsuario = nombreUsuario;
		this.rol = rol;
		this.autenticado = true;
	}

	public void iniciarSesion(String correo, String contrasena, String nombreUsuario, String rol, Integer idUsuario) {
		this.correo = correo;
		this.basicAuthToken = codificarBasicAuth(correo, contrasena);
		this.nombreUsuario = nombreUsuario;
		this.rol = rol;
		this.idUsuario = idUsuario;
		this.autenticado = true;
	}

	public void cerrarSesion() {
		this.correo = null;
		this.basicAuthToken = null;
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
	 * Devuelve el valor del header Authorization (Basic xxxxx) para llamadas al backend.
	 */
	public String getAuthorizationHeader() {
		return basicAuthToken != null ? "Basic " + basicAuthToken : null;
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

	private String codificarBasicAuth(String correo, String contrasena) {
		String credenciales = correo + ":" + contrasena;
		return Base64.getEncoder().encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));
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
