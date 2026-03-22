package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CustodiosRequestDTO {

    private int idCustodio;
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correo;
    private String telefono;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaIngreso;

    private boolean estado;

    private DepartamentosRequestDTO fkDepartamento;

    private CargosRequestDTO fkCargo;

    private UbicacionesRequestDTO fkUbicacion;
}
