package com.uisrael.gestionactivosapi.aplicacion.casosuso.impl;

import java.util.List;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.DuplicidadException;
import com.uisrael.gestionactivosapi.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;

public class CustodiosUseCaseImpl implements ICustodiosUseCase {

    private final CustodioRepositorioPuerto custodioRepositorio;

    public CustodiosUseCaseImpl(CustodioRepositorioPuerto custodioRepositorio) {
        this.custodioRepositorio = custodioRepositorio;
    }

    @Override
    public Custodios crear(Custodios custodio) {

    	if (custodioRepositorio.existeCorreo(custodio.getCorreo().trim())) {
			throw new DuplicidadException("Ya existe un empleado con ese correo");
		}

    	if (custodioRepositorio.existeCedula(custodio.getCedula().trim())) {
			throw new DuplicidadException("Ya existe un empleado con esa cédula");
		}

        return custodioRepositorio.guardar(custodio);
    }

    @Override
    public Custodios obtenerPorId(int id) {
        return custodioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));
    }

    @Override
    public List<Custodios> listar() {
        return custodioRepositorio.listarTodos();
    }

    @Override
    public Custodios actualizar(int id, Custodios custodio) {
        custodioRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Custodio no encontrado"));


    	if (custodioRepositorio.existeCedulaParaOtro(custodio.getCedula().trim(), id)) {
			throw new DuplicidadException("Ya existe otro empleado con esa cédula");
		}

    	if (custodioRepositorio.existeCorreoParaOtro(custodio.getCorreo().trim(), id)) {
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

        return custodioRepositorio.actualizar(id, actualizado);
    }

    @Override
    public Custodios actualizarEstado(int id, boolean estado) {
    	Custodios custodio = custodioRepositorio.buscarPorId(id)
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

        return custodioRepositorio.actualizarEstado(id, actualizado);
    }

	@Override
	public boolean existeCorreo(String correo) {
		return custodioRepositorio.existeCorreo(correo.trim());
	}

	@Override
	public boolean existeCorreoParaOtro(String correo, int idCustodio) {
		return custodioRepositorio.existeCorreoParaOtro(correo.trim(), idCustodio);
	}

	@Override
	public boolean existeCedula(String cedula) {
		return custodioRepositorio.existeCedula(cedula.trim());
	}

	@Override
	public boolean existeCedulaParaOtro(String cedula, int idCustodio) {
		return custodioRepositorio.existeCedulaParaOtro(cedula.trim(), idCustodio);
	}
}
