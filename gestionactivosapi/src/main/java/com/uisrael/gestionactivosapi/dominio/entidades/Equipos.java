package com.uisrael.gestionactivosapi.dominio.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Equipos implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int idEquipo;
	private final String codigoSap;
	private final String tipoEquipo;
	private final String modelo;
	private final String serial;
	private final String procesador;
	private final Integer memoriaRamGb;
	private final Integer capacidadAlmacenamientoGb;
	private final String sistemaOperativo;
	private final Boolean licenciaWindowsActivada;
	private final Boolean etiquetaActivoFijo;
	private final String tipoLicenciaOffice;
	private final String versionOffice;
	private final Boolean unionDominio;
	private final String ip;
	private final String mac;
	private final LocalDate fechaCompra;
	private final BigDecimal precioCompra;
	private final String estadoEquipo;
	private final String observacionEquipo;
	private final boolean estado;

	// ✅ AGREGA ESTOS 3 (DOMINIO)
	private Marcas fkMarca;
	private CategoriaEquipos fkCategoria;
	private Ubicaciones fkUbicacion;
	public Equipos(int idEquipo, String codigoSap, String tipoEquipo, String modelo, String serial, String procesador,
			Integer memoriaRamGb, Integer capacidadAlmacenamientoGb, String sistemaOperativo,
			Boolean licenciaWindowsActivada, Boolean etiquetaActivoFijo, String tipoLicenciaOffice,
			String versionOffice, Boolean unionDominio, String ip, String mac, LocalDate fechaCompra,
			BigDecimal precioCompra, String estadoEquipo, String observacionEquipo, boolean estado, Marcas fkMarca,
			CategoriaEquipos fkCategoria, Ubicaciones fkUbicacion) {
		this.idEquipo = idEquipo;
		this.codigoSap = codigoSap;
		this.tipoEquipo = tipoEquipo;
		this.modelo = modelo;
		this.serial = serial;
		this.procesador = procesador;
		this.memoriaRamGb = memoriaRamGb;
		this.capacidadAlmacenamientoGb = capacidadAlmacenamientoGb;
		this.sistemaOperativo = sistemaOperativo;
		this.licenciaWindowsActivada = licenciaWindowsActivada;
		this.etiquetaActivoFijo = etiquetaActivoFijo;
		this.tipoLicenciaOffice = tipoLicenciaOffice;
		this.versionOffice = versionOffice;
		this.unionDominio = unionDominio;
		this.ip = ip;
		this.mac = mac;
		this.fechaCompra = fechaCompra;
		this.precioCompra = precioCompra;
		this.estadoEquipo = estadoEquipo;
		this.observacionEquipo = observacionEquipo;
		this.estado = estado;
		this.fkMarca = fkMarca;
		this.fkCategoria = fkCategoria;
		this.fkUbicacion = fkUbicacion;
	}

	public int getIdEquipo() {
		return idEquipo;
	}

	public String getCodigoSap() {
		return codigoSap;
	}

	public String getTipoEquipo() {
		return tipoEquipo;
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

	public String getSistemaOperativo() {
		return sistemaOperativo;
	}

	public Boolean getLicenciaWindowsActivada() {
		return licenciaWindowsActivada;
	}

	public Boolean getEtiquetaActivoFijo() {
		return etiquetaActivoFijo;
	}

	public String getTipoLicenciaOffice() {
		return tipoLicenciaOffice;
	}

	public String getVersionOffice() {
		return versionOffice;
	}

	public Boolean getUnionDominio() {
		return unionDominio;
	}

	public String getIp() {
		return ip;
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

	// ✅ GETTERS/SETTERS NUEVOS
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

	public Ubicaciones getFkUbicacion() {
		return fkUbicacion;
	}

	public void setFkUbicacion(Ubicaciones fkUbicacion) {
		this.fkUbicacion = fkUbicacion;
	}

	@Override
	public String toString() {
		return "Equipos [idEquipo=" + idEquipo + ", codigoSap=" + codigoSap + ", tipoEquipo=" + tipoEquipo + ", modelo="
				+ modelo + ", serial=" + serial + ", procesador=" + procesador + ", memoriaRamGb=" + memoriaRamGb
				+ ", capacidadAlmacenamientoGb=" + capacidadAlmacenamientoGb + ", sistemaOperativo=" + sistemaOperativo
				+ ", licenciaWindowsActivada=" + licenciaWindowsActivada + ", etiquetaActivoFijo=" + etiquetaActivoFijo
				+ ", tipoLicenciaOffice=" + tipoLicenciaOffice + ", versionOffice=" + versionOffice + ", unionDominio="
				+ unionDominio + ", ip=" + ip + ", mac=" + mac + ", fechaCompra=" + fechaCompra + ", precioCompra="
				+ precioCompra + ", estadoEquipo=" + estadoEquipo + ", observacionEquipo=" + observacionEquipo
				+ ", estado=" + estado + ", fkMarca=" + fkMarca + ", fkCategoria=" + fkCategoria + "]";
	}


}
