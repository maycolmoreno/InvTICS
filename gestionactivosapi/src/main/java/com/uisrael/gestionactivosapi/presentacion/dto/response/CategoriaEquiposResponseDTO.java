package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class CategoriaEquiposResponseDTO {

	private int idCategoria;
	private String nombre;
	private boolean estado;

	public int getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(int idCategoria) {
		this.idCategoria = idCategoria;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

}

