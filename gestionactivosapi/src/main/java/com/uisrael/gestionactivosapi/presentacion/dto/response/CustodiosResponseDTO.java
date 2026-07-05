package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.time.LocalDate;

public class CustodiosResponseDTO {

	private int idCustodio;
	private String nombre;
	private String cedula;
	private String correo;
	private String telefono;
	private LocalDate fechaIngreso;
	private boolean estado;

	private DepartamentosResponseDTO fkDepartamento;

	private CargosResponseDTO fkCargo;

	private UbicacionesResponseDTO fkUbicacion;

	private String cargoDirectorio;
	private String departamentoDirectorio;

	public String getCargoDirectorio() {
		return cargoDirectorio;
	}

	public void setCargoDirectorio(String cargoDirectorio) {
		this.cargoDirectorio = cargoDirectorio;
	}

	public String getDepartamentoDirectorio() {
		return departamentoDirectorio;
	}

	public void setDepartamentoDirectorio(String departamentoDirectorio) {
		this.departamentoDirectorio = departamentoDirectorio;
	}

	public int getIdCustodio() {
		return idCustodio;
	}

	public void setIdCustodio(int idCustodio) {
		this.idCustodio = idCustodio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public void setFechaIngreso(LocalDate fechaIngreso) {
		this.fechaIngreso = fechaIngreso;
	}

	public DepartamentosResponseDTO getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(DepartamentosResponseDTO fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	public CargosResponseDTO getFkCargo() {
		return fkCargo;
	}

	public void setFkCargo(CargosResponseDTO fkCargo) {
		this.fkCargo = fkCargo;
	}

	public UbicacionesResponseDTO getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(UbicacionesResponseDTO fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
	}
}

