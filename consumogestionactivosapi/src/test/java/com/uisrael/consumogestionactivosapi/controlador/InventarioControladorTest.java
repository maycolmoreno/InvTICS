package com.uisrael.consumogestionactivosapi.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.uisrael.consumogestionactivosapi.exception.GlobalExceptionHandler;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

@ExtendWith(MockitoExtension.class)
class InventarioControladorTest {

    @Mock private ICustodiasServicio servicioCustodias;
    @Mock private ICustodiosServicio custodiosServicio;
    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;
    @Mock private IMarcasServicio marcasServicio;
    @Mock private ICategoriaEquiposServicio categoriaEquiposServicio;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventarioControlador controlador = new InventarioControlador(
                servicioCustodias,
                custodiosServicio,
                inventarioOperacionServicio,
                marcasServicio,
                categoriaEquiposServicio);

        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class CrearOrdenCompra {

        @Test
        void exito_redirige_a_compras_con_flash_success() throws Exception {
            when(inventarioOperacionServicio.crearOrdenCompra(any()))
                    .thenReturn(new OrdenCompraResponseDTO());

            mockMvc.perform(post("/inventario/ordenes-compra")
                            .param("numeroOc", "OC-2026-001")
                            .param("proveedor", "TechCorp")
                            .param("bodegaDestinoId", "1")
                            .param("detalles[0].tipoItem", "ACTIVO")
                            .param("detalles[0].descripcion", "Laptop Dell")
                            .param("detalles[0].cantidadSolicitada", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/compras"))
                    .andExpect(flash().attributeExists("success"));
        }

        @Test
        void error_del_backend_redirige_con_flash_error() throws Exception {
            when(inventarioOperacionServicio.crearOrdenCompra(any()))
                    .thenThrow(new RuntimeException("Numero de OC duplicado"));

            mockMvc.perform(post("/inventario/ordenes-compra")
                            .param("numeroOc", "OC-DUP")
                            .param("bodegaDestinoId", "1")
                            .param("detalles[0].tipoItem", "STOCK")
                            .param("detalles[0].descripcion", "Toner HP")
                            .param("detalles[0].cantidadSolicitada", "10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/compras"))
                    .andExpect(flash().attributeExists("error"));
        }

        @Test
        void multiples_detalles_se_envian_correctamente() throws Exception {
            when(inventarioOperacionServicio.crearOrdenCompra(any()))
                    .thenReturn(new OrdenCompraResponseDTO());

            mockMvc.perform(post("/inventario/ordenes-compra")
                            .param("numeroOc", "OC-2026-002")
                            .param("bodegaDestinoId", "2")
                            .param("detalles[0].tipoItem", "ACTIVO")
                            .param("detalles[0].descripcion", "Monitor LG")
                            .param("detalles[0].cantidadSolicitada", "5")
                            .param("detalles[1].tipoItem", "CONSUMIBLE")
                            .param("detalles[1].descripcion", "Toner Canon")
                            .param("detalles[1].cantidadSolicitada", "20"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/compras"))
                    .andExpect(flash().attributeExists("success"));
        }
    }

    @Nested
    class RecepcionStock {

        @Test
        void exito_redirige_a_gestionar_con_flash_success() throws Exception {
            when(inventarioOperacionServicio.registrarRecepcionStock(eq(1), eq(2), any()))
                    .thenReturn(new RecepcionLoteResponseDTO());

            mockMvc.perform(post("/inventario/ordenes-compra/1/detalles/2/recepciones/stock")
                            .param("idBodegaDestino", "1")
                            .param("cantidad", "5")
                            .param("recepcionadoPor", "admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/ordenes-compra/1/gestionar"))
                    .andExpect(flash().attributeExists("success"));
        }

        @Test
        void error_redirige_a_gestionar_con_flash_error() throws Exception {
            when(inventarioOperacionServicio.registrarRecepcionStock(eq(1), eq(2), any()))
                    .thenThrow(new RuntimeException("Cantidad excede la solicitada"));

            mockMvc.perform(post("/inventario/ordenes-compra/1/detalles/2/recepciones/stock")
                            .param("idBodegaDestino", "1")
                            .param("cantidad", "999")
                            .param("recepcionadoPor", "admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/ordenes-compra/1/gestionar"))
                    .andExpect(flash().attributeExists("error"));
        }

        @Test
        void redirige_al_idOC_correcto_cuando_hay_multiples_ordenes() throws Exception {
            when(inventarioOperacionServicio.registrarRecepcionStock(eq(42), eq(7), any()))
                    .thenReturn(new RecepcionLoteResponseDTO());

            mockMvc.perform(post("/inventario/ordenes-compra/42/detalles/7/recepciones/stock")
                            .param("idBodegaDestino", "3")
                            .param("cantidad", "10")
                            .param("recepcionadoPor", "bodeguero"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/ordenes-compra/42/gestionar"));
        }
    }

    @Nested
    class RecepcionActivo {

        @Test
        void exito_redirige_a_gestionar_con_flash_success() throws Exception {
            when(inventarioOperacionServicio.registrarRecepcionActivo(eq(1), eq(3), any()))
                    .thenReturn(new RecepcionLoteResponseDTO());

            mockMvc.perform(post("/inventario/ordenes-compra/1/detalles/3/recepciones/activo")
                            .param("idBodegaDestino", "1")
                            .param("modelo", "ThinkPad X1")
                            .param("serial", "SN-2026-001")
                            .param("recepcionadoPor", "admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/ordenes-compra/1/gestionar"))
                    .andExpect(flash().attributeExists("success"));
        }

        @Test
        void error_serial_duplicado_redirige_con_flash_error() throws Exception {
            when(inventarioOperacionServicio.registrarRecepcionActivo(eq(1), eq(3), any()))
                    .thenThrow(new RuntimeException("Ya existe un activo con serial SN-DUP"));

            mockMvc.perform(post("/inventario/ordenes-compra/1/detalles/3/recepciones/activo")
                            .param("idBodegaDestino", "1")
                            .param("modelo", "ThinkPad X1")
                            .param("serial", "SN-DUP")
                            .param("recepcionadoPor", "admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/ordenes-compra/1/gestionar"))
                    .andExpect(flash().attributeExists("error"));
        }

        @Test
        void error_contiene_mensaje_del_servicio_en_flash() throws Exception {
            String mensajeEsperado = "La recepcion excede la cantidad solicitada";
            when(inventarioOperacionServicio.registrarRecepcionActivo(any(), any(), any()))
                    .thenThrow(new RuntimeException(mensajeEsperado));

            mockMvc.perform(post("/inventario/ordenes-compra/1/detalles/3/recepciones/activo")
                            .param("serial", "SN-X")
                            .param("recepcionadoPor", "admin"))
                    .andExpect(flash().attribute("error",
                            "No se pudo registrar la recepcion del activo: " + mensajeEsperado));
        }
    }
}
