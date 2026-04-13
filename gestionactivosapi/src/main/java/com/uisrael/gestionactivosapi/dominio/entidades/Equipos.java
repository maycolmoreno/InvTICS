package com.uisrael.gestionactivosapi.dominio.entidades;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Equipos {

	private final int idEquipo;
	private final String codigoSap;
	private final String modelo;
	private final String serial;
	private final String procesador;
	private final Integer memoriaRamGb;
	private final Integer capacidadAlmacenamientoGb;
	private final Boolean licenciaWindowsActivada;
	private final String mac;
	private final LocalDate fechaCompra;
	private final BigDecimal precioCompra;
	private final String estadoEquipo;
	private final String observacionEquipo;
	private final boolean estado;
	private final LocalDate fechaAdquisicion;
	private final Double valorActual;
	private final String descripcion;

	private Marcas fkMarca;
	private CategoriaEquipos fkCategoria;

	public Equipos(int idEquipo, String codigoSap, String modelo, String serial, String procesador,
			Integer memoriaRamGb, Integer capacidadAlmacenamientoGb,
			Boolean licenciaWindowsActivada, String mac, LocalDate fechaCompra,
			BigDecimal precioCompra, String estadoEquipo, String observacionEquipo,
			boolean estado, LocalDate fechaAdquisicion, Double valorActual, String descripcion,
			Marcas fkMarca, CategoriaEquipos fkCategoria) {
		this.idEquipo = idEquipo;
		this.codigoSap = codigoSap;
		this.modelo = modelo;
		this.serial = serial;
		this.procesador = procesador;
		this.memoriaRamGb = memoriaRamGb;
		this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb;
		this.licenciaWindowsActivada = licenciaWindowsActivada;
		this.mac = mac;
		this.fechaCompra = fechaCompra;
		this.precioCompra = precioCompra;
		this.estadoEquipo = estadoEquipo;
		this.observacionEquipo = observacionEquipo;
		this.estado = estado;
		this.fechaAdquisicion = fechaAdquisicion;
		this.valorActual = valorActual;
		this.descripcion = descripcion;
		this.fkMarca = fkMarca;
		this.fkCategoria = fkCategoria;
	}

	public int getIdEquipo() {
		return idEquipo;
	}

	public String getCodigoSap() {
		return codigoSap;
	}

	public String getModelo() {
		return modelo;
	}

	public String getSerial() {
		return serial;
	}

	public String getProcesador() {
		return procesador;
	}

	public Integer getMemoriaRamGb() {
		return memoriaRamGb;
	}

	public Integer getCapacidadAlmacenamientoGb() {
		return capacidadAlmacenamientoGb;
	}

	public Boolean getLicenciaWindowsActivada() {
		return licenciaWindowsActivada;
	}

	public String getMac() {
		return mac;
	}

	public LocalDate getFechaCompra() {
		return fechaCompra;
	}

	public BigDecimal getPrecioCompra() {
		return precioCompra;
	}

	public String getEstadoEquipo() {
		return estadoEquipo;
	}

	public String getObservacionEquipo() {
		return observacionEquipo;
	}

	public boolean isEstado() {
		return estado;
	}

	public LocalDate getFechaAdquisicion() {
		return fechaAdquisicion;
	}

	public Double getValorActual() {
		return valorActual;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Marcas getFkMarca() {
		return fkMarca;
	}

	public void setFkMarca(Marcas fkMarca) {
		this.fkMarca = fkMarca;
	}

	public CategoriaEquipos getFkCategoria() {
		return fkCategoria;
	}

	public void setFkCategoria(CategoriaEquipos fkCategoria) {
		this.fkCategoria = fkCategoria;
	}

	@Override
	public String toString() {
		return "Equipos [idEquipo=" + idEquipo + ", codigoSap=" + codigoSap + ", modelo="
				+ modelo + ", serial=" + serial + ", procesador=" + procesador + ", memoriaRamGb=" + memoriaRamGb
				+ ", capacidadAlmacenamientoGb=" + capacidadAlmacenamientoGb
				+ ", licenciaWindowsActivada=" + licenciaWindowsActivada
				+ ", mac=" + mac + ", fechaCompra=" + fechaCompra + ", precioCompra="
				+ precioCompra + ", estadoEquipo=" + estadoEquipo + ", observacionEquipo=" + observacionEquipo
				+ ", fkMarca=" + fkMarca + ", fkCategoria=" + fkCategoria + "]";
	}

}
