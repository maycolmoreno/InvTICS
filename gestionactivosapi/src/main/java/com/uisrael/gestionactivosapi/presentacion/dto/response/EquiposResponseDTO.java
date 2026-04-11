package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class EquiposResponseDTO {

	private int idEquipo;
	private String codigoSap;
	private String modelo;
	private String serial;
	private String procesador;

	private Integer memoriaRamGb;
	private Integer capacidadAlmacenamientoGb;

	private Boolean licenciaWindowsActivada;

	private String mac;

	private LocalDate fechaCompra;
	private BigDecimal precioCompra;

	private String estadoEquipo;
	private String observacionEquipo;

	private boolean estado;

	private MarcasResponseDTO fkMarca;
	private CategoriaEquiposResponseDTO fkCategoria;
	private UbicacionesResponseDTO fkUbicacion;

	public int getIdEquipo() {
		return idEquipo;
	}

	public void setIdEquipo(int idEquipo) {
		this.idEquipo = idEquipo;
	}

	public String getCodigoSap() {
		return codigoSap;
	}

	public void setCodigoSap(String codigoSap) {
		this.codigoSap = codigoSap;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getProcesador() {
		return procesador;
	}

	public void setProcesador(String procesador) {
		this.procesador = procesador;
	}

	public Integer getMemoriaRamGb() {
		return memoriaRamGb;
	}

	public void setMemoriaRamGb(Integer memoriaRamGb) {
		this.memoriaRamGb = memoriaRamGb;
	}

	public Integer getCapacidadAlmacenamientoGb() {
		return capacidadAlmacenamientoGb;
	}

	public void setCapacidadAlmacenamientoGb(Integer capacidadAlmacenamientoGb) {
		this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb;
	}

	public Boolean getLicenciaWindowsActivada() {
		return licenciaWindowsActivada;
	}

	public void setLicenciaWindowsActivada(Boolean licenciaWindowsActivada) {
		this.licenciaWindowsActivada = licenciaWindowsActivada;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public LocalDate getFechaCompra() {
		return fechaCompra;
	}

	public void setFechaCompra(LocalDate fechaCompra) {
		this.fechaCompra = fechaCompra;
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public void setPrecioCompra(BigDecimal precioCompra) {
		this.precioCompra = precioCompra;
	}

	public String getEstadoEquipo() {
		return estadoEquipo;
	}

	public void setEstadoEquipo(String estadoEquipo) {
		this.estadoEquipo = estadoEquipo;
	}

	public String getObservacionEquipo() {
		return observacionEquipo;
	}

	public void setObservacionEquipo(String observacionEquipo) {
		this.observacionEquipo = observacionEquipo;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

	public MarcasResponseDTO getFkMarca() {
		return fkMarca;
	}

	public void setFkMarca(MarcasResponseDTO fkMarca) {
		this.fkMarca = fkMarca;
	}

	public CategoriaEquiposResponseDTO getFkCategoria() {
		return fkCategoria;
	}

	public void setFkCategoria(CategoriaEquiposResponseDTO fkCategoria) {
		this.fkCategoria = fkCategoria;
	}

	public UbicacionesResponseDTO getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(UbicacionesResponseDTO fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
	}
}
