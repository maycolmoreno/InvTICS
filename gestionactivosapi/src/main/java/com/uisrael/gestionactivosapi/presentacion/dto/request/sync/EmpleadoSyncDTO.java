package com.uisrael.gestionactivosapi.presentacion.dto.request.sync;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Empleado tal como llega desde la fuente externa (JSON).
 *
 * Formato esperado (array raiz o bajo la clave "empleados"):
 * <pre>
 * [{ "cedula": "0102030405", "nombre": "Ana Perez", "correo": "ana@uisrael.edu.ec",
 *    "telefono": "0991234567", "cargo": "Analista TIC",
 *    "departamento": "TECNOLOGIAS E INNOVACION", "activo": true }]
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoSyncDTO {

    private String cedula;
    private String nombre;
    private String correo;
    private String telefono;
    private String cargo;
    private String departamento;
    private Boolean activo;
}
