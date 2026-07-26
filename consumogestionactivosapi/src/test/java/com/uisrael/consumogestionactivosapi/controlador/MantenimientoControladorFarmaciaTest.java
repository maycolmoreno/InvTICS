package com.uisrael.consumogestionactivosapi.controlador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.ActividadChecklistResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MantenimientoManualResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.UbicacionesResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IActividadChecklistServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoManualServicio;
import com.uisrael.consumogestionactivosapi.service.IMantenimientoProgramadoServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;
import com.uisrael.consumogestionactivosapi.service.IUsuariosServicio;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@ExtendWith(MockitoExtension.class)
class MantenimientoControladorFarmaciaTest {

    private static final int UBICACION_MATRIZ = 1;
    private static final int UBICACION_SUCURSAL_B = 2;
    private static final int CUSTODIO_FARMACIA = 100;
    private static final int EQUIPO_FARMACIA = 200;

    @Mock private IMantenimientoManualServicio mantenimientoManualServicio;
    @Mock private IMantenimientoProgramadoServicio mantenimientoProgramadoServicio;
    @Mock private IActividadChecklistServicio actividadChecklistServicio;
    @Mock private IEquiposServicio equiposServicio;
    @Mock private ICustodiasServicio custodiasServicio;
    @Mock private ICustodiosServicio custodiosServicio;
    @Mock private IUsuariosServicio usuariosServicio;
    @Mock private IUbicacionesServicio ubicacionesServicio;
    @Mock private IInventarioOperacionServicio inventarioOperacionServicio;
    @Mock private SesionUsuario sesionUsuario;

    private MantenimientoControlador controlador;

    @BeforeEach
    void setUp() {
        controlador = new MantenimientoControlador(
                mantenimientoManualServicio,
                mantenimientoProgramadoServicio,
                actividadChecklistServicio,
                equiposServicio,
                custodiasServicio,
                custodiosServicio,
                usuariosServicio,
                ubicacionesServicio,
                inventarioOperacionServicio,
                sesionUsuario);

        ActividadChecklistResponseDTO actividad = new ActividadChecklistResponseDTO();
        actividad.setIdActividad(1);
        actividad.setNombre("Limpieza");
        actividad.setOrden(1);
        lenient().when(actividadChecklistServicio.listarActivas()).thenReturn(List.of(actividad));
        lenient().when(actividadChecklistServicio.listarActivasPorTipo("PREVENTIVO"))
                .thenReturn(List.of(actividad));

        // Custodio de la sucursal B, con un equipo cuya custodia activa le pertenece.
        CustodiosResponseDTO custodioFarmacia = new CustodiosResponseDTO();
        custodioFarmacia.setIdCustodio(CUSTODIO_FARMACIA);
        custodioFarmacia.setEstado(true);
        UbicacionesResponseDTO sucursalB = new UbicacionesResponseDTO();
        sucursalB.setIdUbicacion(UBICACION_SUCURSAL_B);
        custodioFarmacia.setFkUbicacion(sucursalB);
        lenient().when(custodiosServicio.listarCustodios()).thenReturn(List.of(custodioFarmacia));

        EquiposResponseDTO equipoFarmacia = new EquiposResponseDTO();
        equipoFarmacia.setIdEquipo(EQUIPO_FARMACIA);

        CustodiasResponseDTO custodiaActiva = new CustodiasResponseDTO();
        custodiaActiva.setEstado(true);
        custodiaActiva.setFkCustodio(custodioFarmacia);
        custodiaActiva.setFkEquipo(equipoFarmacia);
        lenient().when(custodiasServicio.listarCustodias()).thenReturn(List.of(custodiaActiva));

        lenient().when(mantenimientoManualServicio.subirImagenes(any(), any())).thenReturn(List.of());
    }

    private String guardarFarmacia(List<Integer> equipoIds, Integer custodioId, Integer ubicacionId,
            RedirectAttributesModelMap redirect) {
        return controlador.guardar(
                equipoIds,
                custodioId,
                "FARMACIA",
                ubicacionId,
                "PREVENTIVO",
                LocalDate.now(),
                null,
                "BUENO",
                "Detalle de prueba",
                "firmaTecnicoBase64",
                "firmaCustodioBase64",
                List.of(1),
                null,
                null,
                Map.of(),
                redirect);
    }

    @Test
    void rechazaCustodioDeOtraSucursalQueLaSeleccionada() {
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String vista = guardarFarmacia(List.of(EQUIPO_FARMACIA), CUSTODIO_FARMACIA, UBICACION_MATRIZ, redirect);

        assertThat(vista).startsWith("redirect:/mantenimiento/nuevo");
        assertThat(redirect.getFlashAttributes()).containsKey("error");
        verify(mantenimientoManualServicio, never()).crear(any());
    }

    @Test
    void rechazaEquipoSinCustodiaEnLaSucursalSeleccionada() {
        int equipoDeOtraSucursal = 999;
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String vista = guardarFarmacia(List.of(equipoDeOtraSucursal), CUSTODIO_FARMACIA, UBICACION_SUCURSAL_B, redirect);

        assertThat(vista).startsWith("redirect:/mantenimiento/nuevo");
        assertThat(redirect.getFlashAttributes()).containsKey("error");
        verify(mantenimientoManualServicio, never()).crear(any());
    }

    @Test
    void rechazaSinUbicacionSeleccionada() {
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String vista = guardarFarmacia(List.of(EQUIPO_FARMACIA), CUSTODIO_FARMACIA, null, redirect);

        assertThat(vista).startsWith("redirect:/mantenimiento/nuevo");
        assertThat(redirect.getFlashAttributes()).containsKey("error");
        verify(mantenimientoManualServicio, never()).crear(any());
    }

    @Test
    void aceptaCustodioYEquipoDeLaMismaSucursal() {
        MantenimientoManualResponseDTO creado = new MantenimientoManualResponseDTO();
        creado.setIdMantenimiento(50);
        when(mantenimientoManualServicio.crear(any())).thenReturn(creado);

        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String vista = guardarFarmacia(List.of(EQUIPO_FARMACIA), CUSTODIO_FARMACIA, UBICACION_SUCURSAL_B, redirect);

        assertThat(vista).isEqualTo("redirect:/mantenimiento");
        assertThat(redirect.getFlashAttributes()).containsKey("exito");
        verify(mantenimientoManualServicio).crear(any());
    }

    @Test
    void rechazaChecklistQueNoCorrespondeAlTipoDeMantenimiento() {
        RedirectAttributesModelMap redirect = new RedirectAttributesModelMap();

        String vista = controlador.guardar(
                List.of(EQUIPO_FARMACIA), CUSTODIO_FARMACIA, "FARMACIA", UBICACION_SUCURSAL_B,
                "PREVENTIVO", LocalDate.now(), null, "BUENO", "Detalle de prueba",
                "firmaTecnicoBase64", "firmaCustodioBase64", List.of(999), null, null, Map.of(), redirect);

        assertThat(vista).startsWith("redirect:/mantenimiento/nuevo");
        assertThat(redirect.getFlashAttributes().get("error")).isEqualTo("Debes completar todo el checklist");
        verify(mantenimientoManualServicio, never()).crear(any());
    }
}
