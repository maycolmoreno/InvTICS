package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ModuloJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolModuloJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;
import com.uisrael.gestionactivosapi.presentacion.dto.response.ModuloDTO;

@RestController
@RequestMapping("/api/modulos")
public class ModuloControlador {

    private final IModuloJpaRepositorio moduloRepo;
    private final IRolModuloJpaRepositorio rolModuloRepo;
    private final IRolesJpaRepositorio rolesRepo;
    private final EntityManager entityManager;

    public ModuloControlador(IModuloJpaRepositorio moduloRepo,
                             IRolModuloJpaRepositorio rolModuloRepo,
                             IRolesJpaRepositorio rolesRepo,
                             EntityManager entityManager) {
        this.moduloRepo = moduloRepo;
        this.rolModuloRepo = rolModuloRepo;
        this.rolesRepo = rolesRepo;
        this.entityManager = entityManager;
    }

    @GetMapping
    public List<ModuloDTO> listarModulos() {
        return moduloRepo.findByEstadoTrueOrderByOrdenAsc().stream()
                .map(m -> new ModuloDTO(
                        m.getIdModulo(), m.getCodigo(), m.getNombre(),
                        m.getIcono(), m.getRuta(), m.getOrden(), m.isEstado(), false))
                .toList();
    }

    @GetMapping("/por-rol/{rolId}")
    public List<ModuloDTO> listarModulosPorRol(@PathVariable int rolId) {
        List<ModuloJpa> todosModulos = moduloRepo.findByEstadoTrueOrderByOrdenAsc();
        Set<Integer> asignados = rolModuloRepo.findByRolId(rolId).stream()
                .map(rm -> rm.getModulo().getIdModulo())
                .collect(Collectors.toSet());

        return todosModulos.stream()
                .map(m -> new ModuloDTO(
                        m.getIdModulo(), m.getCodigo(), m.getNombre(),
                        m.getIcono(), m.getRuta(), m.getOrden(), m.isEstado(),
                        asignados.contains(m.getIdModulo())))
                .toList();
    }

    @PutMapping("/por-rol/{rolId}")
    @Transactional
    public ResponseEntity<String> actualizarModulosRol(@PathVariable int rolId,
                                                        @RequestBody List<Integer> moduloIds) {
        RolesJpa rol = rolesRepo.findById(rolId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        rolModuloRepo.deleteByRolIdRol(rolId);
        entityManager.flush();

        for (Integer moduloId : moduloIds) {
            ModuloJpa modulo = moduloRepo.findById(moduloId)
                    .orElseThrow(() -> new IllegalArgumentException("Módulo no encontrado: " + moduloId));
            RolModuloJpa rm = new RolModuloJpa();
            rm.setRol(rol);
            rm.setModulo(modulo);
            rolModuloRepo.save(rm);
        }

        return ResponseEntity.ok("Permisos actualizados");
    }
}
