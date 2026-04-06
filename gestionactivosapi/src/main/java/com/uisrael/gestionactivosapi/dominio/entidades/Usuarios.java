package com.uisrael.gestionactivosapi.dominio.entidades;


public class Usuarios {

	private final int idUsuario;
	private final String nombre;
	private final String cedula;
	private final String correo;
	private final String contrasena;
	private final boolean estado;

	private Departamentos fkDepartamento;
	private Roles fkRol;

	public Usuarios(int idUsuario, String nombre, String cedula, String correo, String contrasena, boolean estado,
			Departamentos fkDepartamento, Roles fkRol) {
		this.idUsuario = idUsuario;
		this.nombre = nombre;
		this.cedula = cedula;
		this.correo = correo;
		this.contrasena = contrasena;
		this.estado = estado;
		this.fkDepartamento = fkDepartamento;
		this.fkRol = fkRol;
	}

	public Departamentos getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(Departamentos fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	public Roles getFkRol() {
		return fkRol;
	}

	public void setFkRol(Roles fkRol) {
		this.fkRol = fkRol;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public String getCorreo() {
		return correo;
	}

	public String getContrasena() {
		return contrasena;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Usuarios [idUsuario=" + idUsuario + ", nombre=" + nombre + ", cedula=" + cedula + ", correo=" + correo
			+ ", contrasena=" + contrasena + ", estado=" + estado + ", fkDepartamento=" + fkDepartamento + ", fkRol="
			+ fkRol + "]";
	}

}
