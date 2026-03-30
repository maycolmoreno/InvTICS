package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.util.Date;

public class Activo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int idActivo;
	private String nombre;
	private String descripcion;
	private String serie;
	private String modelo;
	private Date fechaAdquisicion;
	private Double valorActual;
	private String estado;
	private String ubicacion;
	private int fkDepartamento;
	private int fkCategoria;

	public Activo() {
	}

	public Activo(int idActivo, String nombre, String descripcion, String serie, String modelo,
			Date fechaAdquisicion, Double valorActual, String estado, String ubicacion,
			int fkDepartamento, int fkCategoria) {
		this.idActivo = idActivo;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.serie = serie;
		this.modelo = modelo;
		this.fechaAdquisicion = fechaAdquisicion;
		this.valorActual = valorActual;
		this.estado = estado;
		this.ubicacion = ubicacion;
		this.fkDepartamento = fkDepartamento;
		this.fkCategoria = fkCategoria;
	}

	public int getIdActivo() {
		return idActivo;
	}

	public void setIdActivo(int idActivo) {
		this.idActivo = idActivo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Date getFechaAdquisicion() {
		return fechaAdquisicion;
	}

	public void setFechaAdquisicion(Date fechaAdquisicion) {
		this.fechaAdquisicion = fechaAdquisicion;
	}

	public Double getValorActual() {
		return valorActual;
	}

	public void setValorActual(Double valorActual) {
		this.valorActual = valorActual;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getUbicacion() {
		return ubicacion;
	}

	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}

	public int getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(int fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	public int getFkCategoria() {
		return fkCategoria;
	}

	public void setFkCategoria(int fkCategoria) {
		this.fkCategoria = fkCategoria;
	}

	@Override
	public String toString() {
		return "Activo [idActivo=" + idActivo + ", nombre=" + nombre + ", descripcion=" + descripcion + ", serie="
				+ serie + ", modelo=" + modelo + ", fechaAdquisicion=" + fechaAdquisicion + ", valorActual="
				+ valorActual + ", estado=" + estado + ", ubicacion=" + ubicacion + ", fkDepartamento="
				+ fkDepartamento + ", fkCategoria=" + fkCategoria + "]";
	}
}
