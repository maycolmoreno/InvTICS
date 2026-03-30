package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.comandos.CrearAdminSetupCommand;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ISetupInicialUseCase;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;

public class SetupInicialUseCaseImpl implements ISetupInicialUseCase {

	private static final String DEPTO_DEFAULT = "Transformacion Digital";

	private final UsuarioRepositorioPuerto usuariosRepositorio;
	private final RolRepositorioPuerto rolesRepositorio;
	private final DepartamentoRepositorioPuerto departamentosRepositorio;
	private final PasswordEncoder passwordEncoder;

	public SetupInicialUseCaseImpl(UsuarioRepositorioPuerto usuariosRepositorio,
			RolRepositorioPuerto rolesRepositorio,
			DepartamentoRepositorioPuerto departamentosRepositorio,
			PasswordEncoder passwordEncoder) {
		this.usuariosRepositorio = usuariosRepositorio;
		this.rolesRepositorio = rolesRepositorio;
		this.departamentosRepositorio = departamentosRepositorio;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public boolean esNecesario() {
		return usuariosRepositorio.listarTodos().stream()
				.filter(Usuarios::isEstado)
				.noneMatch(usuario -> usuario.getFkRol() != null
						&& "ADMINISTRADOR".equalsIgnoreCase(usuario.getFkRol().getNombre()));
	}

	@Override
	public void crearAdmin(CrearAdminSetupCommand command) {
		if (command == null) {
			throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio.");
		}
		if (!esNecesario()) {
			throw new IllegalStateException("Ya existe un administrador. Setup no permitido.");
		}

		String nombre = textoObligatorio(command.nombre(), "Nombre, correo y contrasena son obligatorios.");
		String correo = textoObligatorio(command.correo(), "Nombre, correo y contrasena son obligatorios.");
		String contrasena = textoObligatorio(command.contrasena(), "Nombre, correo y contrasena son obligatorios.");
		String cedula = (command.cedula() == null || command.cedula().isBlank()) ? "0000000000" : command.cedula();

		Roles rolAdmin = rolesRepositorio.buscarPorNombre("ADMINISTRADOR")
				.orElseGet(() -> rolesRepositorio.guardar(new Roles(0, "ADMINISTRADOR", true)));
		if (rolesRepositorio.buscarPorNombre("TECNICO").isEmpty()) {
			rolesRepositorio.guardar(new Roles(0, "TECNICO", true));
		}
		if (rolesRepositorio.buscarPorNombre("AUDITOR").isEmpty()) {
			rolesRepositorio.guardar(new Roles(0, "AUDITOR", true));
		}

		Departamentos depto = obtenerOCrearDepartamentoDefault();
		String contrasenaEncriptada = passwordEncoder.encode(contrasena);
		Usuarios admin = new Usuarios(0, nombre, cedula, correo, contrasenaEncriptada, true, depto, rolAdmin);
		usuariosRepositorio.guardar(admin);
	}

	private Departamentos obtenerOCrearDepartamentoDefault() {
		if (departamentosRepositorio.existeNombre(DEPTO_DEFAULT)) {
			Departamentos existente = departamentosRepositorio.listarTodos().stream()
					.filter(d -> DEPTO_DEFAULT.equals(d.getNombre()))
					.findFirst()
					.orElse(null);
			if (existente != null) {
				return existente;
			}
		}
		return departamentosRepositorio.guardar(new Departamentos(0, DEPTO_DEFAULT, true, null));
	}

	private String textoObligatorio(String valor, String mensaje) {
		if (valor == null || valor.isBlank()) {
			throw new IllegalArgumentException(mensaje);
		}
		return valor;
	}
}
