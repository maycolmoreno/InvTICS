package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActualizarActivoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IBuscarActivoPorIdUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ActualizarActivoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.BuscarActivoPorIdUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CustodiasUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CustodiosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.EquiposUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.VisitaTecnicaUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActualizacionActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoVisitaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActualizacionActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActivoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActualizacionActivoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.CustodiasRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.CustodiosRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.EquipoVisitaRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.EquiposRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActivoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActualizacionActivoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;

@Configuration
public class EquiposActivosConfig {

	@Bean
	IEquiposUseCase equiposUseCase(EquipoRepositorioPuerto repositorio) {
		return new EquiposUseCaseImpl(repositorio);
	}

	@Bean
	EquiposRepositorioImpl equiposRepositorio(IEquiposJpaRepositorio jpaRepositorio, IEquiposJpaMapper mapper) {
		return new EquiposRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	EquipoVisitaRepositorioPuerto equipoVisitaRepositorio(jakarta.persistence.EntityManager entityManager) {
		return new EquipoVisitaRepositorioImpl(entityManager);
	}

	@Bean
	ICustodiosUseCase custodiosUseCase(CustodioRepositorioPuerto repositorio) {
		return new CustodiosUseCaseImpl(repositorio);
	}

	@Bean
	CustodiosRepositorioImpl custodiosRepositorio(ICustodiosJpaRepositorio jpaRepositorio, ICustodiosJpaMapper mapper) {
		return new CustodiosRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ICustodiasUseCase custodiasUseCase(CustodiasRepositorioPuerto repositorio) {
		return new CustodiasUseCaseImpl(repositorio);
	}

	@Bean
	public CustodiasRepositorioImpl custodiasRepositorio(ICustodiasJpaRepositorio jpaRepositorio,
			ICustodiasJpaMapper mapper, IEquiposJpaRepositorio equiposRepo, ICustodiosJpaRepositorio custodiosRepo,
			IUbicacionesJpaRepositorio ubicacionesRepo) {
		return new CustodiasRepositorioImpl(jpaRepositorio, mapper, equiposRepo, custodiosRepo, ubicacionesRepo);
	}

	@Bean
	IVisitaTecnicaUseCase visitaTecnicaUseCase(EquipoVisitaRepositorioPuerto equipoVisitaRepositorio) {
		return new VisitaTecnicaUseCaseImpl(equipoVisitaRepositorio);
	}

	@Bean
	ActivoRepositorioPuerto activoRepositorio(IActivoJpaRepositorio jpaRepo, IActivoJpaMapper mapper) {
		return new ActivoRepositorioImpl(jpaRepo, mapper);
	}

	@Bean
	ActualizacionActivoRepositorioPuerto actualizacionActivoRepositorio(
			IActualizacionActivoJpaRepositorio jpaRepo, IActualizacionActivoJpaMapper mapper) {
		return new ActualizacionActivoRepositorioImpl(jpaRepo, mapper);
	}

	@Bean
	IActualizarActivoUseCase actualizarActivoUseCase(ActivoRepositorioPuerto activoRepositorio) {
		return new ActualizarActivoUseCaseImpl(activoRepositorio);
	}

	@Bean
	IBuscarActivoPorIdUseCase buscarActivoPorIdUseCase(ActivoRepositorioPuerto activoRepositorio) {
		return new BuscarActivoPorIdUseCaseImpl(activoRepositorio);
	}
}
