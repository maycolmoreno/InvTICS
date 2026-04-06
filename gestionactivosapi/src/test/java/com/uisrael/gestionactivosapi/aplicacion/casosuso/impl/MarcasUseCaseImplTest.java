package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MarcaRepositorioPuerto;

@ExtendWith(MockitoExtension.class)
@DisplayName("MarcasUseCaseImpl")
class MarcasUseCaseImplTest {

    @Mock
    private MarcaRepositorioPuerto marcaRepositorio;

    private MarcasUseCaseImpl sut;

    @BeforeEach
    void setUp() {
        sut = new MarcasUseCaseImpl(marcaRepositorio);
    }

    @Nested
    @DisplayName("crear()")
    class Crear {

        @Test
        @DisplayName("crea marca exitosamente")
        void crearMarca() {
            Marcas marca = new Marcas(0, "Dell", true);
            Marcas guardada = new Marcas(1, "Dell", true);
            when(marcaRepositorio.guardar(marca)).thenReturn(guardada);

            Marcas resultado = sut.crear(marca);

            assertThat(resultado.getIdMarca()).isEqualTo(1);
            assertThat(resultado.getNombre()).isEqualTo("Dell");
            verify(marcaRepositorio).guardar(marca);
        }
    }

    @Nested
    @DisplayName("obtenerPorId()")
    class ObtenerPorId {

        @Test
        @DisplayName("retorna marca si existe")
        void obtenerExistente() {
            Marcas marca = new Marcas(1, "HP", true);
            when(marcaRepositorio.buscarPorId(1)).thenReturn(Optional.of(marca));

            Marcas resultado = sut.obtenerPorId(1);

            assertThat(resultado.getNombre()).isEqualTo("HP");
        }

        @Test
        @DisplayName("lanza RecursoNoEncontradoException si no existe")
        void obtenerInexistente() {
            when(marcaRepositorio.buscarPorId(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> sut.obtenerPorId(999))
                    .isInstanceOf(RecursoNoEncontradoException.class)
                    .hasMessageContaining("no encontrada");
        }
    }

    @Nested
    @DisplayName("listar()")
    class Listar {

        @Test
        @DisplayName("retorna lista de marcas")
        void listarMarcas() {
            List<Marcas> marcas = List.of(
                    new Marcas(1, "Dell", true),
                    new Marcas(2, "HP", true),
                    new Marcas(3, "Lenovo", true));
            when(marcaRepositorio.listarTodos()).thenReturn(marcas);

            List<Marcas> resultado = sut.listar();

            assertThat(resultado).hasSize(3);
        }

        @Test
        @DisplayName("retorna lista vacía si no hay marcas")
        void listarSinMarcas() {
            when(marcaRepositorio.listarTodos()).thenReturn(List.of());

            List<Marcas> resultado = sut.listar();

            assertThat(resultado).isEmpty();
        }
    }

    @Nested
    @DisplayName("actualizar()")
    class Actualizar {

        @Test
        @DisplayName("actualiza marca exitosamente")
        void actualizarMarca() {
            Marcas marca = new Marcas(1, "Dell Technologies", true);
            when(marcaRepositorio.actualizar(1, marca)).thenReturn(marca);

            Marcas resultado = sut.actualizar(1, marca);

            assertThat(resultado.getNombre()).isEqualTo("Dell Technologies");
            verify(marcaRepositorio).actualizar(1, marca);
        }
    }

    @Nested
    @DisplayName("eliminar()")
    class Eliminar {

        @Test
        @DisplayName("elimina marca por ID")
        void eliminarMarca() {
            sut.eliminar(1);

            verify(marcaRepositorio).eliminar(1);
        }
    }
}
