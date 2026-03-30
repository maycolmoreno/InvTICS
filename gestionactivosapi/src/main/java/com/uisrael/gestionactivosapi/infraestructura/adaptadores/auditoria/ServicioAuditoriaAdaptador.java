package com.uisrael.gestionactivosapi.infraestructura.adaptadores.auditoria;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.gestionactivosapi.dominio.puertos.servicios.ServicioAuditoriaPuerto;

/**
 * Adaptador de auditoria basado en logging.
 * Registra acciones y errores en los logs del sistema.
 * Puede ser reemplazado por una implementacion con persistencia en BD.
 */
public class ServicioAuditoriaAdaptador implements ServicioAuditoriaPuerto {

	private static final Logger log = LoggerFactory.getLogger(ServicioAuditoriaAdaptador.class);

	@Override
	public void registrarAccion(Integer usuarioId, String tipoAccion, String entidad,
			String descripcion, Object datosAnteriores, Object datosNuevos) {
		log.info("AUDITORIA [{}] usuario={} entidad={} desc={}",
				tipoAccion, usuarioId, entidad, descripcion);
	}

	@Override
	public void registrarError(Integer usuarioId, String codigoError,
			String descripcionError, String stackTrace) {
		log.error("AUDITORIA_ERROR usuario={} codigo={} desc={}",
				usuarioId, codigoError, descripcionError);
	}

	@Override
	public void registrarAccesoRecurso(Integer usuarioId, String recurso, String tipo) {
		log.info("AUDITORIA_ACCESO usuario={} recurso={} tipo={}", usuarioId, recurso, tipo);
	}

	@Override
	public List<Map<String, Object>> obtenerHistorial(String entidad, Integer entityId) {
		log.warn("obtenerHistorial no implementado con persistencia. Retornando lista vacia.");
		return List.of();
	}

	@Override
	public Integer purgarAuditoriasAntiguas(Integer diasRetencion) {
		log.warn("purgarAuditoriasAntiguas no implementado con persistencia.");
		return 0;
	}
}
