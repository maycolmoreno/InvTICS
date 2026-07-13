package com.uisrael.gestionactivosapi.presentacion.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICustodiasDtoMapper;

@ExtendWith(MockitoExtension.class)
class CustodiasControladorTest {

    private static final Path ACTAS_TEST_PATH = Path.of("target/test-actas-" + System.nanoTime());

    @Mock private ICustodiasUseCase custodiasUseCase;
    @Mock private ICustodiasDtoMapper mapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CustodiasControlador(custodiasUseCase, mapper, ACTAS_TEST_PATH.toString())).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(ACTAS_TEST_PATH)) {
            try (var walk = Files.walk(ACTAS_TEST_PATH)) {
                walk.sorted((a, b) -> b.compareTo(a)).forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException ignored) {
                        // limpieza best-effort
                    }
                });
            }
        }
    }

    private Custodias custodiaDominio(int id) {
        return new Custodias(id, LocalDate.of(2024, 1, 1), null, "Asignacion inicial", true, null, null);
    }

    private CustodiasResponseDTO custodiaResponse(int id) {
        CustodiasResponseDTO dto = new CustodiasResponseDTO();
        dto.setIdCustodiaEquipo(id);
        dto.setObservacion("Asignacion inicial");
        dto.setEstado(true);
        return dto;
    }

    @Test
    void crear_creaUnaCustodiaPorCadaEquipoYDevuelve201() throws Exception {
        when(mapper.toDomain(any())).thenReturn(custodiaDominio(0));
        when(custodiasUseCase.crear(any())).thenReturn(custodiaDominio(1), custodiaDominio(2));
        when(mapper.toResponseDto(any())).thenReturn(custodiaResponse(1), custodiaResponse(2));

        String body = """
                {
                  "fechaInicio":"2024-01-01",
                  "observacion":"Asignacion inicial",
                  "estado":true,
                  "equipos":[{"idEquipo":10},{"idEquipo":11}],
                  "fkCustodio":{"idCustodio":5,"nombre":"Maria Perez","cedula":"1710034065"}
                }
                """;

        mockMvc.perform(post("/api/custodias").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));

        // una custodia creada por cada equipo del arreglo (comportamiento actual, sin las 5 reglas de asignacion)
        verify(custodiasUseCase, times(2)).crear(any());
    }

    @Test
    void crear_devuelve400SiFaltaObservacion() throws Exception {
        String body = """
                {
                  "fechaInicio":"2024-01-01",
                  "estado":true,
                  "equipos":[{"idEquipo":10}],
                  "fkCustodio":{"idCustodio":5,"nombre":"Maria Perez","cedula":"1710034065"}
                }
                """;

        mockMvc.perform(post("/api/custodias").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verify(custodiasUseCase, times(0)).crear(any());
    }

    @Test
    void crear_devuelve400SiFaltaFechaInicio() throws Exception {
        String body = """
                {
                  "observacion":"Asignacion inicial",
                  "estado":true,
                  "equipos":[{"idEquipo":10}],
                  "fkCustodio":{"idCustodio":5,"nombre":"Maria Perez","cedula":"1710034065"}
                }
                """;

        mockMvc.perform(post("/api/custodias").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listar_devuelveListaDeCustodias() throws Exception {
        when(custodiasUseCase.listar()).thenReturn(List.of(custodiaDominio(1), custodiaDominio(2)));
        when(mapper.toResponseDto(any())).thenReturn(custodiaResponse(1), custodiaResponse(2));

        mockMvc.perform(get("/api/custodias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerPorId_devuelveCustodia() throws Exception {
        when(custodiasUseCase.obtenerPorId(7)).thenReturn(custodiaDominio(7));
        when(mapper.toResponseDto(any())).thenReturn(custodiaResponse(7));

        mockMvc.perform(get("/api/custodias/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodiaEquipo").value(7));
    }

    @Test
    void actualizar_devuelveCustodiaActualizada() throws Exception {
        Custodias dominio = custodiaDominio(0);
        Custodias actualizada = custodiaDominio(3);
        when(mapper.toDomain(any())).thenReturn(dominio);
        when(custodiasUseCase.actualizar(eq(3), eq(dominio))).thenReturn(actualizada);
        when(mapper.toResponseDto(actualizada)).thenReturn(custodiaResponse(3));

        String body = """
                {
                  "fechaInicio":"2024-01-01",
                  "observacion":"Actualizada",
                  "estado":true,
                  "equipos":[{"idEquipo":10}],
                  "fkCustodio":{"idCustodio":5,"nombre":"Maria Perez","cedula":"1710034065"}
                }
                """;

        mockMvc.perform(put("/api/custodias/3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodiaEquipo").value(3));
    }

    @Test
    void actualizarEstado_devuelveCustodiaConNuevoEstado() throws Exception {
        Custodias dominio = custodiaDominio(0);
        Custodias actualizada = custodiaDominio(4);
        when(mapper.toDomain(any())).thenReturn(dominio);
        when(custodiasUseCase.actualizarEstado(eq(4), eq(dominio))).thenReturn(actualizada);
        when(mapper.toResponseDto(actualizada)).thenReturn(custodiaResponse(4));

        mockMvc.perform(put("/api/custodias/estado/4").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodiaEquipo").value(4));
    }

    @Test
    void registrarActaPdf_delegaAlUseCaseYDevuelve200() throws Exception {
        mockMvc.perform(put("/api/custodias/acta-pdf")
                        .param("ids", "1", "2")
                        .param("rutaPdf", "/data/actas/acta-1.pdf"))
                .andExpect(status().isOk());

        verify(custodiasUseCase).registrarActaPdf(List.of(1, 2), "/data/actas/acta-1.pdf");
    }

    @Test
    void subirActaFirmada_devuelve400SiArchivoVacio() throws Exception {
        MockMultipartFile archivoVacio = new MockMultipartFile("archivo", "acta.pdf",
                "application/pdf", new byte[0]);

        mockMvc.perform(multipart("/api/custodias/1/acta-firmada").file(archivoVacio))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El archivo es obligatorio"));
    }

    @Test
    void subirActaFirmada_devuelve400SiSuperaTamanoMaximo() throws Exception {
        byte[] contenidoGrande = new byte[6 * 1024 * 1024];
        MockMultipartFile archivoGrande = new MockMultipartFile("archivo", "acta.pdf",
                "application/pdf", contenidoGrande);

        mockMvc.perform(multipart("/api/custodias/1/acta-firmada").file(archivoGrande))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El archivo supera el limite de 5MB"));
    }

    @Test
    void subirActaFirmada_devuelve400SiNoEsPdf() throws Exception {
        MockMultipartFile archivoTexto = new MockMultipartFile("archivo", "acta.txt",
                "text/plain", "contenido".getBytes());

        mockMvc.perform(multipart("/api/custodias/1/acta-firmada").file(archivoTexto))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Solo se permiten archivos PDF"));
    }

    @Test
    void subirActaFirmada_guardaArchivoYRegistraRuta() throws Exception {
        MockMultipartFile archivoPdf = new MockMultipartFile("archivo", "acta.pdf",
                "application/pdf", "contenido-pdf".getBytes());

        mockMvc.perform(multipart("/api/custodias/1/acta-firmada").file(archivoPdf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutaActaFirmada").exists());

        verify(custodiasUseCase).registrarActaFirmada(eq(1), any());
        assertActaFirmadaGuardadaEnDisco();
    }

    private void assertActaFirmadaGuardadaEnDisco() {
        Path esperado = ACTAS_TEST_PATH.resolve("firmadas").resolve("acta_firmada_1.pdf");
        org.junit.jupiter.api.Assertions.assertTrue(Files.exists(esperado));
    }

    @Test
    void descargarActaFirmada_devuelve204SiNoHayActaRegistrada() throws Exception {
        Custodias sinActa = custodiaDominio(8);
        when(custodiasUseCase.obtenerPorId(8)).thenReturn(sinActa);

        mockMvc.perform(get("/api/custodias/8/acta-firmada"))
                .andExpect(status().isNoContent());
    }

    @Test
    void descargarActaFirmada_devuelve404SiArchivoNoExisteEnDisco() throws Exception {
        Custodias conRutaInexistente = custodiaDominio(9);
        conRutaInexistente.setRutaActaFirmada(ACTAS_TEST_PATH.resolve("firmadas/no-existe.pdf").toString());
        when(custodiasUseCase.obtenerPorId(9)).thenReturn(conRutaInexistente);

        mockMvc.perform(get("/api/custodias/9/acta-firmada"))
                .andExpect(status().isNotFound());
    }

    @Test
    void descargarActaFirmada_devuelveBytesDelPdfSiExisteEnDisco() throws Exception {
        Files.createDirectories(ACTAS_TEST_PATH.resolve("firmadas"));
        Path archivo = ACTAS_TEST_PATH.resolve("firmadas/acta_firmada_10.pdf");
        Files.write(archivo, "contenido-pdf".getBytes());

        Custodias conActa = custodiaDominio(10);
        conActa.setRutaActaFirmada(archivo.toString());
        when(custodiasUseCase.obtenerPorId(10)).thenReturn(conActa);

        mockMvc.perform(get("/api/custodias/10/acta-firmada"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_PDF));
    }
}
