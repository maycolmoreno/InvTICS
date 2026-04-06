package com.uisrael.gestionactivosapi.dominio.entidades;

import java.util.Date;

public class ActualizacionActivo {

	private int id;
	private int activoId;
	private Date fechaActualizacion;
	private String descripcion;
	private String usuarioActualizacion;

	public ActualizacionActivo() {
	}

	public ActualizacionActivo(int id, int activoId, Date fechaActualizacion, String descripcion,
			String usuarioActualizacion) {
		this.id = id;
		this.activoId = activoId;
		this.fechaActualizacion = fechaActualizacion;
		this.descripcion = descripcion;
		this.usuarioActualizacion = usuarioActualizacion;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getActivoId() {
		return activoId;
	}

	public void setActivoId(int activoId) {
		this.activoId = activoId;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getUsuarioActualizacion() {
		return usuarioActualizacion;
	}

	public void setUsuarioActualizacion(String usuarioActualizacion) {
		this.usuarioActualizacion = usuarioActualizacion;
	}

	@Override
	public String toString() {
		return "ActualizacionActivo [id=" + id + ", activoId=" + activoId + ", fechaActualizacion="
				+ fechaActualizacion + ", descripcion=" + descripcion + ", usuarioActualizacion=" + usuarioActualizacion
				+ "]";
	}
}
