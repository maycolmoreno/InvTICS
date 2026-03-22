package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;

public class Departamentos implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idDepartamento;
	private final String nombre;
	private final boolean estado;

	private Ubicaciones fkUbicacion;

	public Departamentos(int idDepartamento, String nombre, boolean estado, Ubicaciones fkUbicacion) {
		this.idDepartamento = idDepartamento;
		this.nombre = nombre;
		this.estado = estado;
		this.fkUbicacion = fkUbicacion;
	}

	public Ubicaciones getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(Ubicaciones fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
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
				+ ", fkUbicacion=" + fkUbicacion + "]";
	}

}
