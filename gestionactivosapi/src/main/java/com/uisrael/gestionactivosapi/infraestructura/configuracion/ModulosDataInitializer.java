package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.ModuloJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolModuloJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.RolesJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolModuloJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ModulosDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ModulosDataInitializer.class);

    private final IModuloJpaRepositorio moduloRepo;
    private final IRolModuloJpaRepositorio rolModuloRepo;
    private final IRolesJpaRepositorio rolesRepo;

    public ModulosDataInitializer(IModuloJpaRepositorio moduloRepo,
                                   IRolModuloJpaRepositorio rolModuloRepo,
                                   IRolesJpaRepositorio rolesRepo) {
        this.moduloRepo = moduloRepo;
        this.rolModuloRepo = rolModuloRepo;
        this.rolesRepo = rolesRepo;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (moduloRepo.count() == 0) {
            log.info("Inicializando datos de modulos...");
            crearModulo("EQUIPOS", "Equipos", "feather-box", "/equipos", 1);
            crearModulo("MANTENIMIENTO", "Mantenimiento", "feather-tool", "/mantenimiento", 2);
            crearModulo("CUSTODIAS", "Custodias", "feather-clipboard", "/custodias", 3);
            crearModulo("CUSTODIOS", "Custodios", "feather-user-check", "/custodios", 4);
            crearModulo("UBICACIONES", "Ubicaciones", "feather-map-pin", "/ubicaciones", 5);
            crearModulo("DEPARTAMENTOS", "Departamentos", "feather-home", "/departamentos", 6);
            crearModulo("USUARIOS", "Usuarios", "feather-users", "/usuarios", 7);
            crearModulo("REPORTES", "Reportes", "feather-bar-chart-2", "/equipos/reporte-equipo", 8);
            crearModulo("NOTIFICACIONES", "Notificaciones", "feather-bell", "/notificaciones", 9);
            crearModulo("CHECKLIST", "Checklist", "feather-check-square", "/checklist", 10);
            crearModulo("PLANIFICACION", "Planificación", "feather-calendar", "/planificacion", 11);
            crearModulo("MONITOREO_GPS", "Monitoreo GPS", "feather-navigation", "/ubicaciones-tecnicos/consentimiento", 12);
            crearModulo("GPS_TIEMPO_REAL", "GPS Tiempo Real", "feather-radio", "/ubicaciones-tecnicos/tiempo-real", 13);
            crearModulo("GPS_HISTORIAL", "GPS Historial", "feather-clock", "/ubicaciones-tecnicos/historial", 14);
            crearModulo("IMPORTAR", "Importar", "feather-upload-cloud", "/importar", 15);
            crearModulo("ROLES", "Roles", "feather-shield", "/roles", 16);
            crearModulo("CARGOS", "Cargos", "feather-briefcase", "/cargos", 17);
            crearModulo("MARCAS", "Marcas", "feather-tag", "/marcas", 18);
            crearModulo("CATEGORIAS", "Categorias", "feather-layers", "/categorias-equipo", 19);
            crearModulo("VISITA_TECNICA", "Visita Técnica", "feather-map", "/visita", 20);
            crearModulo("INVENTARIO", "Inventario", "feather-archive", "/inventario/por-sucursal", 21);
            log.info("Modulos creados correctamente.");
        } else {
            // Crear módulos faltantes
            crearModuloSiFalta("INVENTARIO", "Inventario", "feather-archive", "/inventario/por-sucursal", 21);
        }

        // Siempre verificar y completar asignaciones de módulos a roles
        inicializarAsignacionesRoles();
    }

    /**
     * Verifica y crea las asignaciones de módulos a roles que falten.
     * Puede invocarse después de crear roles (ej. setup inicial).
     */
    @Transactional
    public void inicializarAsignacionesRoles() {
        List<ModuloJpa> todosModulos = moduloRepo.findByEstadoTrueOrderByOrdenAsc();
        if (todosModulos.isEmpty()) {
            return;
        }

        // Módulos para TECNICO: su trabajo es mantenimiento (web) y campo (móvil).
        // EQUIPOS y NOTIFICACIONES se conservan porque la app móvil deriva sus
        // capacidades de estos códigos; la web oculta EQUIPOS para este rol.
        Set<String> modulosTecnico = Set.of(
            "EQUIPOS", "MANTENIMIENTO", "NOTIFICACIONES", "CHECKLIST",
            "PLANIFICACION", "MONITOREO_GPS", "VISITA_TECNICA"
        );

        // Módulos para AUDITOR
        Set<String> modulosAuditor = Set.of("REPORTES", "INVENTARIO");

        Map<String, Set<String>> modulosPorRol = Map.of(
            "ADMINISTRADOR", Set.of(), // vacío = todos
            "TECNICO", modulosTecnico,
            "AUDITOR", modulosAuditor
        );

        for (Map.Entry<String, Set<String>> entry : modulosPorRol.entrySet()) {
            String nombreRol = entry.getKey();
            Set<String> codigosPermitidos = entry.getValue();

            Optional<RolesJpa> rolOpt = rolesRepo.findByNombre(nombreRol);
            if (rolOpt.isEmpty()) {
                continue;
            }

            RolesJpa rol = rolOpt.get();
            List<RolModuloJpa> asignacionesExistentes = rolModuloRepo.findByRolId(rol.getIdRol());
            if (!asignacionesExistentes.isEmpty()) {
                continue; // ya tiene asignaciones, no sobrescribir
            }

            boolean esAdmin = "ADMINISTRADOR".equals(nombreRol);
            int count = 0;
            for (ModuloJpa modulo : todosModulos) {
                if (esAdmin || codigosPermitidos.contains(modulo.getCodigo())) {
                    asignarModulo(rol, modulo);
                    count++;
                }
            }
            log.info("Asignados {} modulos al rol {}", count, nombreRol);
        }
    }

    private ModuloJpa crearModulo(String codigo, String nombre, String icono, String ruta, int orden) {
        ModuloJpa modulo = new ModuloJpa();
        modulo.setCodigo(codigo);
        modulo.setNombre(nombre);
        modulo.setIcono(icono);
        modulo.setRuta(ruta);
        modulo.setOrden(orden);
        modulo.setEstado(true);
        return moduloRepo.save(modulo);
    }

    private void crearModuloSiFalta(String codigo, String nombre, String icono, String ruta, int orden) {
        Optional<ModuloJpa> existente = moduloRepo.findAll().stream()
                .filter(m -> codigo.equals(m.getCodigo()))
                .findFirst();
        if (existente.isEmpty()) {
            log.info("Creando modulo faltante: {}", codigo);
            crearModulo(codigo, nombre, icono, ruta, orden);
        }
    }

    private void asignarModulo(RolesJpa rol, ModuloJpa modulo) {
        RolModuloJpa rm = new RolModuloJpa();
        rm.setRol(rol);
        rm.setModulo(modulo);
        rolModuloRepo.save(rm);
    }
}
