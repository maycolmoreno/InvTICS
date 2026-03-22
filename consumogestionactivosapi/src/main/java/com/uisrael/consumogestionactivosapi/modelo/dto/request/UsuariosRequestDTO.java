package com.uisrael.consumogestionactivosapi.modelo.dto.request;

import lombok.Data;

@Data
public class UsuariosRequestDTO {
	private int idUsuario;
	private String nombre;
	private String cedula;
	private String correo;
	private String contrasena;
	private boolean estado;
	private DepartamentosRequestDTO fkDepartamento;
	private RolesRequestDTO fkRol;
}
