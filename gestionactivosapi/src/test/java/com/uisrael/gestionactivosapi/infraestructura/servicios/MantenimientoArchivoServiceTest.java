package com.uisrael.gestionactivosapi.infraestructura.servicios;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import com.uisrael.gestionactivosapi.presentacion.dto.request.ImagenMantenimientoRequestDTO;

class MantenimientoArchivoServiceTest {

    private Path basePath;
    private MantenimientoArchivoService service;

    @BeforeEach
    void setUp() throws IOException {
        basePath = Files.createTempDirectory(Path.of("target"), "mantenimientos-test");
        service = new MantenimientoArchivoService(basePath.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        try (var paths = Files.walk(basePath)) {
            paths.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete());
        }
    }

    @Test
    void guardarImagenesConNombreNormalLoConserva() {
        List<ImagenMantenimientoRequestDTO> metadata = service.guardarImagenes(1,
                List.of(imagen("evidencia_01.png")));

        assertThat(metadata).hasSize(1);
        assertThat(metadata.get(0).getNombreArchivo()).isEqualTo("evidencia_01.png");
        assertThat(basePath.resolve("imagenes").resolve("1").resolve("evidencia_01.png")).exists();
    }

    @Test
    void guardarImagenesConTraversalNoEscribeFueraDeLaCarpeta() {
        List<ImagenMantenimientoRequestDTO> metadata = service.guardarImagenes(2,
                List.of(imagen("..\\..\\..\\fuera.txt"), imagen("../../fuera2.txt")));

        assertThat(metadata).extracting(ImagenMantenimientoRequestDTO::getNombreArchivo)
                .containsExactly("fuera.txt", "fuera2.txt");
        Path carpeta = basePath.resolve("imagenes").resolve("2");
        assertThat(carpeta.resolve("fuera.txt")).exists();
        assertThat(carpeta.resolve("fuera2.txt")).exists();
        assertThat(basePath.resolve("fuera.txt")).doesNotExist();
        assertThat(basePath.getParent().resolve("fuera.txt")).doesNotExist();
        assertThat(basePath.getParent().resolve("fuera2.txt")).doesNotExist();
    }

    @Test
    void guardarImagenesConEspaciosYTildesQuedaServible() {
        // El endpoint que sirve imagenes solo acepta [a-zA-Z0-9._-]+: lo guardado
        // debe cumplir ese patron o la evidencia queda inaccesible via web.
        List<ImagenMantenimientoRequestDTO> metadata = service.guardarImagenes(3,
                List.of(imagen("foto de equipo ñ.jpg")));

        assertThat(metadata).hasSize(1);
        String nombre = metadata.get(0).getNombreArchivo();
        assertThat(nombre).matches("[a-zA-Z0-9._-]+");
        assertThat(nombre).endsWith(".jpg");
        assertThat(basePath.resolve("imagenes").resolve("3").resolve(nombre)).exists();
    }

    @Test
    void sanearNombreArchivoCasosLimite() {
        assertThat(MantenimientoArchivoService.sanearNombreArchivo(null)).isEqualTo("evidencia");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("")).isEqualTo("evidencia");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("..")).isEqualTo("evidencia");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("...")).isEqualTo("evidencia");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("C:\\temp\\a.png")).isEqualTo("a.png");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("/etc/passwd")).isEqualTo("passwd");
        assertThat(MantenimientoArchivoService.sanearNombreArchivo("a b%c.png")).isEqualTo("a_b_c.png");
    }

    private MockMultipartFile imagen(String nombreOriginal) {
        return new MockMultipartFile("files", nombreOriginal, "image/jpeg", new byte[] {1, 2, 3});
    }
}
