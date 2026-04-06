package com.uisrael.gestionactivosapi.presentacion.dto.response;

import java.util.List;

/**
 * DTO genérico para respuestas paginadas.
 * Envuelve cualquier lista de resultados con metadatos de paginación.
 */
public class PaginaResponse<T> {

	private List<T> contenido;
	private int paginaActual;
	private int tamanioPagina;
	private long totalElementos;
	private int totalPaginas;
	private boolean primera;
	private boolean ultima;

	public PaginaResponse() {
	}

	public PaginaResponse(List<T> contenido, int paginaActual, int tamanioPagina,
			long totalElementos, int totalPaginas) {
		this.contenido = contenido;
		this.paginaActual = paginaActual;
		this.tamanioPagina = tamanioPagina;
		this.totalElementos = totalElementos;
		this.totalPaginas = totalPaginas;
		this.primera = paginaActual == 0;
		this.ultima = paginaActual >= totalPaginas - 1;
	}

	public List<T> getContenido() {
		return contenido;
	}

	public void setContenido(List<T> contenido) {
		this.contenido = contenido;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public void setPaginaActual(int paginaActual) {
		this.paginaActual = paginaActual;
	}

	public int getTamanioPagina() {
		return tamanioPagina;
	}

	public void setTamanioPagina(int tamanioPagina) {
		this.tamanioPagina = tamanioPagina;
	}

	public long getTotalElementos() {
		return totalElementos;
	}

	public void setTotalElementos(long totalElementos) {
		this.totalElementos = totalElementos;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(int totalPaginas) {
		this.totalPaginas = totalPaginas;
	}

	public boolean isPrimera() {
		return primera;
	}

	public void setPrimera(boolean primera) {
		this.primera = primera;
	}

	public boolean isUltima() {
		return ultima;
	}

	public void setUltima(boolean ultima) {
		this.ultima = ultima;
	}
}
