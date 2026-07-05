package com.uisrael.gestionactivosapi.presentacion.dto.request;

import java.time.LocalDate;

import com.uisrael.gestionactivosapi.presentacion.validacion.CedulaEcuatoriana;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustodiosRequestDTO {

    private int idCustodio;

    @NotBlank
    private String nombre;

    @NotBlank
    @CedulaEcuatoriana
    private String cedula;

    private String correo;
    private String telefono;

    private LocalDate fechaIngreso;

    private boolean estado;

    private DepartamentosRequestDTO fkDepartamento;

    private CargosRequestDTO fkCargo;

    private UbicacionesRequestDTO fkUbicacion;

    /** Cargo/departamento tal cual vienen del directorio institucional externo, texto libre sin catalogo. */
    private String cargoDirectorio;
    private String departamentoDirectorio;
}
