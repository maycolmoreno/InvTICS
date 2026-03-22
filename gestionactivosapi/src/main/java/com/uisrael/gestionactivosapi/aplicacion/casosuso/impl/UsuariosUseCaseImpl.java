package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUsuariosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.entidades.Roles;
import com.uisrael.gestionactivosapi.dominio.entidades.Usuarios;
import com.uisrael.gestionactivosapi.dominio.repositorios.IDepartamentosRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.IRolesRepositorio;
import com.uisrael.gestionactivosapi.dominio.repositorios.IUsuariosRepositorio;
import com.uisrael.gestionactivosapi.dominio.validacion.CedulaEcuatorianaUtils;

public class UsuariosUseCaseImpl implements IUsuariosUseCase {

	private final IUsuariosRepositorio repositorio;
	private final IRolesRepositorio rolesRepositorio;
	private final IDepartamentosRepositorio departamentosRepositorio;
	private final PasswordEncoder passwordEncoder;

	public UsuariosUseCaseImpl(IUsuariosRepositorio repositorio, IRolesRepositorio rolesRepositorio,
			IDepartamentosRepositorio departamentosRepositorio, PasswordEncoder passwordEncoder) {
		this.repositorio = repositorio;
		this.rolesRepositorio = rolesRepositorio;
		this.departamentosRepositorio = departamentosRepositorio;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Usuarios crear(Usuarios usuario) {
		if (usuario == null) {
			throw new IllegalArgumentException("El usuario es obligatorio");
		}
		if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
			throw new IllegalArgumentException("El correo es obligatorio");
		}
		if (usuario.getCedula() == null || usuario.getCedula().isBlank()) {
			throw new IllegalArgumentException("La cedula es obligatoria");
		}
		if (!CedulaEcuatorianaUtils.esValida(usuario.getCedula())) {
			throw new IllegalArgumentException("La cedula debe ser ecuatoriana valida de 10 digitos");
		}
		if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
			throw new IllegalArgumentException("La contrasena es obligatoria");
		}

		if (!usuario.isEstado()) {
			throw new IllegalArgumentException("Solo se pueden crear usuarios en estado activo");
		}

		if (usuario.getFkRol() != null) {
			int idRol = usuario.getFkRol().getIdRol();
			var rolOpt = rolesRepositorio.buscarPorId(idRol);
			if (rolOpt.isEmpty()) {
				throw new RecursoNoEncontradoException("Rol no encontrado");
			}
			if (!rolOpt.get().isEstado()) {
				throw new IllegalArgumentException("No se puede asignar un rol inactivo");
			}
		}
		if (usuario.getFkDepartamento() != null) {
			int idDep = usuario.getFkDepartamento().getIdDepartamento();
			var depOpt = departamentosRepositorio.buscarPorId(idDep);
			if (depOpt.isEmpty()) {
				throw new RecursoNoEncontradoException("Departamento no encontrado");
			}
			if (!depOpt.get().isEstado()) {
				throw new IllegalArgumentException("No se puede asignar un departamento inactivo");
			}
		}

		if (repositorio.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
			throw new IllegalArgumentException("Ya existe un usuario con el correo: " + usuario.getCorreo());
		}

		String contrasenaEncriptada = passwordEncoder.encode(usuario.getContrasena());

		Roles rol = usuario.getFkRol();
		Departamentos departamento = usuario.getFkDepartamento();
		Usuarios usuarioConContrasenaEncriptada = new Usuarios(
			usuario.getIdUsuario(),
			usuario.getNombre(),
			usuario.getCedula(),
			usuario.getCorreo(),
			contrasenaEncriptada,
			usuario.isEstado(),
			departamento,
			rol
		);

		return repositorio.guardar(usuarioConContrasenaEncriptada);
	}

	@Override
	public Usuarios obtenerPorId(int id) {
		return repositorio.buscarPorId(id).orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
	}

	@Override
	public List<Usuarios> listar() {
		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {
		Usuarios usuario = repositorio.buscarPorId(id)
			.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));

		if (!usuario.isEstado()) {
			throw new IllegalArgumentException("Este usuario ya se encuentra inactivo");
		}

		Usuarios usuarioInactivo = new Usuarios(
			usuario.getIdUsuario(),
			usuario.getNombre(),
			usuario.getCedula(),
			usuario.getCorreo(),
			usuario.getContrasena(),
			false,
			usuario.getFkDepartamento(),
			usuario.getFkRol()
		);
		repositorio.guardar(usuarioInactivo);
	}

	@Override
	public Usuarios actualizar(Usuarios usuario) {
		if (usuario == null || usuario.getIdUsuario() <= 0) {
			throw new IllegalArgumentException("El usuario a actualizar es invalido");
		}
		if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {
			throw new IllegalArgumentException("El correo es obligatorio");
		}
		if (usuario.getCedula() == null || usuario.getCedula().isBlank()) {
			throw new IllegalArgumentException("La cedula es obligatoria");
		}
		if (!CedulaEcuatorianaUtils.esValida(usuario.getCedula())) {
			throw new IllegalArgumentException("La cedula debe ser ecuatoriana valida de 10 digitos");
		}

		Usuarios usuarioExistente = repositorio.buscarPorId(usuario.getIdUsuario())
			.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + usuario.getIdUsuario()));

		if (usuario.getFkRol() != null) {
			int idRol = usuario.getFkRol().getIdRol();
			var rolOpt = rolesRepositorio.buscarPorId(idRol);
			if (rolOpt.isEmpty()) {
				throw new RecursoNoEncontradoException("Rol no encontrado");
			}
			if (!rolOpt.get().isEstado()) {
				throw new IllegalArgumentException("No se puede asignar un rol inactivo");
			}
		}
		if (usuario.getFkDepartamento() != null) {
			int idDep = usuario.getFkDepartamento().getIdDepartamento();
			var depOpt = departamentosRepositorio.buscarPorId(idDep);
			if (depOpt.isEmpty()) {
				throw new RecursoNoEncontradoException("Departamento no encontrado");
			}
			if (!depOpt.get().isEstado()) {
				throw new IllegalArgumentException("No se puede asignar un departamento inactivo");
			}
		}

		repositorio.buscarPorCorreo(usuario.getCorreo()).ifPresent(usuarioConCorreo -> {
			if (usuarioConCorreo.getIdUsuario() != usuario.getIdUsuario()) {
				throw new IllegalArgumentException("Ya existe otro usuario con el correo: " + usuario.getCorreo());
			}
		});

		String contrasenaFinal = usuarioExistente.getContrasena();
		if (usuario.getContrasena() != null && !usuario.getContrasena().isEmpty()
			&& !usuario.getContrasena().equals(usuarioExistente.getContrasena())) {
			contrasenaFinal = passwordEncoder.encode(usuario.getContrasena());
		}

		Roles rol = usuario.getFkRol();
		Departamentos departamento = usuario.getFkDepartamento();
		Usuarios usuarioActualizado = new Usuarios(
			usuario.getIdUsuario(),
			usuario.getNombre(),
			usuario.getCedula(),
			usuario.getCorreo(),
			contrasenaFinal,
			usuario.isEstado(),
			departamento,
			rol
		);

		return repositorio.guardar(usuarioActualizado);
	}

}
