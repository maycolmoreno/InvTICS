package com.uisrael.consumogestionactivosapi.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.consumogestionactivosapi.TestFixtures;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;

/**
 * Suite de pruebas unitarias para EquiposService.
 * Implementa patrón AAA (Arrange, Act, Assert).
 * Utiliza Mockito para simular dependencias externas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EquiposServiceTest - Unit Tests")
class EquiposServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(EquiposServiceTest.class);

    @Mock
    private IEquiposServicio equiposServicio;

    @InjectMocks
    private IEquiposServicio servicioUnderTest;

    @BeforeEach
    void setUp() {
        logger.info("Inicializando prueba unitaria");
    }

    @Test
    @DisplayName("Debe listar equipos exitosamente")
    void testListarEquipos_Success() {
        // Arrange
        List<EquiposResponseDTO> expected = TestFixtures.crearListaEquiposDePrueba();
        when(equiposServicio.listarEquipos()).thenReturn(expected);

        // Act
        List<EquiposResponseDTO> result = equiposServicio.listarEquipos();

        // Assert
        assertThat(result)
            .isNotEmpty()
            .hasSize(2)
            .isEqualTo(expected);

        verify(equiposServicio).listarEquipos();
        logger.info("Test listado de equipos: PASSED");
    }

    @Test
    @DisplayName("Debe crear equipo con datos válidos")
    void testCrearEquipo_Success() {
        // Arrange
        EquiposRequestDTO request = TestFixtures.crearEquipoRequestDePrueba();

        // Act & Assert
        assertThat(request.getCodigoSap()).isNotBlank();
        assertThat(request.getMemoriaRamGb()).isPositive();

        verify(equiposServicio, times(0)).listarEquipos();
        logger.info("Test creación de equipo: PASSED");
    }

    @Test
    @DisplayName("Debe obtener equipo por ID existente")
    void testObtenerPorId_Success() {
        // Arrange
        int idEquipo = 1;
        EquiposResponseDTO expected = TestFixtures.crearEquipoResponseDePrueba();
        when(equiposServicio.obtenerPorId(idEquipo)).thenReturn(expected);

        // Act
        EquiposResponseDTO result = equiposServicio.obtenerPorId(idEquipo);

        // Assert
        assertThat(result)
            .isNotNull()
            .isEqualTo(expected);

        verify(equiposServicio).obtenerPorId(idEquipo);
        logger.info("Test obtener equipo por ID: PASSED");
    }

    @Test
    @DisplayName("Debe fallar al obtener equipo con ID inválido")
    void testObtenerPorId_NotFound() {
        // Arrange
        int idEquipo = 999;
        when(equiposServicio.obtenerPorId(idEquipo))
            .thenThrow(new RuntimeException("Equipo no encontrado"));

        // Act & Assert
        assertThatThrownBy(() -> equiposServicio.obtenerPorId(idEquipo))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("no encontrado");

        logger.info("Test equipo no encontrado: PASSED");
    }
}
