package com.uisrael.gestionactivosapi.aplicacion.servicios;

import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.EquiposJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MantenimientoInformeService {

    private final IEquiposJpaRepositorio equiposRepo;
    private final ICustodiosJpaRepositorio custodiosRepo;
    private final IUsuariosJpaRepositorio usuariosRepo;
    private final PdfMantenimientoService pdfMantenimientoService;
    private final MantenimientoArchivoService archivoService;
    private final CorreoMantenimientoService correoService;

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
                mantenimiento.getDetalle());
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
        EquiposJpa equipo = mantenimiento.getEquipoId() == null
                ? null
                : equiposRepo.findById(mantenimiento.getEquipoId()).orElse(null);
        CustodiosJpa custodio = mantenimiento.getCustodioId() == null
                ? null
                : custodiosRepo.findById(mantenimiento.getCustodioId()).orElse(null);
        UsuariosJpa tecnico = mantenimiento.getTecnicoId() == null
                ? null
                : usuariosRepo.findById(mantenimiento.getTecnicoId()).orElse(null);
        List<Path> rutasImagenes = mantenimiento.getImagenes() == null
                ? List.of()
                : mantenimiento.getImagenes().stream()
                        .map(item -> item.getRutaArchivo())
                        .filter(path -> path != null && !path.isBlank())
                        .map(Path::of)
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
        return custodiosRepo.findById(mantenimiento.getCustodioId())
                .map(CustodiosJpa::getCorreo)
                .orElse(null);
    }
}
