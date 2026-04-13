package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.AsignarEmpresaAMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerActividadesPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerFrecuenciaMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerUbicacionEquipoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.RegistrarFirmaMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActividadPlanificadaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IGuardarMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientoManualUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerHistorialEquipoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerOrdenTrabajoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CrearMantenimientosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.GuardarMantenimientoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.MantenimientosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerChecklistPorCategoriaUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerHistorialEquipoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerOrdenTrabajoUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadChecklistRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadRealizadaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EmpresaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.FirmaMantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.HistorialEquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActividadChecklistRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActividadRealizadaRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.FirmaMantenimientoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.HistorialEquipoRepositoryImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.MantenimientosRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMantenimientosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadPlanificadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IFirmaMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IImagenMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoProgramadoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.INotificacionJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.servicios.ActividadPlanificadaService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoManualService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoProgramadoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.NotificacionService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PushNotificacionService;

@Configuration
public class MantenimientoConfig {

	@Bean
	IMantenimientosUseCase mantenimientosUseCase(MantenimientoRepositorioPuerto repositorio) {
		return new MantenimientosUseCaseImpl(repositorio);
	}

	@Bean
	MantenimientoRepositorioPuerto mantenimientosRepositorio(IMantenimientosJpaRepositorio jpaRepositorio,
			IMantenimientosJpaMapper mapper) {
		return new MantenimientosRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	ActividadChecklistRepositorioPuerto actividadChecklistRepositorio(IActividadChecklistJpaRepositorio jpaRepositorio) {
		return new ActividadChecklistRepositorioImpl(jpaRepositorio);
	}

	@Bean
	IObtenerChecklistPorCategoriaUseCase obtenerChecklistPorCategoriaUseCase(
			ActividadChecklistRepositorioPuerto actividadChecklistRepository) {
		return new ObtenerChecklistPorCategoriaUseCaseImpl(actividadChecklistRepository);
	}

	@Bean
	ActividadRealizadaRepositorioPuerto actividadRealizadaRepositorio(IActividadRealizadaJpaRepositorio jpaRepositorio) {
		return new ActividadRealizadaRepositorioImpl(jpaRepositorio);
	}

	@Bean
	ICrearMantenimientosUseCase crearMantenimientosUseCase(MantenimientoRepositorioPuerto mantenimientoRepository,
			CustodiasRepositorioPuerto custodiasRepositorio, EquipoRepositorioPuerto equiposRepositorio) {
		return new CrearMantenimientosUseCaseImpl(mantenimientoRepository, custodiasRepositorio, equiposRepositorio);
	}

	@Bean
	IGuardarMantenimientoUseCase guardarMantenimientoUseCase(MantenimientoRepositorioPuerto mantenimientoRepository,
			ActividadRealizadaRepositorioPuerto actividadRealizadaRepository) {
		return new GuardarMantenimientoUseCaseImpl(mantenimientoRepository, actividadRealizadaRepository);
	}

	@Bean
	IObtenerOrdenTrabajoUseCase obtenerOrdenTrabajoUseCase(MantenimientoRepositorioPuerto mantenimientoRepository,
			EquipoRepositorioPuerto equiposRepositorio, CustodiasRepositorioPuerto custodiasRepositorio,
			ActividadChecklistRepositorioPuerto actividadChecklistRepository,
			ActividadRealizadaRepositorioPuerto actividadRealizadaRepository) {
		return new ObtenerOrdenTrabajoUseCaseImpl(mantenimientoRepository, equiposRepositorio, custodiasRepositorio,
				actividadChecklistRepository, actividadRealizadaRepository);
	}

	@Bean
	HistorialEquipoRepositorioPuerto historialEquipoRepository(EquipoRepositorioPuerto equiposRepositorio,
			CustodiasRepositorioPuerto custodiasRepositorio, MantenimientoRepositorioPuerto mantenimientosRepositorio,
			UsuarioRepositorioPuerto usuariosRepositorio) {
		return new HistorialEquipoRepositoryImpl(equiposRepositorio, custodiasRepositorio, mantenimientosRepositorio,
				usuariosRepositorio);
	}

	@Bean
	FirmaMantenimientoRepositorioPuerto firmaMantenimientoRepositorio(IFirmaMantenimientoJpaRepositorio jpaRepositorio) {
		return new FirmaMantenimientoRepositorioImpl(jpaRepositorio);
	}

	@Bean
	IObtenerHistorialEquipoUseCase obtenerHistorialEquipoUseCase(HistorialEquipoRepositorioPuerto historialRepo) {
		return new ObtenerHistorialEquipoUseCaseImpl(historialRepo);
	}

	@Bean
	IMantenimientoManualUseCase mantenimientoManualService(
			IMantenimientosJpaRepositorio mantenimientosRepo,
			IActividadRealizadaJpaRepositorio actividadRealizadaRepo,
			IActividadChecklistJpaRepositorio actividadChecklistRepo,
			IImagenMantenimientoJpaRepositorio imagenRepo,
			IEquiposJpaRepositorio equiposRepo,
			ICustodiosJpaRepositorio custodiosRepo,
			IUsuariosJpaRepositorio usuariosRepo,
			MantenimientoProgramadoService programadoService,
			NotificacionService notificacionService,
			FirmaMantenimientoRepositorioPuerto firmaMantenimientoRepositorio) {
		return new MantenimientoManualService(mantenimientosRepo, actividadRealizadaRepo, actividadChecklistRepo,
				imagenRepo, equiposRepo, custodiosRepo, usuariosRepo, programadoService, notificacionService,
				firmaMantenimientoRepositorio);
	}

	@Bean
	IActividadPlanificadaUseCase actividadPlanificadaService(
			IActividadPlanificadaJpaRepositorio actividadRepo,
			IUsuariosJpaRepositorio usuariosRepo,
			ICrearMantenimientosUseCase crearMantenimientosUseCase) {
		return new ActividadPlanificadaService(actividadRepo, usuariosRepo, crearMantenimientosUseCase);
	}

	@Bean
	MantenimientoProgramadoService mantenimientoProgramadoService(
			IMantenimientoProgramadoJpaRepositorio programadoRepo,
			IEquiposJpaRepositorio equiposRepo,
			IUsuariosJpaRepositorio usuariosRepo) {
		return new MantenimientoProgramadoService(programadoRepo, equiposRepo, usuariosRepo);
	}

	@Bean
	NotificacionService notificacionService(
			INotificacionJpaRepositorio notificacionRepo,
			IUsuariosJpaRepositorio usuariosRepo,
			IMantenimientosJpaRepositorio mantenimientosRepo,
			PushNotificacionService pushNotificacionService) {
		return new NotificacionService(notificacionRepo, usuariosRepo, mantenimientosRepo, pushNotificacionService);
	}

	@Bean
	RegistrarFirmaMantenimientoUseCase registrarFirmaMantenimientoUseCase(
			IFirmaMantenimientoJpaRepositorio firmaRepositorio,
			IMantenimientosJpaRepositorio mantenimientoRepositorio,
			IUsuariosJpaRepositorio usuarioRepositorio) {
		return new RegistrarFirmaMantenimientoUseCase(firmaRepositorio, mantenimientoRepositorio, usuarioRepositorio);
	}

	@Bean
	ObtenerUbicacionEquipoUseCase obtenerUbicacionEquipoUseCase(
			IEquiposJpaRepositorio equipoRepo,
			ICustodiasJpaRepositorio custodiaRepo) {
		return new ObtenerUbicacionEquipoUseCase(equipoRepo, custodiaRepo);
	}

	@Bean
	ObtenerFrecuenciaMantenimientoUseCase obtenerFrecuenciaMantenimientoUseCase(
			IMantenimientosJpaRepositorio mantenimientoRepo) {
		return new ObtenerFrecuenciaMantenimientoUseCase(mantenimientoRepo);
	}

	@Bean
	ObtenerActividadesPorCategoriaUseCase obtenerActividadesPorCategoriaUseCase(
			IActividadChecklistJpaRepositorio actividadRepo) {
		return new ObtenerActividadesPorCategoriaUseCase(actividadRepo);
	}

	@Bean
	AsignarEmpresaAMantenimientoUseCase asignarEmpresaAMantenimientoUseCase(
			EmpresaRepositorioPuerto empresaRepo,
			MantenimientoRepositorioPuerto mantenimientoRepo) {
		return new AsignarEmpresaAMantenimientoUseCase(empresaRepo, mantenimientoRepo);
	}
}
