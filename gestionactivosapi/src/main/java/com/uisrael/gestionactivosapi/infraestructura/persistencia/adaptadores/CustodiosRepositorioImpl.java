package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICustodiosRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CargosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.CustodiosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UsuariosJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;

public class CustodiosRepositorioImpl implements ICustodiosRepositorio {

    private final ICustodiosJpaRepositorio jpaRepository;
    private final ICustodiosJpaMapper entityMapper;

    public CustodiosRepositorioImpl(ICustodiosJpaRepositorio jpaRepository, ICustodiosJpaMapper entityMapper) {
        this.jpaRepository = jpaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Custodios guardar(Custodios custodio) {
        CustodiosJpa entity = entityMapper.toEntity(custodio);
        CustodiosJpa guardado = jpaRepository.save(entity);
        return entityMapper.toDomain(guardado);
    }

    @Override
    public Optional<Custodios> buscarPorId(int id) {
        return jpaRepository.findById(id).map(entityMapper::toDomain);
    }

    @Override
    public List<Custodios> listarTodos() {
        return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
    }

    @Override
    public Custodios actualizar(int id, Custodios custodio) {
        CustodiosJpa existente = jpaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));

        existente.setNombre(custodio.getNombre());
        existente.setCedula(custodio.getCedula());
        existente.setCorreo(custodio.getCorreo());
        existente.setTelefono(custodio.getTelefono());
        existente.setEstado(custodio.isEstado());
        existente.setFechaIngreso(custodio.getFechaIngreso());

		if (custodio.getFkCargo() != null) {
			CargosJpa car = new CargosJpa();
			car.setIdCargo(custodio.getFkCargo().getIdCargo());
			existente.setFkCargo(car);
		}

		if (custodio.getFkUbicacion() != null) {
			UbicacionesJpa ub = new UbicacionesJpa();
			ub.setIdUbicacion(custodio.getFkUbicacion().getIdUbicacion());
			existente.setFkUbicacion(ub);
		} else {
			existente.setFkUbicacion(null);
		}

        if (custodio.getFkUsuario() != null) {
            UsuariosJpa usuario = new UsuariosJpa();
            usuario.setIdUsuario(custodio.getFkUsuario().getIdUsuario());
            existente.setFkUsuario(usuario);
        } else {
            existente.setFkUsuario(null);
        }

        CustodiosJpa guardado = jpaRepository.save(existente);
        return entityMapper.toDomain(guardado);
    }

    @Override
    public Custodios actualizarEstado(int id, Custodios custodio) {
        CustodiosJpa existente = jpaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));

        existente.setEstado(custodio.isEstado());

        CustodiosJpa guardado = jpaRepository.save(existente);
        return entityMapper.toDomain(guardado);
    }

	@Override
	public boolean existeCorreo(String correo) {
		return jpaRepository.existsByCorreoIgnoreCase(correo);
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		return jpaRepository.existsByCorreoIgnoreCaseAndIdCustodioNot(correo, idCustodio);
	}

	@Override
	public boolean existeCedula(String cedula) {
		return jpaRepository.existsByCedulaIgnoreCase(cedula);
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		return jpaRepository.existsByCedulaIgnoreCaseAndIdCustodioNot(cedula, idCustodio);

	}

    @Override
    public Custodios vincularUsuario(int idCustodio, int idUsuario) {
        CustodiosJpa custodio = jpaRepository.findById(idCustodio)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
        UsuariosJpa usuario = new UsuariosJpa();
        usuario.setIdUsuario(idUsuario);
        custodio.setFkUsuario(usuario);
        return entityMapper.toDomain(jpaRepository.save(custodio));
    }

    @Override
    public boolean existeUsuarioVinculado(int idUsuario) {
        return jpaRepository.existsByFkUsuario_IdUsuario(idUsuario);
    }

    @Override
    public boolean existeUsuarioVinculadoEnOtroCustodio(int idUsuario, int idCustodio) {
        return jpaRepository.existsByFkUsuario_IdUsuarioAndIdCustodioNot(idUsuario, idCustodio);
    }
}
