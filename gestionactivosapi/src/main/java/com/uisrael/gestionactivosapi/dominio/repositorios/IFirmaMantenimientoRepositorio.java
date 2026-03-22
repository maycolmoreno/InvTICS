package com.uisrael.gestionactivosapi.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.gestionactivosapi.dominio.entidades.FirmaMantenimiento;
import com.uisrael.gestionactivosapi.dominio.entidades.TipoFirma;

public interface IFirmaMantenimientoRepositorio {

    FirmaMantenimiento guardar(FirmaMantenimiento firmaMantenimiento);

    List<FirmaMantenimiento> listarPorMantenimiento(Integer idMantenimiento);

    Optional<FirmaMantenimiento> buscarPorMantenimientoYTipo(Integer idMantenimiento, TipoFirma tipoFirma);
}
