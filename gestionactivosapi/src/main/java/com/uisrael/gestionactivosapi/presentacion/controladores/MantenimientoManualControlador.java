package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.gestionactivosapi.aplicacion.servicios.MantenimientoArchivoService;
import com.uisrael.gestionactivosapi.aplicacion.servicios.MantenimientoInformeService;
import com.uisrael.gestionactivosapi.aplicacion.servicios.MantenimientoManualService;
import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.request.MantenimientoManualRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mantenimiento")
@RequiredArgsConstructor
public class MantenimientoManualControlador {

    private static final Logger log = LoggerFactory.getLogger(MantenimientoManualControlador.class);

    private final MantenimientoManualService mantenimientoService;
    private final MantenimientoInformeService mantenimientoInformeService;
    private final MantenimientoArchivoService mantenimientoArchivoService;

    @GetMapping
    public List<MantenimientoManualResponseDTO> listarTodos() {
        return mantenimientoService.listarTodos();
    }

    @GetMapping("/{id}")
    public MantenimientoManualResponseDTO obtenerDetalle(@PathVariable Integer id) {
        return mantenimientoService.obtenerDetalle(id);
    }

    @GetMapping("/historial/{equipoId}")
    public List<MantenimientoManualResponseDTO> historial(@PathVariable Integer equipoId) {
        return mantenimientoService.obtenerHistorial(equipoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientoManualResponseDTO crear(@Valid @RequestBody MantenimientoManualRequestDTO request,
            Principal principal) {
        MantenimientoManualResponseDTO creado = mantenimientoService.crear(request, principal.getName());
        try {
            mantenimientoInformeService.generarGuardarYEnviar(creado);
        } catch (Exception e) {
            log.error("No se pudo generar/enviar el informe del mantenimiento {}: {}", creado.getIdMantenimiento(),
                    e.getMessage(), e);
        }
        return creado;
    }

    @PostMapping("/{id}/imagenes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void guardarImagenes(@PathVariable Integer id, @RequestBody List<ImagenMantenimientoRequestDTO> imagenes) {
        mantenimientoService.guardarImagenes(id, imagenes);
    }

    @PostMapping(value = "/{id}/imagenes/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ImagenMantenimientoRequestDTO> subirImagenes(
            @PathVariable Integer id,
            @RequestPart("files") List<MultipartFile> files) {
        List<ImagenMantenimientoRequestDTO> metadata = mantenimientoArchivoService.guardarImagenes(id, files);
        mantenimientoService.guardarImagenes(id, metadata);
        return metadata;
    }

    @PostMapping("/cerrar/{id}")
    public MantenimientoManualResponseDTO cerrar(@PathVariable Integer id) {
        return mantenimientoService.cerrar(id);
    }

    @GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> verPdf(@PathVariable Integer id) {
        MantenimientoManualResponseDTO mantenimiento = mantenimientoService.obtenerDetalle(id);
        byte[] pdfBytes = mantenimientoInformeService.obtenerOGenerarPdf(mantenimiento);
        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=\"mantenimiento_" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @PostMapping("/{id}/reenviar-correo")
    public ResponseEntity<Map<String, String>> reenviarCorreo(@PathVariable Integer id) {
        MantenimientoManualResponseDTO mantenimiento = mantenimientoService.obtenerDetalle(id);
        mantenimientoInformeService.reenviar(mantenimiento);
        return ResponseEntity.ok(Map.of("message", "Correo reenviado correctamente."));
    }
}
