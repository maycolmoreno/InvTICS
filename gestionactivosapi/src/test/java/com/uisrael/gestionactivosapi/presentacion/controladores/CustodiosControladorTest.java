package com.uisrael.gestionactivosapi.presentacion.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.servicios.SincronizacionEmpleadosService;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.CandidatoDirectorioDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.CustodioResueltoDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICustodiosDtoMapper;

@ExtendWith(MockitoExtension.class)
class CustodiosControladorTest {

    @Mock private ICustodiosUseCase custodiosUseCase;
    @Mock private ICustodiosDtoMapper mapper;
    @Mock private SincronizacionEmpleadosService sincronizacionEmpleadosService;

    private MockMvc mockMvc;

    private static final String CEDULA_VALIDA = "1710034065";
    private static final String CEDULA_INVALIDA = "1234567890";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new CustodiosControlador(custodiosUseCase, mapper, sincronizacionEmpleadosService)).build();
    }

    private Custodios custodioDominio(int id) {
        return new Custodios(id, "Maria Perez", CEDULA_VALIDA, "maria@cresio.com", "0999999999",
                LocalDate.of(2024, 1, 1), true, null);
    }

    private CustodiosResponseDTO custodioResponse(int id) {
        CustodiosResponseDTO dto = new CustodiosResponseDTO();
        dto.setIdCustodio(id);
        dto.setNombre("Maria Perez");
        dto.setCedula(CEDULA_VALIDA);
        dto.setEstado(true);
        return dto;
    }

    @Test
    void crear_devuelve201ConCustodioCreado() throws Exception {
        Custodios dominio = custodioDominio(0);
        Custodios creado = custodioDominio(1);
        when(mapper.toDomain(any())).thenReturn(dominio);
        when(custodiosUseCase.crear(dominio)).thenReturn(creado);
        when(mapper.toResponseDto(creado)).thenReturn(custodioResponse(1));

        String body = """
                {"nombre":"Maria Perez","cedula":"%s","fechaIngreso":"2024-01-01","estado":true}
                """.formatted(CEDULA_VALIDA);

        mockMvc.perform(post("/api/custodios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCustodio").value(1))
                .andExpect(jsonPath("$.nombre").value("Maria Perez"));
    }

    @Test
    void crear_devuelve400SiNombreFalta() throws Exception {
        String body = """
                {"cedula":"%s"}
                """.formatted(CEDULA_VALIDA);

        mockMvc.perform(post("/api/custodios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verify(custodiosUseCase, never()).crear(any());
    }

    @Test
    void crear_devuelve400SiCedulaInvalida() throws Exception {
        String body = """
                {"nombre":"Maria Perez","cedula":"%s"}
                """.formatted(CEDULA_INVALIDA);

        mockMvc.perform(post("/api/custodios").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest());

        verify(custodiosUseCase, never()).crear(any());
    }

    @Test
    void listar_devuelveListaDeCustodios() throws Exception {
        when(custodiosUseCase.listar()).thenReturn(List.of(custodioDominio(1), custodioDominio(2)));
        when(mapper.toResponseDto(any())).thenReturn(custodioResponse(1), custodioResponse(2));

        mockMvc.perform(get("/api/custodios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void obtenerPorId_devuelveCustodio() throws Exception {
        when(custodiosUseCase.obtenerPorId(5)).thenReturn(custodioDominio(5));
        when(mapper.toResponseDto(any())).thenReturn(custodioResponse(5));

        mockMvc.perform(get("/api/custodios/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodio").value(5));
    }

    @Test
    void actualizar_devuelveCustodioActualizado() throws Exception {
        Custodios dominio = custodioDominio(0);
        Custodios actualizado = custodioDominio(3);
        when(mapper.toDomain(any())).thenReturn(dominio);
        when(custodiosUseCase.actualizar(eq(3), eq(dominio))).thenReturn(actualizado);
        when(mapper.toResponseDto(actualizado)).thenReturn(custodioResponse(3));

        String body = """
                {"nombre":"Maria Perez","cedula":"%s","estado":true}
                """.formatted(CEDULA_VALIDA);

        mockMvc.perform(put("/api/custodios/3").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodio").value(3));
    }

    @Test
    void actualizarEstado_cambiaEstadoYDevuelveCustodio() throws Exception {
        Custodios actualizado = custodioDominio(4);
        when(custodiosUseCase.actualizarEstado(4, false)).thenReturn(actualizado);
        when(mapper.toResponseDto(actualizado)).thenReturn(custodioResponse(4));

        mockMvc.perform(put("/api/custodios/estado/4").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodio").value(4));

        verify(custodiosUseCase).actualizarEstado(4, false);
    }

    @Test
    void existeCedula_sinIdConsultaExisteCedula() throws Exception {
        when(custodiosUseCase.existeCedula(CEDULA_VALIDA)).thenReturn(true);

        mockMvc.perform(get("/api/custodios/existe-cedula").param("cedula", CEDULA_VALIDA))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(custodiosUseCase, never()).existeCedulaParaOtro(any(), anyInt());
    }

    @Test
    void existeCedula_conIdConsultaExisteCedulaParaOtro() throws Exception {
        when(custodiosUseCase.existeCedulaParaOtro(CEDULA_VALIDA, 9)).thenReturn(false);

        mockMvc.perform(get("/api/custodios/existe-cedula").param("cedula", CEDULA_VALIDA).param("id", "9"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(custodiosUseCase, never()).existeCedula(any());
    }

    @Test
    void existeCorreo_sinIdConsultaExisteCorreo() throws Exception {
        when(custodiosUseCase.existeCorreo("maria@cresio.com")).thenReturn(true);

        mockMvc.perform(get("/api/custodios/existe-correo").param("correo", "maria@cresio.com"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void existeCorreo_conIdConsultaExisteCorreoParaOtro() throws Exception {
        when(custodiosUseCase.existeCorreoParaOtro("maria@cresio.com", 2)).thenReturn(false);

        mockMvc.perform(get("/api/custodios/existe-correo").param("correo", "maria@cresio.com").param("id", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void buscarEnDirectorio_devuelveCandidatos() throws Exception {
        CandidatoDirectorioDTO candidato = new CandidatoDirectorioDTO(CEDULA_VALIDA, "Maria Perez",
                "Analista", "Tecnologia", "maria@cresio.com", "0999999999");
        when(sincronizacionEmpleadosService.buscarEnDirectorio("maria")).thenReturn(List.of(candidato));

        mockMvc.perform(get("/api/custodios/directorio/buscar").param("q", "maria"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cedula").value(CEDULA_VALIDA));
    }

    @Test
    void resolverDesdeDirectorio_devuelveCustodioResuelto() throws Exception {
        CustodioResueltoDTO resuelto = new CustodioResueltoDTO(10, "Maria Perez", CEDULA_VALIDA,
                "Analista", "Tecnologia", true, List.of());
        when(sincronizacionEmpleadosService.resolverDesdeDirectorio(CEDULA_VALIDA)).thenReturn(resuelto);

        mockMvc.perform(post("/api/custodios/directorio/resolver").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cedula\": \"" + CEDULA_VALIDA + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCustodio").value(10))
                .andExpect(jsonPath("$.creado").value(true));
    }

    @Test
    void resolverDesdeDirectorio_devuelve400SiPersonaNoExiste() throws Exception {
        when(sincronizacionEmpleadosService.resolverDesdeDirectorio(CEDULA_INVALIDA))
                .thenThrow(new IllegalArgumentException("La persona no aparece en el directorio institucional"));

        mockMvc.perform(post("/api/custodios/directorio/resolver").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cedula\": \"" + CEDULA_INVALIDA + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La persona no aparece en el directorio institucional"));
    }

    @Test
    void previsualizarDesdeDirectorio_devuelveDatosSiExiste() throws Exception {
        CandidatoDirectorioDTO candidato = new CandidatoDirectorioDTO(CEDULA_VALIDA, "Maria Perez",
                "Analista", "Tecnologia", "maria@cresio.com", "0999999999");
        when(sincronizacionEmpleadosService.buscarPorCedulaEnDirectorio(CEDULA_VALIDA))
                .thenReturn(Optional.of(candidato));

        mockMvc.perform(get("/api/custodios/directorio/" + CEDULA_VALIDA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Maria Perez"));
    }

    @Test
    void previsualizarDesdeDirectorio_devuelve404SiNoExiste() throws Exception {
        when(sincronizacionEmpleadosService.buscarPorCedulaEnDirectorio(CEDULA_INVALIDA))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/custodios/directorio/" + CEDULA_INVALIDA))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void previsualizarDesdeDirectorio_devuelve400SiFallaLaSesionExterna() throws Exception {
        when(sincronizacionEmpleadosService.buscarPorCedulaEnDirectorio(CEDULA_VALIDA))
                .thenThrow(new IllegalStateException("No se pudo conectar con el directorio institucional"));

        mockMvc.perform(get("/api/custodios/directorio/" + CEDULA_VALIDA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("No se pudo conectar con el directorio institucional"));
    }
}
