package com.uisrael.gestionactivosapi.aplicacion.casosuso;

import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: Obtener la frecuencia en días de un mantenimiento.
 *
 * <p>
 * <b>Decisión de diseño:</b> La columna {@code frecuencia_dias} fue eliminada
 * de {@code mantenimientos} porque era redundante con
 * {@code mantenimientos_programados.frecuencia_dias}. La frecuencia se resuelve
 * así:
 * </p>
 * <ul>
 * <li>Si el mantenimiento tiene {@code fk_programado} → se obtiene del
 * programado.</li>
 * <li>Si es manual (sin fk_programado) → retorna {@code Optional.empty()} (no
 * tiene frecuencia).</li>
 * </ul>
 */
@RequiredArgsConstructor
public class ObtenerFrecuenciaMantenimientoUseCase {

	private final IMantenimientosJpaRepositorio mantenimientoRepo;

	/**
	 * Resultado del caso de uso.
	 *
	 * @param frecuenciaDias días entre mantenimientos, null si es manual
	 * @param esProgramado   indica si el mantenimiento tiene un programado asociado
	 */
	public record Resultado(Integer frecuenciaDias, boolean esProgramado) {
	}

	/**
	 * Obtiene la frecuencia del mantenimiento resolviendo desde el programado
	 * asociado.
	 *
	 * @param idMantenimiento ID del mantenimiento
	 * @return resultado con frecuencia si es programado, empty-frequency si es
	 *         manual
	 * @throws RecursoNoEncontradoException si el mantenimiento no existe
	 */
	public Resultado ejecutar(Integer idMantenimiento) {
		MantenimientosJpa mantenimiento = mantenimientoRepo.findById(idMantenimiento).orElseThrow(
				() -> new RecursoNoEncontradoException("No se encontró el mantenimiento con ID: " + idMantenimiento));

		if (mantenimiento.getFkProgramado() != null && mantenimiento.getProgramadoRel() != null) {
			return new Resultado(mantenimiento.getProgramadoRel().getFrecuenciaDias(), true);
		}

		return new Resultado(null, false);
	}
}
