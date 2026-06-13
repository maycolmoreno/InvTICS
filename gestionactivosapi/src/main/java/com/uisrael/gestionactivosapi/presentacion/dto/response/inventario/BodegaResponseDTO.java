package com.uisrael.gestionactivosapi.presentacion.dto.response.inventario;

public class BodegaResponseDTO {
    private Integer idBodega;
    private String codigo;
    private String nombre;
    private String ciudad;
    private String direccion;
    private boolean estado;
    private Integer custodioResponsableId;
    private String custodioResponsableNombre;

    public Integer getIdBodega() { return idBodega; }
    public void setIdBodega(Integer idBodega) { this.idBodega = idBodega; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }
    public Integer getCustodioResponsableId() { return custodioResponsableId; }
    public void setCustodioResponsableId(Integer custodioResponsableId) { this.custodioResponsableId = custodioResponsableId; }
    public String getCustodioResponsableNombre() { return custodioResponsableNombre; }
    public void setCustodioResponsableNombre(String custodioResponsableNombre) { this.custodioResponsableNombre = custodioResponsableNombre; }
}
