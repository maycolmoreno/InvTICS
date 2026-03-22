package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class UsuariosResponseDTO {

	private RolesResponseDTO fkRol;
	private int idUsuario;
	private String nombre;
	private String cedula;
	private String correo;
	private String contrasena;
	private boolean estado;

	private DepartamentosResponseDTO fkDepartamento;

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo() {
		return correo;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public DepartamentosResponseDTO getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(DepartamentosResponseDTO fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	public RolesResponseDTO getFkRol() {
		return fkRol;
	}

	public void setFkRol(RolesResponseDTO fkRol) {
		this.fkRol = fkRol;
	}

}

