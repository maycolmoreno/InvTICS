package com.uisrael.gestionactivosapi.dominio.entidades;


public class Departamentos {

	private final int idDepartamento;
	private final String nombre;
	private final boolean estado;

	public Departamentos(int idDepartamento, String nombre, boolean estado) {
		this.idDepartamento = idDepartamento;
		this.nombre = nombre;
		this.estado = estado;
	}

	public int getIdDepartamento() {
		return idDepartamento;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Departamentos [idDepartamento=" + idDepartamento + ", nombre=" + nombre + ", estado=" + estado
				+ "]";
	}

}
