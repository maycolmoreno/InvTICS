package com.uisrael.consumogestionactivosapi.controlador;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional.CentroOperacionalDTO;
import com.uisrael.consumogestionactivosapi.service.ICentroOperacionalServicio;

@ExtendWith(MockitoExtension.class)
class InventarioDashboardControladorTest {

    @Mock private ICentroOperacionalServicio centroOperacionalServicio;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new InventarioDashboardControlador(centroOperacionalServicio))
                .build();
    }

    @Test
    void dashboard_retornaCentroOperacional() throws Exception {
        when(centroOperacionalServicio.obtenerCentroOperacional()).thenReturn(new CentroOperacionalDTO());

        mockMvc.perform(get("/inventario/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("Inventario/dashboard"))
                .andExpect(model().attributeExists("centro"));
    }
}
