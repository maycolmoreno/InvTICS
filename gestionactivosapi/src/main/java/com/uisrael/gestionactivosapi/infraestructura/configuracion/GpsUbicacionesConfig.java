package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarHistorialGpsUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarUbicacionesTiempoRealUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarConsentimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarUbicacionTecnicoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ConsultarHistorialGpsUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ConsultarUbicacionesTiempoRealUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RegistrarConsentimientoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RegistrarUbicacionTecnicoUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ConsentimientoMonitoreoPort;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ConsentimientoMonitoreoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.UbicacionTecnicoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IConsentimientoMonitoreoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionTecnicoJpaRepositorio;

@Configuration
public class GpsUbicacionesConfig {

	@Bean
	ConsentimientoMonitoreoPort consentimientoMonitoreoPort(
			IConsentimientoMonitoreoJpaRepositorio jpaRepositorio) {
		return new ConsentimientoMonitoreoRepositorioImpl(jpaRepositorio);
	}

	@Bean
	UbicacionTecnicoPort ubicacionTecnicoPort(
			IUbicacionTecnicoJpaRepositorio jpaRepositorio) {
		return new UbicacionTecnicoRepositorioImpl(jpaRepositorio);
	}

	@Bean
	IRegistrarConsentimientoUseCase registrarConsentimientoUseCase(ConsentimientoMonitoreoPort consentimientoPort) {
		return new RegistrarConsentimientoUseCaseImpl(consentimientoPort);
	}

	@Bean
	IRegistrarUbicacionTecnicoUseCase registrarUbicacionTecnicoUseCase(
			UbicacionTecnicoPort ubicacionPort,
			ConsentimientoMonitoreoPort consentimientoPort,
			UsuarioRepositorioPuerto usuarioRepositorio) {
		return new RegistrarUbicacionTecnicoUseCaseImpl(ubicacionPort, consentimientoPort, usuarioRepositorio);
	}

	@Bean
	IConsultarUbicacionesTiempoRealUseCase consultarUbicacionesTiempoRealUseCase(UbicacionTecnicoPort ubicacionPort) {
		return new ConsultarUbicacionesTiempoRealUseCaseImpl(ubicacionPort);
	}

	@Bean
	IConsultarHistorialGpsUseCase consultarHistorialGpsUseCase(UbicacionTecnicoPort ubicacionPort) {
		return new ConsultarHistorialGpsUseCaseImpl(ubicacionPort);
	}
}
