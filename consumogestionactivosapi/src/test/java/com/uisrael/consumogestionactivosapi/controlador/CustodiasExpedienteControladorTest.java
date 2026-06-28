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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

@ExtendWith(MockitoExtension.class)
class CustodiasExpedienteControladorTest {

    @Mock private ICustodiosServicio custodiosServicio;
    @Mock private ICustodiasServicio custodiasServicio;
    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CustodiasExpedienteControlador(
                custodiosServicio, custodiasServicio, inventarioOperacionServicio)).build();
    }

    @Test
    void expediente_carga_bodegas_para_devolucion() throws Exception {
        CustodiosResponseDTO custodio = new CustodiosResponseDTO();
        custodio.setIdCustodio(7);
        BodegaResponseDTO bodega = new BodegaResponseDTO();
        bodega.setIdBodega(5);
        bodega.setEstado(true);

        when(custodiosServicio.obtenerPorId(7)).thenReturn(custodio);
        when(custodiasServicio.listarCustodias()).thenReturn(List.of());
        when(inventarioOperacionServicio.listarMovimientosRecientes()).thenReturn(List.of());
        when(inventarioOperacionServicio.listarBodegas()).thenReturn(List.of(bodega));

        mockMvc.perform(get("/custodias/expediente/7"))
                .andExpect(status().isOk())
                .andExpect(view().name("Custodias/expedienteCustodio"))
                .andExpect(model().attributeExists("bodegas"));
    }
}
