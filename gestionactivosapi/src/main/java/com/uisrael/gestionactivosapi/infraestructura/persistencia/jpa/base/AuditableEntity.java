package com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.base;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", length = 100, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 100)
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Realiza soft delete: establece la fecha de eliminación en lugar de borrar físicamente.
     * Las entidades con @SQLRestriction("deleted_at IS NULL") quedarán filtradas automáticamente.
     */
    public void eliminar() {
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Indica si la entidad ha sido eliminada lógicamente.
     */
    public boolean estaEliminada() {
        return this.deletedAt != null;
    }

    /**
     * Restaura una entidad eliminada lógicamente.
     */
    public void restaurar() {
        this.deletedAt = null;
    }
}
