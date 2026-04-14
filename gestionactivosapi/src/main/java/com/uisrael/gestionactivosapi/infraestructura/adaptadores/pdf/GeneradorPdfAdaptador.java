package com.uisrael.gestionactivosapi.infraestructura.adaptadores.pdf;

import java.util.Map;

import com.uisrael.gestionactivosapi.infraestructura.servicios.PdfMantenimientoService;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.GeneradorPdfPuerto;

/**
 * Adaptador que implementa GeneradorPdfPuerto delegando la generacion
 * de PDF al servicio existente PdfMantenimientoService.
 * 
 * Los metodos que requieren resolucion de entidades (generarReporteMantenimiento,
 * generarEtiquetaEquipo) seran implementados completamente cuando la capa de
 * aplicacion se refactorice para usar puertos (Fase 4).
 */
public class GeneradorPdfAdaptador implements GeneradorPdfPuerto {

	private final PdfMantenimientoService pdfService;

	public GeneradorPdfAdaptador(PdfMantenimientoService pdfService) {
		this.pdfService = pdfService;
	}

	@Override
	public byte[] generarReporteMantenimiento(Integer mantenimientoId) {
		throw new UnsupportedOperationException(
				"generarReporteMantenimiento por ID requiere resolucion de entidades. "
						+ "Use PdfMantenimientoService.generarInforme() directamente hasta Fase 4.");
	}

	@Override
	public byte[] generarEtiquetaEquipo(Integer equipoId) {
		throw new UnsupportedOperationException(
				"generarEtiquetaEquipo no esta implementado aun.");
	}

	@Override
	public byte[] generarReporteConImagenes(Map<String, Object> datos) {
		throw new UnsupportedOperationException(
				"generarReporteConImagenes no esta implementado aun.");
	}

	@Override
	public boolean isDisponible() {
		return pdfService != null;
	}
}
