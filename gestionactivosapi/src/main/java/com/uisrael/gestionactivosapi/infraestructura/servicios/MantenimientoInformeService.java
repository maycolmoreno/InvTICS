package com.uisrael.gestionactivosapi.infraestructura.servicios;

import java.nio.file.Path;
import java.nio.file.InvalidPathException;
import java.util.List;
import java.util.Objects;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

public class MantenimientoInformeService {

    private final EquipoRepositorioPuerto equiposRepo;
    private final CustodioRepositorioPuerto custodiosRepo;
    private final UsuarioRepositorioPuerto usuariosRepo;
    private final PdfMantenimientoService pdfMantenimientoService;
    private final MantenimientoArchivoService archivoService;
    private final CorreoMantenimientoService correoService;

    public MantenimientoInformeService(EquipoRepositorioPuerto equiposRepo,
            CustodioRepositorioPuerto custodiosRepo,
            UsuarioRepositorioPuerto usuariosRepo,
            PdfMantenimientoService pdfMantenimientoService,
            MantenimientoArchivoService archivoService,
            CorreoMantenimientoService correoService) {
        this.equiposRepo = equiposRepo;
        this.custodiosRepo = custodiosRepo;
        this.usuariosRepo = usuariosRepo;
        this.pdfMantenimientoService = pdfMantenimientoService;
        this.archivoService = archivoService;
        this.correoService = correoService;
    }

    public void generarGuardarYEnviar(MantenimientoManualResponseDTO mantenimiento) {
        byte[] pdfBytes = generarYGuardarPdf(mantenimiento);
        String destinatario = correoDestino(mantenimiento);
        correoService.enviarInformeMantenimientoConPdf(
                destinatario,
                mantenimiento.getCustodioNombre(),
                String.valueOf(mantenimiento.getIdMantenimiento()),
                pdfBytes,
                mantenimiento.getFechaMantenimiento(),
                mantenimiento.getTipoMantenimiento(),
                resumenCorreo(mantenimiento));
    }

    public void reenviar(MantenimientoManualResponseDTO mantenimiento) {
        generarGuardarYEnviar(mantenimiento);
    }

    public byte[] obtenerOGenerarPdf(MantenimientoManualResponseDTO mantenimiento) {
        if (archivoService.existePdf(mantenimiento.getIdMantenimiento())) {
            return archivoService.leerPdf(mantenimiento.getIdMantenimiento());
        }
        return generarYGuardarPdf(mantenimiento);
    }

    private byte[] generarYGuardarPdf(MantenimientoManualResponseDTO mantenimiento) {
        Equipos equipo = mantenimiento.getEquipoId() == null
                ? null
                : equiposRepo.obtenerPorId(mantenimiento.getEquipoId()).orElse(null);
        Custodios custodio = mantenimiento.getCustodioId() == null
                ? null
                : custodiosRepo.obtenerPorId(mantenimiento.getCustodioId()).orElse(null);
        Usuarios tecnico = mantenimiento.getTecnicoId() == null
                ? null
                : usuariosRepo.obtenerPorId(mantenimiento.getTecnicoId()).orElse(null);
        List<Path> rutasImagenes = mantenimiento.getImagenes() == null
                ? List.of()
                : mantenimiento.getImagenes().stream()
                        .map(item -> item.getRutaArchivo())
                        .filter(path -> path != null && !path.isBlank())
                        .map(this::pathSeguro)
                        .filter(Objects::nonNull)
                        .toList();

        byte[] pdfBytes = pdfMantenimientoService.generarInforme(
                mantenimiento,
                equipo,
                custodio,
                tecnico,
                rutasImagenes);
        archivoService.guardarPdf(mantenimiento.getIdMantenimiento(), pdfBytes);
        return pdfBytes;
    }

    private String correoDestino(MantenimientoManualResponseDTO mantenimiento) {
        if (mantenimiento.getCustodioCorreo() != null && !mantenimiento.getCustodioCorreo().isBlank()) {
            return mantenimiento.getCustodioCorreo();
        }
        if (mantenimiento.getCustodioId() == null) {
            return null;
        }
        return custodiosRepo.obtenerPorId(mantenimiento.getCustodioId())
                .map(Custodios::getCorreo)
                .orElse(null);
    }

    private Path pathSeguro(String rawPath) {
        try {
            return Path.of(rawPath);
        } catch (InvalidPathException e) {
            return null;
        }
    }

    private String resumenCorreo(MantenimientoManualResponseDTO mantenimiento) {
        String detalle = limpiar(mantenimiento.getDetalle());
        String trabajo = limpiar(mantenimiento.getDescripcionTrabajoRealizado());
        if (detalle == null) {
            return trabajo;
        }
        if (trabajo == null) {
            return detalle;
        }
        return detalle + "\n\nTrabajo realizado:\n" + trabajo;
    }

    private String limpiar(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }
}
