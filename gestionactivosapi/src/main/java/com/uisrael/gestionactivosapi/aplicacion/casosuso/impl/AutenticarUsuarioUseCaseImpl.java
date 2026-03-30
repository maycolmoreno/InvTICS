package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IAutenticarUsuarioUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;

public class AutenticarUsuarioUseCaseImpl implements IAutenticarUsuarioUseCase {

	private final UsuarioRepositorioPuerto usuariosRepositorio;
	private final PasswordEncoder passwordEncoder;

	public AutenticarUsuarioUseCaseImpl(UsuarioRepositorioPuerto usuariosRepositorio, PasswordEncoder passwordEncoder) {
		this.usuariosRepositorio = usuariosRepositorio;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<Usuarios> ejecutar(String correo, String contrasena) {
		if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
			return Optional.empty();
		}

		return usuariosRepositorio.buscarPorCorreo(correo)
				.filter(Usuarios::isEstado)
				.filter(usuario -> contrasenaValida(contrasena, usuario.getContrasena()));
	}

	private boolean contrasenaValida(String contrasenaPlano, String contrasenaGuardada) {
		if (contrasenaGuardada == null || contrasenaGuardada.isBlank()) {
			return false;
		}

		if (contrasenaPlano.equals(contrasenaGuardada)) {
			return true;
		}

		return passwordEncoder.matches(contrasenaPlano, contrasenaGuardada);
	}
}
