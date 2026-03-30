package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entidad legacy para bienes/activos generales.
 * La tabla «equipos» (EquiposJpa) contiene el detalle específico de equipos TI.
 * Evaluar consolidación en futuras iteraciones.
 */
@Entity
@Table(name = "activos")
public class ActivoJpa {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idActivo;

	@Column(nullable = false)
	private String nombre;

	@Column(columnDefinition = "TEXT")
	private String descripcion;

	@Column(unique = true)
	private String serie;

	private String modelo;

	@Temporal(TemporalType.DATE)
	private Date fechaAdquisicion;

	private Double valorActual;

	private String estado;

	private String ubicacion;

	@Column(name = "fk_departamento")
	private Integer fkDepartamento;

	@Column(name = "fk_categoria")
	private Integer fkCategoria;

	public ActivoJpa() {
	}

	// Getters and Setters
	public Integer getIdActivo() {
		return idActivo;
	}

	public void setIdActivo(Integer idActivo) {
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

	public Integer getFkDepartamento() {
		return fkDepartamento;
	}

	public void setFkDepartamento(Integer fkDepartamento) {
		this.fkDepartamento = fkDepartamento;
	}

	public Integer getFkCategoria() {
		return fkCategoria;
	}

	public void setFkCategoria(Integer fkCategoria) {
		this.fkCategoria = fkCategoria;
	}
}
