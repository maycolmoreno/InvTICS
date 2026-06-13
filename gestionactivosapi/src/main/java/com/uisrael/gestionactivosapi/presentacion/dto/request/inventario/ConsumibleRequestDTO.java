package com.uisrael.gestionactivosapi.presentacion.dto.request.inventario;

import jakarta.validation.constraints.NotBlank;

public class ConsumibleRequestDTO {
    @NotBlank
    private String codigo;
    @NotBlank
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private Boolean estado;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
}
