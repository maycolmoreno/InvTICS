package com.uisrael.consumogestionactivosapi.controlador;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpClientErrorException;

import com.uisrael.consumogestionactivosapi.exception.GlobalExceptionHandler;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

@ExtendWith(MockitoExtension.class)
class InventarioExperienciaControladorTest {

    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;
    @Mock private IMarcasServicio marcasServicio;
    @Mock private ICategoriaEquiposServicio categoriaEquiposServicio;
    @Mock private ICustodiosServicio custodiosServicio;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventarioExperienciaControlador controlador = new InventarioExperienciaControlador(
                inventarioOperacionServicio,
                marcasServicio,
                categoriaEquiposServicio,
                custodiosServicio);

        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void gestionarOC_oc_existente_retorna_vista_con_modelo() throws Exception {
        OrdenCompraResponseDTO oc = new OrdenCompraResponseDTO();
        oc.setIdOrdenCompra(1);
        oc.setNumeroOc("OC-2026-001");

        when(inventarioOperacionServicio.obtenerOrdenCompra(1)).thenReturn(oc);
        when(inventarioOperacionServicio.listarRecepciones(1)).thenReturn(List.of());
        when(inventarioOperacionServicio.listarBodegas()).thenReturn(List.of());
        when(marcasServicio.listarMarca()).thenReturn(List.of());
        when(categoriaEquiposServicio.listarCategoriaEquipo()).thenReturn(List.of());

        mockMvc.perform(get("/inventario/ordenes-compra/1/gestionar"))
                .andExpect(status().isOk())
                .andExpect(view().name("Inventario/gestionarOC"))
                .andExpect(model().attributeExists("oc"))
                .andExpect(model().attributeExists("recepciones"))
                .andExpect(model().attributeExists("bodegas"))
                .andExpect(model().attributeExists("recepcionStockRequest"))
                .andExpect(model().attributeExists("recepcionActivoRequest"));
    }

    @Test
    void gestionarOC_backend_404_renderiza_vista_error() throws Exception {
        when(inventarioOperacionServicio.obtenerOrdenCompra(99))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.NOT_FOUND, "Not Found", null, null, null));

        mockMvc.perform(get("/inventario/ordenes-compra/99/gestionar"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/backend-error"))
                .andExpect(model().attribute("errorStatus", 404));
    }

    @Test
    void compras_retorna_vista_con_lista_ordenes() throws Exception {
        when(inventarioOperacionServicio.listarOrdenesCompra()).thenReturn(List.of());
        when(inventarioOperacionServicio.listarBodegas()).thenReturn(List.of());

        mockMvc.perform(get("/inventario/compras"))
                .andExpect(status().isOk())
                .andExpect(view().name("Inventario/compras"))
                .andExpect(model().attributeExists("ordenesCompra"))
                .andExpect(model().attributeExists("ordenCompraRequest"));
    }
}
