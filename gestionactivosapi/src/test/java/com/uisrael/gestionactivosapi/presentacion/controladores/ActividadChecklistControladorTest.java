package com.uisrael.gestionactivosapi.presentacion.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.ActividadChecklist;

@ExtendWith(MockitoExtension.class)
class ActividadChecklistControladorTest {

    @Mock private IObtenerChecklistPorCategoriaUseCase checklistUseCase;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ActividadChecklistControlador(checklistUseCase)).build();
    }

    private ActividadChecklist actividad(int id, String nombre, int orden, boolean estado) {
        ActividadChecklist a = new ActividadChecklist();
        a.setIdActividad(id);
        a.setNombre(nombre);
        a.setOrden(orden);
        a.setEstado(estado);
        return a;
    }

    @Test
    void listarActivas_devuelveListaMapeadaSinCategoria() throws Exception {
        when(checklistUseCase.listarActivas())
                .thenReturn(List.of(actividad(1, "Limpieza de mainboard", 1, true)));

        mockMvc.perform(get("/api/actividades-checklist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idActividad").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Limpieza de mainboard"))
                .andExpect(jsonPath("$[0].orden").value(1))
                .andExpect(jsonPath("$[0].estado").value(true))
                // el campo "categoria" ya no existe en el DTO de respuesta (eliminado 2026-07-12)
                .andExpect(jsonPath("$[0].categoria").doesNotExist())
                .andExpect(jsonPath("$[0].categorias").doesNotExist());
    }

    @Test
    void obtenerPorId_devuelveActividad() throws Exception {
        when(checklistUseCase.obtenerPorId(5)).thenReturn(actividad(5, "Revisar bateria", 2, true));

        mockMvc.perform(get("/api/actividades-checklist/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idActividad").value(5))
                .andExpect(jsonPath("$.nombre").value("Revisar bateria"));
    }

    @Test
    void listarPorCategoria_delegaAlUseCaseConElIdRecibido() throws Exception {
        when(checklistUseCase.ejecutar(3)).thenReturn(List.of(actividad(1, "Limpieza", 1, true)));

        mockMvc.perform(get("/api/actividades-checklist/categoria/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        verify(checklistUseCase).ejecutar(3);
    }

    @Test
    void crear_devuelve201ConActividadCreada() throws Exception {
        when(checklistUseCase.crear(any())).thenReturn(actividad(10, "Verificar ventiladores", 3, true));

        mockMvc.perform(post("/api/actividades-checklist").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Verificar ventiladores\",\"orden\":3}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idActividad").value(10))
                .andExpect(jsonPath("$.nombre").value("Verificar ventiladores"));
    }

    @Test
    void crear_noEnviaCategoriaAlDominioPorqueYaNoExiste() throws Exception {
        ArgumentCaptor<ActividadChecklist> captor = ArgumentCaptor.forClass(ActividadChecklist.class);
        when(checklistUseCase.crear(captor.capture())).thenReturn(actividad(11, "Nueva actividad", 1, true));

        mockMvc.perform(post("/api/actividades-checklist").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Nueva actividad\",\"orden\":1}"))
                .andExpect(status().isCreated());

        ActividadChecklist enviado = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals("Nueva actividad", enviado.getNombre());
        org.junit.jupiter.api.Assertions.assertEquals(1, enviado.getOrden());
    }

    @Test
    void actualizar_devuelveActividadActualizada() throws Exception {
        when(checklistUseCase.actualizar(eq(7), any())).thenReturn(actividad(7, "Actividad editada", 2, false));

        mockMvc.perform(put("/api/actividades-checklist/7").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Actividad editada\",\"orden\":2,\"estado\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idActividad").value(7))
                .andExpect(jsonPath("$.estado").value(false));
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/actividades-checklist/9"))
                .andExpect(status().isNoContent());

        verify(checklistUseCase).eliminar(9);
    }
}
