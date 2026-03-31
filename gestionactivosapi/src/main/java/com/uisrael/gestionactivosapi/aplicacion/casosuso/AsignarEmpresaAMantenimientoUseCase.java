package com.uisrael.gestionactivosapi.aplicacion.casosuso;

import com.uisrael.gestionactivosapi.dominio.excepciones.EmpresaNoEncontradaException;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EmpresaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: Asignar una empresa a un mantenimiento existente.
 * Valida que tanto la empresa como el mantenimiento existan y que la empresa esté activa.
 */
@RequiredArgsConstructor
public class AsignarEmpresaAMantenimientoUseCase {

    private final EmpresaRepositorioPuerto empresaRepo;
    private final MantenimientoRepositorioPuerto mantenimientoRepo;

    /**
     * Record de entrada para el caso de uso.
     */
    public record Comando(Integer mantenimientoId, Integer empresaId) {}

    /**
     * Ejecuta la asignación de empresa al mantenimiento.
     *
     * @param comando contiene mantenimientoId y empresaId
     * @throws EmpresaNoEncontradaException si la empresa no existe
     * @throws RecursoNoEncontradoException si el mantenimiento no existe
     */
    public void ejecutar(Comando comando) {
        var empresa = empresaRepo.buscarPorId(comando.empresaId())
                .orElseThrow(() -> new EmpresaNoEncontradaException(comando.empresaId()));

        if (!empresa.estaActiva()) {
            throw new com.uisrael.gestionactivosapi.dominio.excepciones.ValidacionNegocioException(
                "La empresa '" + empresa.getNombre() + "' no está activa");
        }

        var mantenimiento = mantenimientoRepo.buscarPorId(comando.mantenimientoId().intValue())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                    "No se encontró el mantenimiento con ID: " + comando.mantenimientoId()));

        mantenimiento.setEmpresaId(comando.empresaId());
        mantenimientoRepo.guardar(mantenimiento);
    }
}
