package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class CargosResponseDTO {

	private int idCargo;
	private String nombre;
	private boolean estado;

	private DepartamentosResponseDTO fkDepartamento;

	public int getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(int idCargo) {
		this.idCargo = idCargo;
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

	public DepartamentosResponseDTO getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(DepartamentosResponseDTO fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

}

