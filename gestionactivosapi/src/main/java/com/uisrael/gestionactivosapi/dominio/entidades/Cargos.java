package com.uisrael.gestionactivosapi.dominio.entidades;


public class Cargos {

	private final int idCargo;
	private final String nombre;
	private final boolean estado;

	private Departamentos fkDepartamento;

	public Cargos(int idCargo, String nombre, boolean estado, Departamentos fkDepartamento) {
		this.idCargo = idCargo;
		this.nombre = nombre;
		this.estado = estado;
		this.fkDepartamento = fkDepartamento;
	}

	public int getIdCargo() {
		return idCargo;
	}

	public String getNombre() {
		return nombre;
	}

	public boolean isEstado() {
		return estado;
	}

	public Departamentos getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(Departamentos fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	@Override
	public String toString() {
		return "Cargos [idCargo=" + idCargo + ", nombre=" + nombre + ", estado=" + estado
				+ ", fkDepartamento=" + fkDepartamento + "]";
	}


}
