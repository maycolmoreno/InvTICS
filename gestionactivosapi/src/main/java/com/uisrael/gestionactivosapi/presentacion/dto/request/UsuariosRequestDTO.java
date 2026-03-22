package com.uisrael.gestionactivosapi.presentacion.dto.request;

import com.uisrael.gestionactivosapi.presentacion.validacion.CedulaEcuatoriana;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UsuariosRequestDTO {

    private int idUsuario;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "La cédula es obligatoria")
    @CedulaEcuatoriana
    private String cedula;

    @NotBlank(message = "El correo es obligatorio")
    private String correo;

    // No requerida en edicion - puede estar vacia si no se cambia
    private String contrasena;

    private boolean estado;

    private DepartamentosRequestDTO fkDepartamento;

    private RolesRequestDTO fkRol;
}
