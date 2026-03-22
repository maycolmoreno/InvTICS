package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.repositorios.ICustodiosRepositorio;

public class CustodiosUseCaseImpl implements ICustodiosUseCase {

    private final ICustodiosRepositorio repositorio;

    public CustodiosUseCaseImpl(ICustodiosRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Custodios crear(Custodios custodio) {

    	if (repositorio.existeCorreo(custodio.getCorreo().trim())) {
			throw new DuplicidadException("Ya existe un empleado con ese correo");
		}

    	if (repositorio.existeCedula(custodio.getCedula().trim())) {
			throw new DuplicidadException("Ya existe un empleado con esa cédula");
		}

        return repositorio.guardar(custodio);
    }

    @Override
    public Custodios obtenerPorId(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
    }

    @Override
    public List<Custodios> listar() {
        return repositorio.listarTodos();
    }

    @Override
    public Custodios actualizar(int id, Custodios custodio) {
        repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));


    	if (repositorio.existeCedulaParaOtro(custodio.getCedula().trim(), id)) {
			throw new DuplicidadException("Ya existe otro empleado con esa cédula");
		}

    	if (repositorio.existeCorreoParaOtro(custodio.getCorreo().trim(), id)) {
			throw new DuplicidadException("Ya existe otro empleado con ese correo");
		}

        Custodios actualizado = new Custodios(
                id,
                custodio.getNombre(),
                custodio.getCedula(),
                custodio.getCorreo(),
                custodio.getTelefono(),
                custodio.getFechaIngreso(),
                custodio.isEstado(),
                custodio.getFkCargo()
        );
        actualizado.setFkUbicacion(custodio.getFkUbicacion());

        return repositorio.actualizar(id, actualizado);
    }

    @Override
    public Custodios actualizarEstado(int id, boolean estado) {
    	Custodios custodio = repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));

        Custodios actualizado = new Custodios(
                id,
                custodio.getNombre(),
                custodio.getCedula(),
                custodio.getCorreo(),
                custodio.getTelefono(),
                custodio.getFechaIngreso(),
                estado,
                custodio.getFkCargo()
        );
        actualizado.setFkUbicacion(custodio.getFkUbicacion());

        return repositorio.actualizarEstado(id, actualizado);
    }

	@Override
	public boolean existeCorreo(String correo) {
		return repositorio.existeCorreo(correo.trim());
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		return repositorio.existeCorreoParaOtro(correo.trim(), idCustodio);
	}

	@Override
	public boolean existeCedula(String cedula) {
		return repositorio.existeCedula(cedula.trim());
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		return repositorio.existeCedulaParaOtro(cedula.trim(), idCustodio);
	}
}
