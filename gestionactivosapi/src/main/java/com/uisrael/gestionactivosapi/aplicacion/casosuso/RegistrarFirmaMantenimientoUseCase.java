package com.uisrael.gestionactivosapi.aplicacion.casosuso;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.dto.RegistrarFirmaCommand;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.FirmaMantenimientoJpa;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IFirmaMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

import lombok.RequiredArgsConstructor;

/**
 * Caso de uso: Registrar la firma de un mantenimiento vinculándola al usuario firmante.
 *
 * Validaciones:
 * - El mantenimiento debe existir.
 * - El usuario firmante debe existir.
 * - El tipo de firma debe ser válido (TECNICO o CUSTODIO).
 * - No debe existir ya una firma del mismo tipo para ese mantenimiento.
 */
@RequiredArgsConstructor
public class RegistrarFirmaMantenimientoUseCase {

    private final IFirmaMantenimientoJpaRepositorio firmaRepositorio;
    private final IMantenimientosJpaRepositorio mantenimientoRepositorio;
    private final IUsuariosJpaRepositorio usuarioRepositorio;

    @Transactional
    public FirmaMantenimientoJpa ejecutar(RegistrarFirmaCommand comando) {
        // 1. Validar que el mantenimiento existe
        if (!mantenimientoRepositorio.existsById(comando.mantenimientoId())) {
            throw new RecursoNoEncontradoException(
                    "Mantenimiento no encontrado con id: " + comando.mantenimientoId());
        }

        // 2. Validar que el usuario firmante existe
        if (!usuarioRepositorio.existsById(comando.firmadoPorId())) {
            throw new RecursoNoEncontradoException(
                    "Usuario firmante no encontrado con id: " + comando.firmadoPorId());
        }

        // 3. Parsear y validar el tipo de firma
        TipoFirma tipoFirma;
        try {
            tipoFirma = TipoFirma.valueOf(comando.tipoFirma().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Tipo de firma inválido: " + comando.tipoFirma()
                            + ". Valores permitidos: TECNICO, CUSTODIO");
        }

        // 4. Verificar que no exista ya una firma del mismo tipo para este mantenimiento
        firmaRepositorio.findByIdMantenimientoAndTipoFirma(comando.mantenimientoId(), tipoFirma)
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Ya existe una firma de tipo " + tipoFirma
                                    + " para el mantenimiento " + comando.mantenimientoId());
                });

        // 5. Crear y persistir la firma
        FirmaMantenimientoJpa firma = new FirmaMantenimientoJpa();
        firma.setIdMantenimiento(comando.mantenimientoId());
        firma.setFirmadoPorId(comando.firmadoPorId());
        firma.setTipoFirma(tipoFirma);
        firma.setFirmaBase64(comando.firmaBase64());
        firma.setFirmadoEn(LocalDateTime.now());
        firma.setIpOrigen(comando.ipOrigen());

        return firmaRepositorio.save(firma);
    }
}
