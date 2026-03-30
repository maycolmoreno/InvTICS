package com.uisrael.consumogestionactivosapi.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uisrael.consumogestionactivosapi.ConsumogestionactivosapiApplication;
import com.uisrael.consumogestionactivosapi.TestFixtures;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

/**
 * Suite de pruebas de integración para la API de Equipos.
 * Carga el contexto completo de Spring Boot y prueba la API con llamadas reales.
 * 
 * Configura el perfil "test" para usar propiedades de prueba.
 */
@SpringBootTest(
    classes = ConsumogestionactivosapiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@DisplayName("EquiposIntegrationTest - Integration Tests")
class EquiposIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(EquiposIntegrationTest.class);

    @Value("${local.server.port}")
    private int port;

    private RestTemplate restTemplate;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        baseUrl = "http://localhost:" + port;
        logger.info("Inicializando prueba de integración");
        assertThat(restTemplate).isNotNull();
    }

    @Test
    @DisplayName("Debe retornar OK en listar equipos")
    void testListarEquipos_ReturnsOk() {
        // Arrange & Act
        ResponseEntity<List> response = restTemplate.getForEntity(
            baseUrl + "/api/equipos",
            List.class
        );

        // Assert
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.OK);

        assertThat(response.getBody())
            .isNotNull();

        logger.info("Test listar equipos endpoint: PASSED");
    }

    @Test
    @DisplayName("Debe crear equipo con datos válidos")
    void testCrearEquipo_ReturnsCreated() {
        // Arrange
        EquiposRequestDTO request = TestFixtures.crearEquipoRequestDePrueba();

        // Act
        ResponseEntity<EquiposResponseDTO> response = restTemplate.postForEntity(
            baseUrl + "/api/equipos",
            request,
            EquiposResponseDTO.class
        );

        // Assert
        assertThat(response.getStatusCode())
            .isIn(HttpStatus.CREATED, HttpStatus.OK);

        logger.info("Test crear equipo endpoint: PASSED");
    }

    @Test
    @DisplayName("Debe retornar 404 al obtener equipo inexistente")
    void testObtenerEquipo_NotFound() {
        // Arrange & Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/api/equipos/999",
            String.class
        );

        // Assert
        assertThat(response.getStatusCode())
            .isEqualTo(HttpStatus.NOT_FOUND);

        logger.info("Test obtener equipo inexistente: PASSED");
    }

    @Test
    @DisplayName("Debe validar datos obligatorios en creación")
    void testCrearEquipo_InvalidData_ReturnsBadRequest() {
        // Arrange
        EquiposRequestDTO invalidRequest = new EquiposRequestDTO();
        // Campo requerido vacío

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/api/equipos",
            invalidRequest,
            String.class
        );

        // Assert
        assertThat(response.getStatusCode())
            .isIn(HttpStatus.BAD_REQUEST, HttpStatus.UNPROCESSABLE_ENTITY);

        logger.info("Test validación de datos: PASSED");
    }
}
