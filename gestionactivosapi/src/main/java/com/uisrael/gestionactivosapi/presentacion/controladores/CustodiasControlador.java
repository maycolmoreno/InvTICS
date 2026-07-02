package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CustodiasRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICustodiasDtoMapper;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/custodias")
public class CustodiasControlador {

    private final ICustodiasUseCase custodiasUseCase;
    private final ICustodiasDtoMapper mapper;
    private final Path actasFirmadasPath;

    private static final long MAX_PDF_BYTES = 5L * 1024 * 1024;

    public CustodiasControlador(ICustodiasUseCase custodiasUseCase, ICustodiasDtoMapper mapper,
            @Value("${actas.storage.base-path:./data/actas}") String actasBasePath) {
        this.custodiasUseCase = custodiasUseCase;
        this.mapper = mapper;
        this.actasFirmadasPath = Path.of(actasBasePath, "firmadas");
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CustodiasResponseDTO> crear(@Valid @RequestBody CustodiasRequestDTO request) {

        return request.getEquipos().stream().map(eq -> {

            CustodiasRequestDTO uno = new CustodiasRequestDTO();
            uno.setFechaInicio(request.getFechaInicio());
            uno.setFechaFin(request.getFechaFin());
            uno.setObservacion(request.getObservacion());
            uno.setEstado(request.isEstado());
            uno.setFkCustodio(request.getFkCustodio());
            uno.setTipoMovimiento(request.getTipoMovimiento());

            // Aqui asignamos un equipo por registro
            uno.setEquipos(List.of(eq));

            return mapper.toResponseDto(
                    custodiasUseCase.crear(mapper.toDomain(uno))
            );

        }).toList();
    }


    @GetMapping
    public List<CustodiasResponseDTO> listar() {
        return custodiasUseCase.listar().stream().map(mapper::toResponseDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustodiasResponseDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(mapper.toResponseDto(custodiasUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustodiasResponseDTO> actualizar(@PathVariable int id,
            @Valid @RequestBody CustodiasRequestDTO request) {

        return ResponseEntity.ok(
            mapper.toResponseDto(
                custodiasUseCase.actualizar(id, mapper.toDomain(request))
            )
        );
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<CustodiasResponseDTO> actualizarEstado(
        @PathVariable int id,
        @RequestBody CustodiasRequestDTO request
    ){

        return ResponseEntity.ok(
            mapper.toResponseDto(
                custodiasUseCase.actualizarEstado(id, mapper.toDomain(request))
            )
        );
    }

    @PutMapping("/acta-pdf")
    public ResponseEntity<Void> registrarActaPdf(
            @RequestParam List<Integer> ids,
            @RequestParam String rutaPdf) {
        custodiasUseCase.registrarActaPdf(ids, rutaPdf);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/{id}/acta-firmada", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> subirActaFirmada(
            @PathVariable int id,
            @RequestPart("archivo") MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo es obligatorio"));
        }
        if (archivo.getSize() > MAX_PDF_BYTES) {
            return ResponseEntity.badRequest().body(Map.of("error", "El archivo supera el limite de 5MB"));
        }
        String contentType = archivo.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Solo se permiten archivos PDF"));
        }
        Files.createDirectories(actasFirmadasPath);
        String nombreArchivo = "acta_firmada_" + id + ".pdf";
        Path destino = actasFirmadasPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        String ruta = destino.toString().replace('\\', '/');
        custodiasUseCase.registrarActaFirmada(id, ruta);
        return ResponseEntity.ok(Map.of("rutaActaFirmada", ruta));
    }

    @GetMapping("/{id}/acta-firmada")
    public ResponseEntity<?> descargarActaFirmada(@PathVariable int id) throws IOException {
        Custodias c = custodiasUseCase.obtenerPorId(id);
        String ruta = c.getRutaActaFirmada();
        if (ruta == null || ruta.isBlank()) {
            return ResponseEntity.noContent().build();
        }
        Path archivo = Path.of(ruta);
        if (!Files.exists(archivo)) {
            return ResponseEntity.notFound().build();
        }
        byte[] bytes = Files.readAllBytes(archivo);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header("Content-Disposition", "inline; filename=\"acta_firmada_" + id + ".pdf\"")
                .body(bytes);
    }

}

