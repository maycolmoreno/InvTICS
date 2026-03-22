package com.uisrael.consumogestionactivosapi.modelo.dto.response;

import lombok.Data;

@Data
public class UsuariosResponseDTO {
	private int idUsuario;
	private String nombre;
	private String cedula;
	private String correo;
	private boolean estado;
	private DepartamentosResponseDTO fkDepartamento;
	private RolesResponseDTO fkRol;
}
