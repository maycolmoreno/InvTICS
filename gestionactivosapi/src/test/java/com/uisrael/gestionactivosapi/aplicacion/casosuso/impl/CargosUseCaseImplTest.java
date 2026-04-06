package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CargosRepositorioPuerto;

@ExtendWith(MockitoExtension.class)
@DisplayName("CargosUseCaseImpl")
class CargosUseCaseImplTest {

    @Mock
    private CargosRepositorioPuerto cargosRepositorio;

    private CargosUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        sut = new CargosUseCaseImpl(cargosRepositorio);
    }

    private Cargos cargoDePrueba(int id, String nombre) {
        return new Cargos(id, nombre, true, new Departamentos(1, "TI", true));
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea cargo exitosamente")
        void crearCargo() {
            Cargos cargo = cargoDePrueba(0, "Analista");
            Cargos guardado = cargoDePrueba(1, "Analista");
            when(cargosRepositorio.existeNombre("Analista")).thenReturn(false);
            when(cargosRepositorio.guardar(cargo)).thenReturn(guardado);

            Cargos resultado = sut.crear(cargo);

            assertThat(resultado.getIdCargo()).isEqualTo(1);
            verify(cargosRepositorio).guardar(cargo);
        }

        @Test
        @DisplayName("lanza DuplicidadException si nombre ya existe")
        void crearCargoDuplicado() {
            Cargos cargo = cargoDePrueba(0, "Analista");
            when(cargosRepositorio.existeNombre("Analista")).thenReturn(true);

            assertThatThrownBy(() -> sut.crear(cargo))
                    .isInstanceOf(DuplicidadException.class)
                    .hasMessageContaining("cargo");

            verify(cargosRepositorio, never()).guardar(any());
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("retorna cargo si existe")
        void obtenerExistente() {
            Cargos cargo = cargoDePrueba(1, "Analista");
            when(cargosRepositorio.buscarPorId(1)).thenReturn(Optional.of(cargo));

            Cargos resultado = sut.obtenerPorId(1);

            assertThat(resultado.getNombre()).isEqualTo("Analista");
        }

        @Test
        @DisplayName("lanza RecursoNoEncontradoException")
        void obtenerInexistente() {
            when(cargosRepositorio.buscarPorId(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.obtenerPorId(999))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si cargo no existe")
        void actualizarInexistente() {
            Cargos cargo = cargoDePrueba(1, "Analista");
            when(cargosRepositorio.buscarPorId(1)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.actualizar(1, cargo))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }

        @Test
        @DisplayName("lanza DuplicidadException si nombre duplicado para otro")
        void actualizarNombreDuplicado() {
            Cargos cargo = cargoDePrueba(1, "Director");
            when(cargosRepositorio.buscarPorId(1)).thenReturn(Optional.of(cargo));
            when(cargosRepositorio.existeNombreParaOtro("Director", 1)).thenReturn(true);

            assertThatThrownBy(() -> sut.actualizar(1, cargo))
                    .isInstanceOf(DuplicidadException.class);
        }

        @Test
        @DisplayName("actualiza cargo exitosamente")
        void actualizarExitoso() {
            Cargos cargo = cargoDePrueba(1, "Analista Senior");
            when(cargosRepositorio.buscarPorId(1)).thenReturn(Optional.of(cargo));
            when(cargosRepositorio.existeNombreParaOtro("Analista Senior", 1)).thenReturn(false);
            when(cargosRepositorio.actualizar(anyInt(), any(Cargos.class))).thenReturn(cargo);

            Cargos resultado = sut.actualizar(1, cargo);

            assertThat(resultado).isNotNull();
        }
    }

    @Nested
    @DisplayName("actualizarEstado()")
    class ActualizarEstado {

        @Test
        @DisplayName("cambia estado del cargo")
        void actualizarEstado() {
            Cargos cargo = cargoDePrueba(1, "Analista");
            when(cargosRepositorio.buscarPorId(1)).thenReturn(Optional.of(cargo));
            when(cargosRepositorio.actualizarEstado(anyInt(), any(Cargos.class))).thenReturn(cargo);

            sut.actualizarEstado(1, false);

            verify(cargosRepositorio).actualizarEstado(anyInt(), any(Cargos.class));
        }

        @Test
        @DisplayName("lanza excepción si cargo no existe")
        void actualizarEstadoInexistente() {
            when(cargosRepositorio.buscarPorId(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.actualizarEstado(999, false))
                    .isInstanceOf(RecursoNoEncontradoException.class);
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("retorna lista de cargos")
        void listarCargos() {
            List<Cargos> cargos = List.of(cargoDePrueba(1, "Analista"), cargoDePrueba(2, "Director"));
            when(cargosRepositorio.listarTodos()).thenReturn(cargos);

            List<Cargos> resultado = sut.listar();

            assertThat(resultado).hasSize(2);
        }
    }
}
