package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoArchivoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoInformeService;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientoManualUseCase;
import com.uisrael.gestionactivosapi.dominio.modelo.Pagina;
import com.uisrael.gestionactivosapi.presentacion.dto.response.PaginaResponse;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ActividadManualComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.ImagenMantenimientoComando;
import com.uisrael.gestionactivosapi.infraestructura.servicios.modelo.MantenimientoManualComando;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CerrarMantenimientoRequestDTO;
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

    private final IMantenimientoManualUseCase mantenimientoService;
    private final MantenimientoInformeService mantenimientoInformeService;
    private final MantenimientoArchivoService mantenimientoArchivoService;

    @GetMapping
    public List<MantenimientoManualResponseDTO> listarTodos() {
        return mantenimientoService.listarTodos();
    }

    @GetMapping("/paginado")
    public PaginaResponse<MantenimientoManualResponseDTO> listarPaginado(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pagina<MantenimientoManualResponseDTO> pagina = mantenimientoService.listarTodosPaginado(page, size);
        PaginaResponse<MantenimientoManualResponseDTO> resp = new PaginaResponse<>();
        resp.setContenido(pagina.contenido());
        resp.setPaginaActual(pagina.paginaActual());
        resp.setTamanioPagina(pagina.tamanioPagina());
        resp.setTotalElementos(pagina.totalElementos());
        resp.setTotalPaginas(pagina.totalPaginas());
        resp.setPrimera(pagina.paginaActual() == 0);
        resp.setUltima(pagina.paginaActual() + 1 >= pagina.totalPaginas());
        return resp;
    }

    @GetMapping("/{id}")
    public MantenimientoManualResponseDTO obtenerDetalle(@PathVariable Integer id) {
        return mantenimientoService.obtenerDetalle(id);
    }

    @GetMapping("/historial/{equipoId}")
    public List<MantenimientoManualResponseDTO> historial(@PathVariable Integer equipoId) {
        return mantenimientoService.obtenerHistorial(equipoId);
    }

    @GetMapping("/tecnico/{tecnicoId}")
    public List<MantenimientoManualResponseDTO> listarPorTecnico(@PathVariable Integer tecnicoId) {
        return mantenimientoService.listarPorTecnico(tecnicoId);
    }

    @GetMapping("/tecnico/{tecnicoId}/paginado")
    public PaginaResponse<MantenimientoManualResponseDTO> listarPorTecnicoPaginado(
            @PathVariable Integer tecnicoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pagina<MantenimientoManualResponseDTO> pagina = mantenimientoService.listarPorTecnicoPaginado(tecnicoId, page, size);
        PaginaResponse<MantenimientoManualResponseDTO> resp = new PaginaResponse<>();
        resp.setContenido(pagina.contenido());
        resp.setPaginaActual(pagina.paginaActual());
        resp.setTamanioPagina(pagina.tamanioPagina());
        resp.setTotalElementos(pagina.totalElementos());
        resp.setTotalPaginas(pagina.totalPaginas());
        resp.setPrimera(pagina.paginaActual() == 0);
        resp.setUltima(pagina.paginaActual() + 1 >= pagina.totalPaginas());
        return resp;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MantenimientoManualResponseDTO crear(@Valid @RequestBody MantenimientoManualRequestDTO request,
            Principal principal) {
        MantenimientoManualResponseDTO creado = mantenimientoService.crear(toCommand(request), principal.getName());
        return creado;
    }

    @PostMapping("/{id}/imagenes")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void guardarImagenes(@PathVariable Integer id, @RequestBody List<ImagenMantenimientoRequestDTO> imagenes) {
        mantenimientoService.guardarImagenes(id, toImageCommands(imagenes));
    }

    @PostMapping(value = "/{id}/imagenes/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<ImagenMantenimientoRequestDTO> subirImagenes(
            @PathVariable Integer id,
            @RequestPart("files") List<MultipartFile> files) {
        List<ImagenMantenimientoRequestDTO> metadata = mantenimientoArchivoService.guardarImagenes(id, files);
        mantenimientoService.guardarImagenes(id, toImageCommands(metadata));
        return metadata;
    }

    @GetMapping("/{id}/imagenes/{filename:.+}")
    public ResponseEntity<byte[]> servirImagen(@PathVariable Integer id, @PathVariable String filename) {
        if (filename == null || !filename.matches("[a-zA-Z0-9._-]+")) {
            return ResponseEntity.badRequest().build();
        }
        byte[] bytes = mantenimientoArchivoService.leerImagen(id, filename);
        if (bytes == null) {
            return ResponseEntity.notFound().build();
        }
        String mime = mantenimientoArchivoService.detectarTipoMime(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mime)
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(bytes);
    }

    @PostMapping("/cerrar/{id}")
    public MantenimientoManualResponseDTO cerrar(@PathVariable Integer id,
            @RequestBody(required = false) CerrarMantenimientoRequestDTO request) {
        MantenimientoManualResponseDTO cerrado = mantenimientoService.cerrar(id,
                request != null ? request.getDescripcionTrabajoRealizado() : null);
        try {
            mantenimientoInformeService.generarGuardarYEnviar(cerrado);
        } catch (Exception e) {
            log.error("No se pudo generar/enviar el informe del mantenimiento cerrado {}: {}", id, e.getMessage(), e);
        }
        return cerrado;
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

    private MantenimientoManualComando toCommand(MantenimientoManualRequestDTO request) {
        return new MantenimientoManualComando(
                request.getEquipoId(),
                request.getCustodioId(),
                request.getTipoMantenimiento(),
                request.getFechaMantenimiento(),
                request.getDetalle(),
                request.getEstadoGeneral(),
                request.getProximaFecha(),
                request.getFirmaTecnico(),
                request.getFirmaCustodio(),
                request.getIpOrigen(),
                request.getActividades() == null ? List.of() : request.getActividades().stream()
                        .map(a -> new ActividadManualComando(a.getIdActividad(), a.getRealizada()))
                        .toList(),
                toImageCommands(request.getImagenes()));
    }

    private List<ImagenMantenimientoComando> toImageCommands(List<ImagenMantenimientoRequestDTO> imagenes) {
        if (imagenes == null) {
            return List.of();
        }
        return imagenes.stream()
                .map(img -> new ImagenMantenimientoComando(img.getNombreArchivo(), img.getRutaArchivo(), img.getTamanioBytes()))
                .toList();
    }
}
