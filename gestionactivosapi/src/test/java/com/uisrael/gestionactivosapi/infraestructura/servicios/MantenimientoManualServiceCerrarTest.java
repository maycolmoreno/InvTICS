package com.uisrael.gestionactivosapi.infraestructura.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uisrael.gestionactivosapi.dominio.entidades.EstadoInternoMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.ResultadoTecnico;
import com.uisrael.gestionactivosapi.dominio.excepciones.MantenimientoNoModificableException;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.FirmaMantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.MantenimientosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IImagenMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoEquipoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

@ExtendWith(MockitoExtension.class)
class MantenimientoManualServiceCerrarTest {

    @Mock
    private IMantenimientosJpaRepositorio mantenimientosRepo;
    @Mock
    private IMantenimientoEquipoJpaRepositorio mantenimientoEquipoRepo;
    @Mock
    private IActividadRealizadaJpaRepositorio actividadRealizadaRepo;
    @Mock
    private IActividadChecklistJpaRepositorio actividadChecklistRepo;
    @Mock
    private IImagenMantenimientoJpaRepositorio imagenRepo;
    @Mock
    private IEquiposJpaRepositorio equiposRepo;
    @Mock
    private ICustodiosJpaRepositorio custodiosRepo;
    @Mock
    private IUsuariosJpaRepositorio usuariosRepo;
    @Mock
    private MantenimientoProgramadoService programadoService;
    @Mock
    private NotificacionService notificacionService;
    @Mock
    private FirmaMantenimientoRepositorioPuerto firmaMantenimientoRepositorio;

    private MantenimientoManualService service;

    @BeforeEach
    void setUp() {
        service = new MantenimientoManualService(mantenimientosRepo, mantenimientoEquipoRepo,
                actividadRealizadaRepo, actividadChecklistRepo, imagenRepo, equiposRepo,
                custodiosRepo, usuariosRepo, programadoService, notificacionService,
                firmaMantenimientoRepositorio);
    }

    private MantenimientosJpa mantenimiento(EstadoInternoMantenimiento estadoInterno) {
        MantenimientosJpa m = new MantenimientosJpa();
        m.setIdMantenimiento(10);
        m.setEquipoId(7);
        m.setTipoMantenimiento("PREVENTIVO");
        m.setEstadoInterno(estadoInterno);
        return m;
    }

    @Test
    void cerrarUnaOtYaCerradaEsRechazado() {
        MantenimientosJpa cerrado = mantenimiento(EstadoInternoMantenimiento.CERRADO);
        when(mantenimientosRepo.findById(10)).thenReturn(Optional.of(cerrado));

        assertThatThrownBy(() -> service.cerrar(10, "otro trabajo", ResultadoTecnico.REPARADO, "otro@correo"))
                .isInstanceOf(MantenimientoNoModificableException.class)
                .hasMessageContaining("CERRADO");

        verify(mantenimientosRepo, never()).save(any());
        verify(programadoService, never()).recalcularProximaFecha(any());
        verify(notificacionService, never()).marcarRelacionadasComoLeidas(any());
    }

    @Test
    void cerrarEnProcesoCierraYRecalculaPlanPreventivo() {
        MantenimientosJpa abierto = mantenimiento(EstadoInternoMantenimiento.EN_PROCESO);
        when(mantenimientosRepo.findById(10)).thenReturn(Optional.of(abierto));

        MantenimientoManualResponseDTO dto = service.cerrar(10, "Se limpio el equipo",
                ResultadoTecnico.REPARADO, "tecnico@correo");

        assertThat(dto.getEstadoInterno()).isEqualTo("CERRADO");
        assertThat(dto.getResultadoTecnico()).isEqualTo(ResultadoTecnico.REPARADO);
        assertThat(dto.getCerradoPor()).isEqualTo("tecnico@correo");
        assertThat(dto.getCerradoEn()).isNotNull();
        verify(mantenimientosRepo).save(abierto);
        verify(programadoService).recalcularProximaFecha(7);
        verify(notificacionService).marcarRelacionadasComoLeidas(10);
    }

    @Test
    void cerrarConEstadoInternoNullSigueSiendoPosible() {
        // Registros legacy sin estado interno no deben quedar imposibles de cerrar.
        MantenimientosJpa legacy = mantenimiento(null);
        when(mantenimientosRepo.findById(10)).thenReturn(Optional.of(legacy));

        MantenimientoManualResponseDTO dto = service.cerrar(10, null, ResultadoTecnico.SIN_FALLA, null);

        assertThat(dto.getEstadoInterno()).isEqualTo("CERRADO");
        verify(mantenimientosRepo).save(legacy);
    }

    @Test
    void cerrarSinResultadoTecnicoEsRechazado() {
        assertThatThrownBy(() -> service.cerrar(10, "trabajo", null, "tecnico@correo"))
                .isInstanceOf(IllegalStateException.class);
        verify(mantenimientosRepo, never()).save(any());
    }
}
