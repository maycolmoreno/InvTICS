package com.uisrael.gestionactivosapi.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.dominio.repositorios.IUbicacionesRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.jpa.UbicacionesJpa;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUbicacionesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

public class UbicacionesRepositorioImpl implements IUbicacionesRepositorio {

	private final IUbicacionesJpaRepositorio jpaRepository;

	private final IUbicacionesJpaMapper entityMapper;



	public UbicacionesRepositorioImpl(IUbicacionesJpaRepositorio jpaRepository, IUbicacionesJpaMapper entityMapper) {
		super();
		this.jpaRepository = jpaRepository;
		this.entityMapper = entityMapper;
	}

	@Override
	public Ubicaciones guardar(Ubicaciones ubicacion) {
		UbicacionesJpa entity = entityMapper.toEntity(ubicacion);
		UbicacionesJpa guardado = jpaRepository.save(entity);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Optional<Ubicaciones> buscarPorId(int id) {
		return jpaRepository.findById(id).map(entityMapper::toDomain);
	}

	@Override
	public List<Ubicaciones> listarTodos() {
		return jpaRepository.findAll().stream().map(entityMapper::toDomain).toList();
	}

	@Override
	public Ubicaciones actualizar(int id, Ubicaciones ubicacion) {
		UbicacionesJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicacion no encontrada"));

		existente.setNombre(ubicacion.getNombre());
		existente.setAgencia(ubicacion.getAgencia());
		existente.setEstado(ubicacion.isEstado());
		existente.setLatitud(ubicacion.getLatitud());
		existente.setLongitud(ubicacion.getLongitud());
		existente.setDireccion(ubicacion.getDireccion());
		existente.setCiudad(ubicacion.getCiudad());
		existente.setParroquia(ubicacion.getParroquia());
		existente.setProvincia(ubicacion.getProvincia());
		existente.setLinkCoordenada(ubicacion.getLinkCoordenada());

		UbicacionesJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

	@Override
	public Ubicaciones actualizarEstado(int id, Ubicaciones ubicacion) {
		UbicacionesJpa existente = jpaRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Ubicacion no encontrada"));

		existente.setEstado(ubicacion.isEstado());

		UbicacionesJpa guardado = jpaRepository.save(existente);
		return entityMapper.toDomain(guardado);
	}

    @Override
    public boolean existeNombre(String nombre) {
        return jpaRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public boolean existeNombreParaOtro(String nombre, int idUbicacion) {
        return jpaRepository.existsByNombreIgnoreCaseAndIdUbicacionNot(nombre, idUbicacion);
    }


}
