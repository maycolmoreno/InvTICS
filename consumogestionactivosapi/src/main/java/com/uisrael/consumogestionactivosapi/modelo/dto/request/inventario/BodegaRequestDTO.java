package com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario;

public class BodegaRequestDTO {
    private String codigo;
    private String nombre;
    private String ciudad;
    private String direccion;
    private Boolean estado = true;
    private Integer custodioResponsableId;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }
    public Integer getCustodioResponsableId() { return custodioResponsableId; }
    public void setCustodioResponsableId(Integer custodioResponsableId) { this.custodioResponsableId = custodioResponsableId; }
}
