package com.uisrael.gestionactivosapi.dominio.entidades;


public class Departamentos {

	private final int idDepartamento;
	private final String nombre;
	private final String tipo;
	private final boolean estado;

	public Departamentos(int idDepartamento, String nombre, String tipo, boolean estado) {
		this.idDepartamento = idDepartamento;
		this.nombre = nombre;
		this.tipo = tipo != null ? tipo : "ADMINISTRATIVO";
		this.estado = estado;
	}

	public int getIdDepartamento() {
		return idDepartamento;
	}

	public String getNombre() {
		return nombre;
	}

	public String getTipo() {
		return tipo;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Departamentos [idDepartamento=" + idDepartamento + ", nombre=" + nombre + ", tipo=" + tipo + ", estado=" + estado
				+ "]";
	}

}
