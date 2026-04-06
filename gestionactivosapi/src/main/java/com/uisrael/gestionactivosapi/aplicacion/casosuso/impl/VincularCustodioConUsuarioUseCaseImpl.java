package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import org.springframework.transaction.annotation.Transactional;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVincularCustodioConUsuarioUseCase;
import com.uisrael.gestionactivosapi.dominio.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;

public class VincularCustodioConUsuarioUseCaseImpl implements IVincularCustodioConUsuarioUseCase {

    private final CustodioRepositorioPuerto custodioRepositorio;
    private final UsuarioRepositorioPuerto usuarioRepositorio;

    public VincularCustodioConUsuarioUseCaseImpl(CustodioRepositorioPuerto custodioRepositorio,
            UsuarioRepositorioPuerto usuarioRepositorio) {
        this.custodioRepositorio = custodioRepositorio;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    @Transactional
    public Custodios ejecutar(int idCustodio, int idUsuario) {
        Custodios custodio = custodioRepositorio.buscarPorId(idCustodio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
        Usuarios usuario = usuarioRepositorio.buscarPorId(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        if (custodioRepositorio.existeUsuarioVinculadoEnOtroCustodio(idUsuario, idCustodio)) {
            throw new IllegalArgumentException("El usuario ya esta vinculado a otro custodio");
        }

        custodio.setFkUsuario(usuario);
        return custodioRepositorio.vincularUsuario(idCustodio, idUsuario);
    }
}
