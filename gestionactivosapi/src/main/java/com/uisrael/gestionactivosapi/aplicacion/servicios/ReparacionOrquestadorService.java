package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientoManualUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.ResultadoTecnico;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoInformeService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.MantenimientoManualComando;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.EnviarConOtRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.inventario.RetornarYCerrarRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

/**
 * Fase C3: coordina en una sola transaccion los flujos que antes el BFF
 * ejecutaba en 2-3 llamadas HTTP encadenadas (con riesgo de inconsistencia
 * parcial). No reimplementa reglas: delega en {@link InventarioService} y
 * {@link IMantenimientoManualUseCase}. Al ser @Transactional con propagacion
 * REQUIRED, si el segundo paso falla se revierte el primero.
 */
@Service
public class ReparacionOrquestadorService {

    private static final String ESTADO_EN_PROCESO = "EN_PROCESO";
    private static final Logger log = LoggerFactory.getLogger(ReparacionOrquestadorService.class);

    private final InventarioService inventarioService;
    private final IMantenimientoManualUseCase mantenimientoService;
    private final MantenimientoInformeService mantenimientoInformeService;

    public ReparacionOrquestadorService(InventarioService inventarioService,
            IMantenimientoManualUseCase mantenimientoService,
            MantenimientoInformeService mantenimientoInformeService) {
        this.inventarioService = inventarioService;
        this.mantenimientoService = mantenimientoService;
        this.mantenimientoInformeService = mantenimientoInformeService;
    }

    /** Crea la OT correctiva y cambia el estado del activo a EN_REPARACION, atomicamente. */
    @Transactional
    public ActivoInventarioResponseDTO enviarConOt(EnviarConOtRequestDTO request, String correoAutenticado) {
        MantenimientoManualComando comando = new MantenimientoManualComando(
                List.of(request.getEquipoId()),
                request.getCustodioId(),
                "CORRECTIVO",
                request.getFechaEnvio() != null ? request.getFechaEnvio() : LocalDate.now(),
                request.getDetalle(),
                "EN_PROCESO",
                request.getProximaFecha(),
                request.getFirmaTecnico(),
                null,   // firmaCustodio
                null,   // ipOrigen
                null,   // actividades (se completan luego)
                null,   // imagenes
                null);  // idProgramado (correctivo, no nace de plan)
        mantenimientoService.crear(comando, correoAutenticado);

        EnviarReparacionRequestDTO envio = new EnviarReparacionRequestDTO();
        envio.setEquipoId(request.getEquipoId());
        envio.setMotivo(request.getMotivo());
        envio.setProveedorTecnico(request.getProveedorTecnico());
        envio.setFechaEnvio(request.getFechaEnvio());
        envio.setObservacion(request.getObservacion());
        return inventarioService.enviarAReparacion(envio);
    }

    /** Retorna el activo a bodega y cierra la OT EN_PROCESO del equipo, atomicamente. */
    @Transactional
    public ActivoInventarioResponseDTO retornarYCerrar(RetornarYCerrarRequestDTO request, String correoAutenticado) {
        ResultadoTecnico resultado = parseResultado(request.getResultadoTecnico());
        if (resultado == null) {
            throw new IllegalArgumentException("Resultado tecnico invalido: " + request.getResultadoTecnico());
        }

        RetornarReparacionRequestDTO retorno = new RetornarReparacionRequestDTO();
        retorno.setEquipoId(request.getEquipoId());
        retorno.setBodegaDestinoId(request.getBodegaDestinoId());
        retorno.setCondicion(request.getCondicion());
        retorno.setFechaRetorno(request.getFechaRetorno());
        retorno.setObservacion(request.getObservacion());
        ActivoInventarioResponseDTO activo = inventarioService.retornarDeReparacion(retorno);

        Integer otId = buscarOtEnProceso(request.getEquipoId());
        if (otId == null) {
            throw new IllegalStateException(
                    "No se encontro una OT en proceso para el equipo " + request.getEquipoId());
        }
        MantenimientoManualResponseDTO cerrada =
                mantenimientoService.cerrar(otId, request.getObservacionCierre(), resultado, correoAutenticado);

        // Informe/correo best-effort: su fallo no debe revertir el cierre ni el retorno.
        try {
            mantenimientoInformeService.generarGuardarYEnviar(cerrada);
        } catch (Exception e) {
            log.error("No se pudo generar/enviar el informe de la OT {} cerrada al retornar: {}",
                    otId, e.getMessage(), e);
        }
        return activo;
    }

    private Integer buscarOtEnProceso(Integer equipoId) {
        List<MantenimientoManualResponseDTO> historial = mantenimientoService.obtenerHistorial(equipoId);
        return historial.stream()
                .filter(ot -> ESTADO_EN_PROCESO.equals(ot.getEstadoInterno()))
                .max(Comparator.comparing(ot -> ot.getCreadoEn() != null ? ot.getCreadoEn() : LocalDateTime.MIN))
                .map(MantenimientoManualResponseDTO::getIdMantenimiento)
                .orElse(null);
    }

    private ResultadoTecnico parseResultado(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return ResultadoTecnico.valueOf(valor.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
