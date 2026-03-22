package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVincularCustodioConUsuarioUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICustodiosRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.IUsuariosRepositorio;

public class VincularCustodioConUsuarioUseCaseImpl implements IVincularCustodioConUsuarioUseCase {

    private final ICustodiosRepositorio custodiosRepositorio;
    private final IUsuariosRepositorio usuariosRepositorio;

    public VincularCustodioConUsuarioUseCaseImpl(ICustodiosRepositorio custodiosRepositorio,
            IUsuariosRepositorio usuariosRepositorio) {
        this.custodiosRepositorio = custodiosRepositorio;
        this.usuariosRepositorio = usuariosRepositorio;
    }

    @Override
    @Transactional
    public Custodios ejecutar(int idCustodio, int idUsuario) {
        Custodios custodio = custodiosRepositorio.buscarPorId(idCustodio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
        Usuarios usuario = usuariosRepositorio.buscarPorId(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (custodiosRepositorio.existeUsuarioVinculadoEnOtroCustodio(idUsuario, idCustodio)) {
            throw new IllegalArgumentException("El usuario ya esta vinculado a otro custodio");
        }

        custodio.setFkUsuario(usuario);
        return custodiosRepositorio.vincularUsuario(idCustodio, idUsuario);
    }
}
