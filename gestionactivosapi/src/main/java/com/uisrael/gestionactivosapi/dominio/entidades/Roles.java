package com.uisrael.gestionactivosapi.dominio.entidades;


public class Roles {

	private final int idRol;
	private final String nombre;
	private final boolean estado;

	public Roles(int idRol, String nombre, boolean estado) {
		this.idRol = idRol;
		this.nombre = nombre;
		this.estado = estado;
	}

	public int getIdRol() {
		return idRol;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Roles [idRol=" + idRol + ", nombre=" + nombre + ", estado=" + estado + "]";
	}


}
