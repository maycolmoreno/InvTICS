package com.uisrael.gestionactivosapi.dominio.entidades;

import java.math.BigDecimal;

public class Ubicaciones {

	private final int idUbicacion;
	private final String nombre;
	private final String agencia;
	private final boolean estado;
	private final BigDecimal latitud;
	private final BigDecimal longitud;
	private final String direccion;
	private final String ciudad;
	private final String parroquia;
	private final String provincia;
	private final String linkCoordenada;

	private Departamentos fkDepartamento;
	private Integer idCustodioEncargado;
	private String nombreCustodioEncargado;

	public Ubicaciones(int idUbicacion, String nombre, String agencia, boolean estado,
			BigDecimal latitud, BigDecimal longitud, String direccion, String ciudad,
			String parroquia, String provincia, String linkCoordenada) {
		this.idUbicacion = idUbicacion;
		this.nombre = nombre;
		this.agencia = agencia;
		this.estado = estado;
		this.latitud = latitud;
		this.longitud = longitud;
		this.direccion = direccion;
		this.ciudad = ciudad;
		this.parroquia = parroquia;
		this.provincia = provincia;
		this.linkCoordenada = linkCoordenada;
	}

	public int getIdUbicacion() { return idUbicacion; }
	public String getNombre() { return nombre; }
	public String getAgencia() { return agencia; }
	public boolean isEstado() { return estado; }
	public BigDecimal getLatitud() { return latitud; }
	public BigDecimal getLongitud() { return longitud; }
	public String getDireccion() { return direccion; }
	public String getCiudad() { return ciudad; }
	public String getParroquia() { return parroquia; }
	public String getProvincia() { return provincia; }
	public String getLinkCoordenada() { return linkCoordenada; }

	public Departamentos getFkDepartamento() { return fkDepartamento; }
	public void setFkDepartamento(Departamentos fkDepartamento) { this.fkDepartamento = fkDepartamento; }

	public Integer getIdCustodioEncargado() { return idCustodioEncargado; }
	public void setIdCustodioEncargado(Integer idCustodioEncargado) { this.idCustodioEncargado = idCustodioEncargado; }
	public String getNombreCustodioEncargado() { return nombreCustodioEncargado; }
	public void setNombreCustodioEncargado(String nombreCustodioEncargado) { this.nombreCustodioEncargado = nombreCustodioEncargado; }

	@Override
	public String toString() {
		return "Ubicaciones [idUbicacion=" + idUbicacion + ", nombre=" + nombre + ", agencia=" + agencia
				+ ", estado=" + estado + "]";
	}

}
