package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;

@ExtendWith(MockitoExtension.class)
@DisplayName("EquiposUseCaseImpl")
class EquiposUseCaseImplTest {

    @Mock
    private EquipoRepositorioPuerto equipoRepositorio;

    private EquiposUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        sut = new EquiposUseCaseImpl(equipoRepositorio);
    }

    private Equipos equipoDePrueba(int id, String serial, String codigoSap, String ip, String mac) {
        return new Equipos(id, codigoSap, "ThinkPad T14", serial,
                "Intel i7", 16, 512, true, mac,
                LocalDate.of(2024, 1, 15), BigDecimal.valueOf(1200),
                "ACTIVO", "Sin observaciones", true, null, null, null);
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea equipo con datos válidos")
        void crearEquipoValido() {
            Equipos equipo = equipoDePrueba(0, "SN001", null, null, null);
            Equipos guardado = equipoDePrueba(1, "SN001", null, null, null);

            when(equipoRepositorio.existeSerial("SN001")).thenReturn(false);
            when(equipoRepositorio.guardar(equipo)).thenReturn(guardado);

            Equipos resultado = sut.crear(equipo);

            assertThat(resultado.getIdEquipo()).isEqualTo(1);
            verify(equipoRepositorio).guardar(equipo);
        }

        @Test
        @DisplayName("lanza DuplicidadException si serial ya existe")
        void crearEquipoSerialDuplicado() {
            Equipos equipo = equipoDePrueba(0, "SN001", null, null, null);
            when(equipoRepositorio.existeSerial("SN001")).thenReturn(true);

            assertThatThrownBy(() -> sut.crear(equipo))
                    .isInstanceOf(DuplicidadException.class)
                    .hasMessageContaining("Serial");

            verify(equipoRepositorio, never()).guardar(any());
        }

        @Test
        @DisplayName("valida formato de Código Activo Fijo")
        void crearEquipoCodigoSapFormatoInvalido() {
            Equipos equipo = equipoDePrueba(0, "SN001", "INVALIDO", null, null);

            assertThatThrownBy(() -> sut.crear(equipo))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("formato");
        }

        @Test
        @DisplayName("acepta Código Activo Fijo con formato válido")
        void crearEquipoCodigoSapValido() {
            Equipos equipo = equipoDePrueba(0, "SN001", "A_EC_00000000919", null, null);
            when(equipoRepositorio.existeCodigo("A_EC_00000000919")).thenReturn(false);
            when(equipoRepositorio.existeSerial("SN001")).thenReturn(false);
            when(equipoRepositorio.guardar(equipo)).thenReturn(equipo);

            sut.crear(equipo);

            verify(equipoRepositorio).guardar(equipo);
        }

        @Test
        @DisplayName("lanza DuplicidadException si Código Activo Fijo ya existe")
        void crearEquipoCodigoSapDuplicado() {
            Equipos equipo = equipoDePrueba(0, "SN001", "A_EC_00000000919", null, null);
            when(equipoRepositorio.existeCodigo("A_EC_00000000919")).thenReturn(true);

            assertThatThrownBy(() -> sut.crear(equipo))
                    .isInstanceOf(DuplicidadException.class)
                    .hasMessageContaining("Código Activo Fijo");
        }

        @Test
        @DisplayName("lanza DuplicidadException si MAC ya existe")
        void crearEquipoMacDuplicada() {
            Equipos equipo = equipoDePrueba(0, "SN001", null, null, "AA:BB:CC:DD:EE:FF");
            when(equipoRepositorio.existeSerial("SN001")).thenReturn(false);
            when(equipoRepositorio.existeMAC("AA:BB:CC:DD:EE:FF")).thenReturn(true);

            assertThatThrownBy(() -> sut.crear(equipo))
                    .isInstanceOf(DuplicidadException.class)
                    .hasMessageContaining("MAC");
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("retorna equipo si existe")
        void obtenerPorIdExistente() {
            Equipos equipo = equipoDePrueba(1, "SN001", null, null, null);
            when(equipoRepositorio.buscarPorId(1)).thenReturn(Optional.of(equipo));

            Equipos resultado = sut.obtenerPorId(1);

            assertThat(resultado.getIdEquipo()).isEqualTo(1);
        }

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si no existe")
        void obtenerPorIdInexistente() {
            when(equipoRepositorio.buscarPorId(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.obtenerPorId(999))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("retorna lista de equipos")
        void listarEquipos() {
            List<Equipos> equipos = List.of(
                    equipoDePrueba(1, "SN001", null, null, null),
                    equipoDePrueba(2, "SN002", null, null, null));
            when(equipoRepositorio.listarTodos()).thenReturn(equipos);

            List<Equipos> resultado = sut.listar();

            assertThat(resultado).hasSize(2);
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("lanza DuplicidadException si serial duplicado para otro equipo")
        void actualizarSerialDuplicado() {
            Equipos equipo = equipoDePrueba(1, "SN002", null, null, null);
            when(equipoRepositorio.existeSerialParaOtro("SN002", 1)).thenReturn(true);

            assertThatThrownBy(() -> sut.actualizar(1, equipo))
                    .isInstanceOf(DuplicidadException.class)
                    .hasMessageContaining("Serial");
        }

        @Test
        @DisplayName("actualiza equipo con datos válidos")
        void actualizarEquipoValido() {
            Equipos equipo = equipoDePrueba(1, "SN001", null, null, null);
            when(equipoRepositorio.existeSerialParaOtro("SN001", 1)).thenReturn(false);
            when(equipoRepositorio.actualizar(anyInt(), any(Equipos.class))).thenReturn(equipo);

            Equipos resultado = sut.actualizar(1, equipo);

            assertThat(resultado).isNotNull();
            verify(equipoRepositorio).actualizar(anyInt(), any(Equipos.class));
        }
    }

    @Nested
    @DisplayName("actualizarEstado()")
    class ActualizarEstado {

        @Test
        @DisplayName("cambia estado del equipo")
        void actualizarEstado() {
            Equipos equipo = equipoDePrueba(1, "SN001", null, null, null);
            when(equipoRepositorio.buscarPorId(1)).thenReturn(Optional.of(equipo));
            when(equipoRepositorio.actualizar(anyInt(), any(Equipos.class))).thenReturn(equipo);

            sut.actualizarEstado(1, false);

            verify(equipoRepositorio).actualizar(anyInt(), any(Equipos.class));
        }

        @Test
        @DisplayName("lanza excepción si equipo no existe")
        void actualizarEstadoNoExiste() {
            when(equipoRepositorio.buscarPorId(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.actualizarEstado(999, false))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }
}
