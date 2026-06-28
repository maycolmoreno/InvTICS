package com.uisrael.consumogestionactivosapi.controlador;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.OrdenCompraResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.RecepcionLoteResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;
import com.uisrael.consumogestionactivosapi.service.ActaStorageService;
import com.uisrael.consumogestionactivosapi.service.CustodiasPdfService;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class InventarioControladorTest {

    @Mock private ICustodiasServicio servicioCustodias;
    @Mock private ICustodiosServicio custodiosServicio;
    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;
    @Mock private IMarcasServicio marcasServicio;
    @Mock private ICategoriaEquiposServicio categoriaEquiposServicio;
    @Mock private CustodiasPdfService custodiasPdfService;
    @Mock private ActaStorageService actaStorageService;
    @Mock private SesionUsuario sesionUsuario;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        InventarioControlador controlador = new InventarioControlador(
                servicioCustodias,
                custodiosServicio,
                inventarioOperacionServicio,
                marcasServicio,
                categoriaEquiposServicio,
                custodiasPdfService,
                actaStorageService,
                sesionUsuario);

        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Nested
    class Asignaciones {

        @Test
        void lote_envia_activo_y_genera_acta() throws Exception {
            ActivoInventarioResponseDTO activo = new ActivoInventarioResponseDTO();
            activo.setCodigoCresio("CR-LAP-001");
            CustodiosResponseDTO custodio = new CustodiosResponseDTO();
            custodio.setIdCustodio(7);
            CustodiasResponseDTO custodia = new CustodiasResponseDTO();
            custodia.setIdCustodiaEquipo(21);
            custodia.setFkCustodio(custodio);
            AsignacionActivosResponseDTO asignacion = new AsignacionActivosResponseDTO();
            asignacion.setActivos(List.of(activo));
            asignacion.setCustodias(List.of(custodia));
            when(inventarioOperacionServicio.asignarActivosLote(any())).thenReturn(asignacion);
            when(custodiasPdfService.generarActaEntregaPdfBytes(any(), eq("admin"), any(), eq("ASIGNACION")))
                    .thenReturn(new byte[] {1});
            when(actaStorageService.guardarActaPdf(any(), eq("ASIGNACION"), eq(7), any()))
                    .thenReturn("acta_asignacion_7.pdf");
            mockMvc.perform(post("/inventario/asignaciones/lote")
                            .param("custodioId", "7")
                            .param("equipoIds", "11")
                            .param("condicionEntrega", "BUENO")
                            .param("fechaInicio", "2026-06-24")
                            .param("realizadoPor", "admin"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/asignaciones"))
                    .andExpect(flash().attributeExists("success"));

            verify(inventarioOperacionServicio).asignarActivosLote(argThat(req ->
                    req.getEquipoIds().equals(List.of(11)) && req.getCustodioId().equals(7)));
            verify(actaStorageService).registrarRutaEnCustodias(eq(List.of(21)), eq("acta_asignacion_7.pdf"));
        }

        @Test
        void lote_sin_items_redirige_con_error_claro() throws Exception {
            mockMvc.perform(post("/inventario/asignaciones/lote")
                            .param("custodioId", "7")
                            .param("condicionEntrega", "BUENO")
                            .param("fechaInicio", "2026-06-24"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/asignaciones"))
                    .andExpect(flash().attributeExists("error"));
        }

        @Test
        @SuppressWarnings("unchecked")
        void vista_oculta_activos_con_custodia_y_consumibles_inactivos() throws Exception {
            ActivoInventarioResponseDTO libre = new ActivoInventarioResponseDTO();
            libre.setIdEquipo(11);
            libre.setEtiquetado(true);
            ActivoInventarioResponseDTO conCustodia = new ActivoInventarioResponseDTO();
            conCustodia.setIdEquipo(22);
            conCustodia.setEtiquetado(true);
            when(inventarioOperacionServicio.listarActivosEnBodega()).thenReturn(List.of(libre, conCustodia));

            EquiposResponseDTO equipo = new EquiposResponseDTO();
            equipo.setIdEquipo(22);
            CustodiasResponseDTO custodia = new CustodiasResponseDTO();
            custodia.setEstado(true);
            custodia.setFkEquipo(equipo);
            when(servicioCustodias.listarCustodias()).thenReturn(List.of(custodia));

            ConsumibleResponseDTO activo = new ConsumibleResponseDTO();
            activo.setIdConsumible(3);
            activo.setEstado(true);
            ConsumibleResponseDTO inactivo = new ConsumibleResponseDTO();
            inactivo.setIdConsumible(4);
            inactivo.setEstado(false);
            when(inventarioOperacionServicio.listarConsumibles()).thenReturn(List.of(activo, inactivo));

            BodegaResponseDTO bodega = new BodegaResponseDTO();
            bodega.setIdBodega(5);
            bodega.setEstado(true);
            when(inventarioOperacionServicio.listarBodegas()).thenReturn(List.of(bodega));

            StockConsumibleResponseDTO stockActivo = new StockConsumibleResponseDTO();
            stockActivo.setConsumibleId(3);
            StockConsumibleResponseDTO stockInactivo = new StockConsumibleResponseDTO();
            stockInactivo.setConsumibleId(4);
            when(inventarioOperacionServicio.listarStockPorBodega(5)).thenReturn(List.of(stockActivo, stockInactivo));

            var result = mockMvc.perform(get("/inventario/asignaciones"))
                    .andExpect(status().isOk())
                    .andReturn();

            List<ActivoInventarioResponseDTO> activos = (List<ActivoInventarioResponseDTO>) result.getModelAndView().getModel().get("activosEnBodega");
            List<StockConsumibleResponseDTO> stock = (List<StockConsumibleResponseDTO>) result.getModelAndView().getModel().get("stockConsumibles");

            org.junit.jupiter.api.Assertions.assertEquals(List.of(11), activos.stream().map(ActivoInventarioResponseDTO::getIdEquipo).toList());
            org.junit.jupiter.api.Assertions.assertEquals(List.of(3), stock.stream().map(StockConsumibleResponseDTO::getConsumibleId).toList());
        }

        @Test
        void devolucion_desde_expediente_regresa_al_expediente() throws Exception {
            ActivoInventarioResponseDTO activo = new ActivoInventarioResponseDTO();
            activo.setCodigoCresio("CR-LAP-001");
            when(inventarioOperacionServicio.devolverActivo(any())).thenReturn(activo);

            mockMvc.perform(post("/inventario/devoluciones/activos")
                            .param("equipoId", "11")
                            .param("bodegaId", "5")
                            .param("returnTo", "/custodias/expediente/7"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/custodias/expediente/7"))
                    .andExpect(flash().attributeExists("success"));
        }

        @Test
        void baja_no_redirige_a_url_externa() throws Exception {
            ActivoInventarioResponseDTO activo = new ActivoInventarioResponseDTO();
            activo.setCodigoCresio("CR-LAP-001");
            when(inventarioOperacionServicio.darBajaActivo(any())).thenReturn(activo);

            mockMvc.perform(post("/inventario/bajas/activos")
                            .param("equipoId", "11")
                            .param("motivo", "OBSOLESCENCIA")
                            .param("observacion", "Fin de vida")
                            .param("returnTo", "//evil.test"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/inventario/bajas"))
                    .andExpect(flash().attributeExists("success"));
        }
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
