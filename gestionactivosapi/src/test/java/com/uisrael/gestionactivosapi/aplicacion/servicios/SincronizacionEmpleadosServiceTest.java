package com.uisrael.gestionactivosapi.aplicacion.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ISyncEmpleadosCambioJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ISyncEmpleadosEjecucionJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.request.sync.EmpleadoSyncDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.sync.SincronizacionResultadoDTO;

class SincronizacionEmpleadosServiceTest {

    private ICustodiosJpaRepositorio custodiosRepo;
    private ICustodiasJpaRepositorio custodiasRepo;
    private ICargosJpaRepositorio cargosRepo;
    private ISyncEmpleadosEjecucionJpaRepositorio ejecucionRepo;
    private ISyncEmpleadosCambioJpaRepositorio cambioRepo;
    private SincronizacionEmpleadosService service;

    @BeforeEach
    void setUp() {
        custodiosRepo = mock(ICustodiosJpaRepositorio.class);
        custodiasRepo = mock(ICustodiasJpaRepositorio.class);
        cargosRepo = mock(ICargosJpaRepositorio.class);
        ejecucionRepo = mock(ISyncEmpleadosEjecucionJpaRepositorio.class);
        cambioRepo = mock(ISyncEmpleadosCambioJpaRepositorio.class);
        service = new SincronizacionEmpleadosService(custodiosRepo, custodiasRepo, cargosRepo,
                ejecucionRepo, cambioRepo);

        when(ejecucionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(custodiosRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(custodiosRepo.findFirstByCedulaIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(cargosRepo.findAll()).thenReturn(List.of());
        when(cambioRepo.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    private static EmpleadoSyncDTO empleado(String cedula, String nombre, Boolean activo) {
        EmpleadoSyncDTO dto = new EmpleadoSyncDTO();
        dto.setCedula(cedula);
        dto.setNombre(nombre);
        dto.setActivo(activo);
        return dto;
    }

    private static CustodiosJpa custodio(int id, String cedula, String nombre, boolean estado) {
        CustodiosJpa c = new CustodiosJpa();
        c.setIdCustodio(id);
        c.setCedula(cedula);
        c.setNombre(nombre);
        c.setEstado(estado);
        return c;
    }

    @Test
    void creaCustodioParaEmpleadoNuevoActivo() {
        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", true)), "MANUAL", "admin");

        assertThat(r.getCreados()).isEqualTo(1);
        assertThat(r.getAdvertencias()).isZero();
        assertThat(r.getCambios()).anySatisfy(c -> {
            assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_CREADO);
            assertThat(c.getCedula()).isEqualTo("0102030405");
        });
    }

    @Test
    void omiteEmpleadoNuevoInactivoConAdvertencia() {
        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", false)), "MANUAL", "admin");

        assertThat(r.getCreados()).isZero();
        assertThat(r.getAdvertencias()).isEqualTo(1);
    }

    @Test
    void omiteEmpleadoSinCedulaConAdvertencia() {
        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("  ", "Sin Cedula", true)), "MANUAL", "admin");

        assertThat(r.getCreados()).isZero();
        assertThat(r.getAdvertencias()).isEqualTo(1);
    }

    @Test
    void actualizaCamposCambiados() {
        when(custodiosRepo.findFirstByCedulaIgnoreCase("0102030405"))
                .thenReturn(Optional.of(custodio(7, "0102030405", "Ana Perez", true)));

        EmpleadoSyncDTO cambio = empleado("0102030405", "Ana Perez Lopez", true);
        cambio.setCorreo("ana@uisrael.edu.ec");
        SincronizacionResultadoDTO r = service.sincronizar(List.of(cambio), "MANUAL", "admin");

        assertThat(r.getActualizados()).isEqualTo(1);
        assertThat(r.getCambios()).anySatisfy(c -> {
            assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_ACTUALIZADO);
            assertThat(c.getDetalle()).contains("nombre").contains("correo");
        });
    }

    @Test
    void sinCambiosCuandoDatosCoinciden() {
        when(custodiosRepo.findFirstByCedulaIgnoreCase("0102030405"))
                .thenReturn(Optional.of(custodio(7, "0102030405", "Ana Perez", true)));

        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", true)), "MANUAL", "admin");

        assertThat(r.getSinCambios()).isEqualTo(1);
        assertThat(r.getActualizados()).isZero();
    }

    @Test
    void inactivaCustodioYAlertaSiTieneCustodiasActivas() {
        when(custodiosRepo.findFirstByCedulaIgnoreCase("0102030405"))
                .thenReturn(Optional.of(custodio(7, "0102030405", "Ana Perez", true)));
        when(custodiasRepo.countByFkCustodio_IdCustodioAndEstadoTrue(7)).thenReturn(2L);

        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", false)), "MANUAL", "admin");

        assertThat(r.getInactivados()).isEqualTo(1);
        assertThat(r.getCambios()).anySatisfy(c ->
                assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_ALERTA_ACTIVOS));
        assertThat(r.getCambios()).anySatisfy(c ->
                assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_INACTIVADO));
    }

    @Test
    void inactivaSinAlertaCuandoNoTieneCustodias() {
        when(custodiosRepo.findFirstByCedulaIgnoreCase("0102030405"))
                .thenReturn(Optional.of(custodio(7, "0102030405", "Ana Perez", true)));
        when(custodiasRepo.countByFkCustodio_IdCustodioAndEstadoTrue(7)).thenReturn(0L);

        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", false)), "MANUAL", "admin");

        assertThat(r.getInactivados()).isEqualTo(1);
        assertThat(r.getCambios()).noneSatisfy(c ->
                assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_ALERTA_ACTIVOS));
    }

    @Test
    void reactivaCustodioInactivoCuandoLaFuenteLoTraeActivo() {
        when(custodiosRepo.findFirstByCedulaIgnoreCase("0102030405"))
                .thenReturn(Optional.of(custodio(7, "0102030405", "Ana Perez", false)));

        SincronizacionResultadoDTO r = service.sincronizar(
                List.of(empleado("0102030405", "Ana Perez", true)), "MANUAL", "admin");

        assertThat(r.getReactivados()).isEqualTo(1);
    }

    @Test
    void resuelveCargoIgnorandoMayusculasYTildes() {
        CargosJpa cargo = new CargosJpa();
        cargo.setIdCargo(3);
        cargo.setNombre("Analista de Tecnología");
        when(cargosRepo.findAll()).thenReturn(List.of(cargo));

        EmpleadoSyncDTO nuevo = empleado("0102030405", "Ana Perez", true);
        nuevo.setCargo("ANALISTA DE TECNOLOGIA");
        SincronizacionResultadoDTO r = service.sincronizar(List.of(nuevo), "MANUAL", "admin");

        assertThat(r.getCreados()).isEqualTo(1);
        assertThat(r.getAdvertencias()).isZero();
    }

    @Test
    void advierteCuandoElCargoNoExiste() {
        EmpleadoSyncDTO nuevo = empleado("0102030405", "Ana Perez", true);
        nuevo.setCargo("Cargo Fantasma");
        SincronizacionResultadoDTO r = service.sincronizar(List.of(nuevo), "MANUAL", "admin");

        assertThat(r.getCreados()).isEqualTo(1);
        assertThat(r.getAdvertencias()).isEqualTo(1);
        assertThat(r.getCambios()).anySatisfy(c ->
                assertThat(c.getTipo()).isEqualTo(SincronizacionEmpleadosService.TIPO_ADVERTENCIA));
    }

    @Test
    void parseaArrayRaizYObjetoConClaveEmpleados() {
        List<EmpleadoSyncDTO> desdeArray = service.parsearEmpleadosDesdeJson(
                "[{\"cedula\":\"0102030405\",\"nombre\":\"Ana\",\"activo\":true}]");
        assertThat(desdeArray).hasSize(1);
        assertThat(desdeArray.get(0).getCedula()).isEqualTo("0102030405");

        List<EmpleadoSyncDTO> desdeObjeto = service.parsearEmpleadosDesdeJson(
                "{\"empleados\":[{\"cedula\":\"0102030405\",\"nombre\":\"Ana\"},{\"cedula\":\"0605040302\",\"nombre\":\"Luis\"}]}");
        assertThat(desdeObjeto).hasSize(2);
    }

    @Test
    void rechazaJsonInvalido() {
        assertThatThrownBy(() -> service.parsearEmpleadosDesdeJson("{\"otraCosa\":1}"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.parsearEmpleadosDesdeJson("no-es-json"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
