package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;

public interface ICustodiosRepositorio {

	Custodios guardar(Custodios custodio);

	Optional<Custodios> buscarPorId(int id);

	List<Custodios> listarTodos();

	Custodios actualizar(int id, Custodios custodio);

	Custodios actualizarEstado(int id, Custodios custodio);

	boolean existeCorreo(String correo);

	boolean existeCorreoParaOtro(String correo, int idCustodio);

	boolean existeCedula(String cedula);

	boolean existeCedulaParaOtro(String cedula, int idCustodio);

	Custodios vincularUsuario(int idCustodio, int idUsuario);

	boolean existeUsuarioVinculado(int idUsuario);

	boolean existeUsuarioVinculadoEnOtroCustodio(int idUsuario, int idCustodio);
}
