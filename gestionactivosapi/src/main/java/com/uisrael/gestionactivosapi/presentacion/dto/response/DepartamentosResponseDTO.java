package com.uisrael.gestionactivosapi.presentacion.dto.response;

public class DepartamentosResponseDTO {

	private int idDepartamento;
	private String nombre;
	private boolean estado;

	private UbicacionesResponseDTO fkUbicacion;

	public int getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(int idDepartamento) {
		this.idDepartamento = idDepartamento;
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

	public UbicacionesResponseDTO getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(UbicacionesResponseDTO fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
	}

}

