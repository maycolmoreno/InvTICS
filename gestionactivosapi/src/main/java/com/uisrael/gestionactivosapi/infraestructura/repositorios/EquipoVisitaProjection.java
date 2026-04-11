package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.time.LocalDateTime;

public interface EquipoVisitaProjection {

    int getIdEquipo();

    int getIdCustodio();

    String getSerial();

    String getMarca();

    String getModelo();

    String getCodigoSap();

    String getCustodioNombre();

    String getCustodioArea();

    String getUbicacionNombre();

    LocalDateTime getFechaUltimoMantenimiento();
}
