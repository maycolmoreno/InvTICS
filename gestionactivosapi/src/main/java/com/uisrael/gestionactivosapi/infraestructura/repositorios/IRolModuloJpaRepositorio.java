package com.uisrael.gestionactivosapi.infraestructura.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolModuloJpa;

public interface IRolModuloJpaRepositorio extends JpaRepository<RolModuloJpa, Long> {

    @Query("SELECT rm FROM RolModuloJpa rm WHERE rm.rol.idRol = :rolId ORDER BY rm.modulo.orden ASC")
    List<RolModuloJpa> findByRolId(@Param("rolId") int rolId);

    @Query("SELECT rm.modulo.codigo FROM RolModuloJpa rm WHERE rm.rol.nombre = :rolNombre AND rm.modulo.estado = true")
    List<String> findCodigosModulosByRolNombre(@Param("rolNombre") String rolNombre);

    @Modifying
    @Query("DELETE FROM RolModuloJpa rm WHERE rm.rol.idRol = :rolId")
    void deleteByRolIdRol(@Param("rolId") int rolId);
}
