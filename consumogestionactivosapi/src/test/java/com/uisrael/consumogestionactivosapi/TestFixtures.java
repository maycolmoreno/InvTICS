package com.uisrael.consumogestionactivosapi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CargosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;

/**
 * Clase de utilidad con datos reutilizables para pruebas unitarias e integración.
 * Proporciona métodos factory para crear objetos DTO con datos de prueba válidos.
 */
public class TestFixtures {

    /**
     * Crea un EquiposResponseDTO de prueba válido
     */
    public static EquiposResponseDTO crearEquipoResponseDePrueba() {
        EquiposResponseDTO dto = new EquiposResponseDTO();
        dto.setIdEquipo(1);
        dto.setCodigoSap("SAP-001");
        dto.setTipoEquipo("Laptop");
        dto.setModelo("ThinkPad X13");
        dto.setSerial("SN123456");
        dto.setProcesador("Intel Core i7");
        dto.setMemoriaRamGb(16);
        dto.setCapacidadAlmacenamientoGb(512);
        dto.setSistemaOperativo("Windows 10");
        dto.setLicenciaWindowsActivada(true);
        dto.setEtiquetaActivoFijo(true);
        dto.setEstadoEquipo("Activo");
        dto.setEstado(true);
        return dto;
    }

    /**
     * Crea un EquiposRequestDTO de prueba válido
     */
    public static EquiposRequestDTO crearEquipoRequestDePrueba() {
        EquiposRequestDTO dto = new EquiposRequestDTO();
        dto.setCodigoSap("SAP-001");
        dto.setTipoEquipo("Laptop");
        dto.setModelo("ThinkPad X13");
        dto.setSerial("SN123456");
        dto.setProcesador("Intel Core i7");
        dto.setMemoriaRamGb(16);
        dto.setCapacidadAlmacenamientoGb(512);
        dto.setSistemaOperativo("Windows 10");
        dto.setLicenciaWindowsActivada(true);
        dto.setEtiquetaActivoFijo(true);
        dto.setEstadoEquipo("Activo");
        dto.setEstado(true);
        dto.setFechaCompra(LocalDate.now());
        dto.setPrecioCompra(new BigDecimal("1500.00"));
        dto.setFkMarca(new MarcasRequestDTO());
        dto.getFkMarca().setIdMarca(1);
        dto.setFkCategoria(new CategoriaEquiposRequestDTO());
        dto.getFkCategoria().setIdCategoria(1);
        return dto;
    }

    /**
     * Crea una lista de EquiposResponseDTO para pruebas
     */
    public static List<EquiposResponseDTO> crearListaEquiposDePrueba() {
        List<EquiposResponseDTO> lista = new ArrayList<>();
        
        EquiposResponseDTO equipo1 = crearEquipoResponseDePrueba();
        equipo1.setIdEquipo(1);
        lista.add(equipo1);
        
        EquiposResponseDTO equipo2 = crearEquipoResponseDePrueba();
        equipo2.setIdEquipo(2);
        equipo2.setCodigoSap("SAP-002");
        lista.add(equipo2);
        
        return lista;
    }

    /**
     * Crea un MarcasRequestDTO de prueba válido
     */
    public static MarcasRequestDTO crearMarcaDePrueba() {
        MarcasRequestDTO dto = new MarcasRequestDTO();
        dto.setNombre("Lenovo");
        dto.setEstado(true);
        return dto;
    }

    /**
     * Crea un CargosRequestDTO de prueba válido
     */
    public static CargosRequestDTO crearCargoDePrueba() {
        CargosRequestDTO dto = new CargosRequestDTO();
        dto.setNombre("Tecnico de TI");
        dto.setEstado(true);
        return dto;
    }

    /**
     * Crea un CategoriaEquiposRequestDTO de prueba válido
     */
    public static CategoriaEquiposRequestDTO crearCategoriaDePrueba() {
        CategoriaEquiposRequestDTO dto = new CategoriaEquiposRequestDTO();
        dto.setNombre("Computadoras de Escritorio");
        dto.setEstado(true);
        return dto;
    }
}
