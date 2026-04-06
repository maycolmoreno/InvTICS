package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICargosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICategoriaEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IDepartamentosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMarcasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRolesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUbicacionesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CargosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CategoriaEquiposUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.DepartamentosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.MarcasUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RolesUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.UbicacionesUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CargosRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CategoriaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MarcaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICargosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICategoriaEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IDepartamentosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMarcasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IRolesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUbicacionesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.CargosRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.CategoriaEquiposRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.DepartamentosRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.MarcasRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.RolesRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.UbicacionesRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IDepartamentosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

@Configuration
public class CatalogosConfig {

	@Bean
	IDepartamentosUseCase departamentoUseCase(DepartamentoRepositorioPuerto repositorio) {
		return new DepartamentosUseCaseImpl(repositorio);
	}

	@Bean
	IUbicacionesUseCase ubicacionUseCase(UbicacionRepositorioPuerto repositorio) {
		return new UbicacionesUseCaseImpl(repositorio);
	}

	@Bean
	ICargosUseCase cargoUseCase(CargosRepositorioPuerto repositorio) {
		return new CargosUseCaseImpl(repositorio);
	}

	@Bean
	DepartamentosRepositorioImpl departamentoRepositorio(IDepartamentosJpaRepositorio jpaRepositorio,
			IDepartamentosJpaMapper mapper) {
		return new DepartamentosRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	UbicacionesRepositorioImpl ubicacionRepositorio(IUbicacionesJpaRepositorio jpaRepositorio,
			IUbicacionesJpaMapper mapper) {
		return new UbicacionesRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	CargosRepositorioImpl cargoRepositorio(ICargosJpaRepositorio jpaRepositorio, ICargosJpaMapper mapper) {
		return new CargosRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IMarcasUseCase marcasUseCase(MarcaRepositorioPuerto repositorio) {
		return new MarcasUseCaseImpl(repositorio);
	}

	@Bean
	MarcasRepositorioImpl marcasRepositorio(IMarcasJpaRepositorio jpaRepositorio, IMarcasJpaMapper mapper) {
		return new MarcasRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ICategoriaEquiposUseCase categoriaEquiposUseCase(CategoriaRepositorioPuerto repositorio) {
		return new CategoriaEquiposUseCaseImpl(repositorio);
	}

	@Bean
	CategoriaEquiposRepositorioImpl categoriaEquiposRepositorio(ICategoriaEquiposJpaRepositorio jpaRepositorio,
			ICategoriaEquiposJpaMapper mapper) {
		return new CategoriaEquiposRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IRolesUseCase rolesUseCase(RolRepositorioPuerto repositorio) {
		return new RolesUseCaseImpl(repositorio);
	}

	@Bean
	RolesRepositorioImpl rolesRepositorio(IRolesJpaRepositorio jpaRepositorio, IRolesJpaMapper mapper) {
		return new RolesRepositorioImpl(jpaRepositorio, mapper);
	}
}
