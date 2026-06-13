package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

public class ConsumibleResponseDTO {
    private Integer idConsumible;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String unidadMedida;
    private boolean estado;

    public Integer getIdConsumible() { return idConsumible; }
    public void setIdConsumible(Integer idConsumible) { this.idConsumible = idConsumible; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
}
