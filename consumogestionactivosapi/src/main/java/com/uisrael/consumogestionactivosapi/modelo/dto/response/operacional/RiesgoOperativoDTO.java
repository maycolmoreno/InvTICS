package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiesgoOperativoDTO {
    private String titulo;
    private String descripcion;
    private String severidad;
    private String href;
}
