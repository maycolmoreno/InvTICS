package com.uisrael.gestionactivosapi.dominio.entidades;


public class CategoriaEquipos {

	private final int idCategoria;
	private final String nombre;
	private final boolean estado;

	public CategoriaEquipos(int idCategoria, String nombre, boolean estado) {
		this.idCategoria = idCategoria;
		this.nombre = nombre;
		this.estado = estado;
	}

	public int getIdCategoria() {
		return idCategoria;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "CategoriaEquipos [idCategoria=" + idCategoria + ", nombre=" + nombre + ", estado=" + estado + "]";
	}


}
