package com.uisrael.gestionactivosapi.dominio.entidades;


public class Marcas {

	private final int idMarca;
	private final String nombre;
	private final boolean estado;

	public Marcas(int idMarca, String nombre, boolean estado) {
		this.idMarca = idMarca;
		this.nombre = nombre;
		this.estado = estado;
	}

	public static Marcas of(int idMarca, String nombre, boolean estado) {
		return new Marcas(idMarca, nombre, estado);
	}

	public int getIdMarca() {
		return idMarca;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public String toString() {
		return "Marcas [idMarca=" + idMarca + ", nombre=" + nombre + ", estado=" + estado + "]";
	}

}
