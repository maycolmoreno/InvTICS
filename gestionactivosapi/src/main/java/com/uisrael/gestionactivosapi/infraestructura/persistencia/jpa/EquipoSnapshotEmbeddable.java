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

    @Column(name = "sine_snapshot", length = 50)
    private String sineSnapshot;

    @Column(name = "year_snapshoted")
    private Integer yearSnapshoted;
}
