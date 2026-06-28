package com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario;

import java.util.ArrayList;
import java.util.List;

import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;

public class AsignacionActivosResponseDTO {

    private List<ActivoInventarioResponseDTO> activos = new ArrayList<>();
    private List<CustodiasResponseDTO> custodias = new ArrayList<>();

    public List<ActivoInventarioResponseDTO> getActivos() {
        return activos;
    }

    public void setActivos(List<ActivoInventarioResponseDTO> activos) {
        this.activos = activos;
    }

    public List<CustodiasResponseDTO> getCustodias() {
        return custodias;
    }

    public void setCustodias(List<CustodiasResponseDTO> custodias) {
        this.custodias = custodias;
    }
}
