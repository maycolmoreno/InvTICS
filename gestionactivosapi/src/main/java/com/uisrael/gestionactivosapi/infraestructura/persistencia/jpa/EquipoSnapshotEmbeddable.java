package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class EquipoSnapshotEmbeddable {

    @Column(name = "serie_snapshot", length = 120)
    private String serieSnapshot;

    @Column(name = "codigo_interno_snapshot", length = 50)
    private String codigoInternoSnapshot;

    @Column(name = "year_snapshoted")
    private Integer yearSnapshoted;
}
