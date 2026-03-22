package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.time.LocalDate;

public class Custodios implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idCustodio;
	private final String nombre;
	private final String cedula;
	private final String correo;
	private final String telefono;
	private final LocalDate fechaIngreso;
	private final boolean estado;

	private Cargos fkCargo;
	private Ubicaciones fkUbicacion;
	private Usuarios fkUsuario;

	public Custodios(int idCustodio, String nombre, String cedula, String correo, String telefono,
			LocalDate fechaIngreso, boolean estado, Cargos fkCargo) {
		this.idCustodio = idCustodio;
		this.nombre = nombre;
		this.cedula = cedula;
		this.correo = correo;
		this.telefono = telefono;
		this.fechaIngreso = fechaIngreso;
		this.estado = estado;
		this.fkCargo = fkCargo;
	}

	public int getIdCustodio() {
		return idCustodio;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCedula() {
		return cedula;
	}

	public String getCorreo() {
		return correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public boolean isEstado() {
		return estado;
	}

	public Departamentos getFkDepartamento() {
		return fkCargo != null ? fkCargo.getFkDepartamento() : null;
	}

	public Cargos getFkCargo() {
		return fkCargo;
	}

	public void setFkCargo(Cargos fkCargo) {
		this.fkCargo = fkCargo;
	}

	public Ubicaciones getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(Ubicaciones fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
	}

	public LocalDate getFechaIngreso() {
		return fechaIngreso;
	}

	public Usuarios getFkUsuario() {
		return fkUsuario;
	}

	public void setFkUsuario(Usuarios fkUsuario) {
		this.fkUsuario = fkUsuario;
	}

	@Override
	public String toString() {
		return "Custodios [idCustodio=" + idCustodio + ", nombre=" + nombre + ", cedula=" + cedula + ", correo="
				+ correo + ", telefono=" + telefono + ", fechaIngreso=" + fechaIngreso + ", estado=" + estado
				+ ", fkCargo=" + fkCargo + ", fkUsuario=" + fkUsuario + "]";
	}


}
