package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoManualService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoProgramadoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.NotificacionService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.CorreoMantenimientoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.CorreoSchedulerService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoArchivoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoInformeService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.ActividadPlanificadaService;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientoManualUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActividadPlanificadaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.RegistrarFirmaMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerUbicacionEquipoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerFrecuenciaMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.ObtenerActividadesPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.AsignarEmpresaAMantenimientoUseCase;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EmpresaRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEmpresaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadPlanificadaJpaRepositorio;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICargosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICategoriaEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICrearMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IDepartamentosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IEquiposUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IGuardarMantenimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMarcasUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IMantenimientosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerChecklistPorCategoriaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerOrdenTrabajoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IObtenerHistorialEquipoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRolesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ISetupInicialUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUbicacionesUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IUsuariosUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVincularCustodioConUsuarioUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CargosUseCaseImpl;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PdfMantenimientoService;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.AlmacenadorArchivosPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.EnviadorCorreoPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.GeneradorPdfPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.ServicioAuditoriaPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.servicios.ServicioNotificacionPuerto;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.archivos.AlmacenadorArchivosAdaptador;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.auditoria.ServicioAuditoriaAdaptador;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.correo.EnviadorCorreoAdaptador;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.notificacion.ServicioNotificacionAdaptador;
import com.uisrael.gestionactivosapi.infraestructura.adaptadores.pdf.GeneradorPdfAdaptador;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CategoriaEquiposUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CrearMantenimientosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CustodiasUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.CustodiosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.DepartamentosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.EquiposUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.GuardarMantenimientoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.MarcasUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.MantenimientosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerChecklistPorCategoriaUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerOrdenTrabajoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ObtenerHistorialEquipoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RolesUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.SetupInicialUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.UbicacionesUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.UsuariosUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.VincularCustodioConUsuarioUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IAutenticarUsuarioUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IActualizarActivoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IBuscarActivoPorIdUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IVisitaTecnicaUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.AutenticarUsuarioUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ActualizarActivoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.BuscarActivoPorIdUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.VisitaTecnicaUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActualizacionActivoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadChecklistRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IActualizacionActivoJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActivoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.ActualizacionActivoRepositorioImpl;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActivoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActualizacionActivoJpaRepositorio;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ActividadRealizadaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CargosRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CategoriaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodiasRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.DepartamentoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoVisitaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.FirmaMantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.HistorialEquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MantenimientoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.MarcaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.RolRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICargosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICategoriaEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.ICustodiosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IDepartamentosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IEquiposJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMarcasJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IMantenimientosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IRolesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUbicacionesJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.mapeadores.IUsuariosJpaMapper;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.*;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICargosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICategoriaEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadChecklistJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IActividadRealizadaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.ICustodiosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IDepartamentosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEquiposJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IFirmaMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IImagenMantenimientoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMarcasJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientoProgramadoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IMantenimientosJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.INotificacionJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IRolesJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionesJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUsuariosJpaRepositorio;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IConsultarUbicacionesTiempoRealUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarConsentimientoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.IRegistrarUbicacionTecnicoUseCase;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.ConsultarUbicacionesTiempoRealUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RegistrarConsentimientoUseCaseImpl;
import com.uisrael.gestionactivosapi.aplicacion.casosuso.impl.RegistrarUbicacionTecnicoUseCaseImpl;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.ConsentimientoMonitoreoPort;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UbicacionTecnicoPort;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IConsentimientoMonitoreoJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IUbicacionTecnicoJpaRepositorio;

@Configuration
public class ConfiguracionGeneral {

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
	IMantenimientosUseCase mantenimientosUseCase(MantenimientoRepositorioPuerto repositorio) {
		return new MantenimientosUseCaseImpl(repositorio);
	}

	@Bean
	MantenimientoRepositorioPuerto mantenimientosRepositorio(IMantenimientosJpaRepositorio jpaRepositorio,
			IMantenimientosJpaMapper mapper) {
		return new MantenimientosRepositorioImpl(jpaRepositorio, mapper);
	}

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
	IRolesUseCase rolesUseCase(RolRepositorioPuerto repositorio) {
		return new RolesUseCaseImpl(repositorio);
	}

	@Bean
	RolesRepositorioImpl rolesRepositorio(IRolesJpaRepositorio jpaRepositorio, IRolesJpaMapper mapper) {
		return new RolesRepositorioImpl(jpaRepositorio, mapper);
	}

	@Bean
	IUsuariosUseCase usuariosUseCase(UsuarioRepositorioPuerto repositorio, RolRepositorioPuerto rolesRepositorio,
			DepartamentoRepositorioPuerto departamentosRepositorio, PasswordEncoder passwordEncoder) {
		return new UsuariosUseCaseImpl(repositorio, rolesRepositorio, departamentosRepositorio, passwordEncoder);
	}

	@Bean
	ISetupInicialUseCase setupInicialUseCase(UsuarioRepositorioPuerto usuariosRepositorio,
			RolRepositorioPuerto rolesRepositorio, DepartamentoRepositorioPuerto departamentosRepositorio,
			PasswordEncoder passwordEncoder) {
		return new SetupInicialUseCaseImpl(usuariosRepositorio, rolesRepositorio, departamentosRepositorio,
				passwordEncoder);
	}

	@Bean
	UsuariosRepositorioImpl usuariosRepositorio(IUsuariosJpaRepositorio jpaRepositorio, IUsuariosJpaMapper mapper) {
		return new UsuariosRepositorioImpl(jpaRepositorio, mapper);
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
	ActivoRepositorioPuerto activoRepositorio(IActivoJpaRepositorio jpaRepo, IActivoJpaMapper mapper) {
		return new ActivoRepositorioImpl(jpaRepo, mapper);
	}

	@Bean
	ActualizacionActivoRepositorioPuerto actualizacionActivoRepositorio(
			IActualizacionActivoJpaRepositorio jpaRepo, IActualizacionActivoJpaMapper mapper) {
		return new ActualizacionActivoRepositorioImpl(jpaRepo, mapper);
	}

	@Bean
	IVincularCustodioConUsuarioUseCase vincularCustodioConUsuarioUseCase(CustodioRepositorioPuerto custodiosRepositorio,
			UsuarioRepositorioPuerto usuariosRepositorio) {
		return new VincularCustodioConUsuarioUseCaseImpl(custodiosRepositorio, usuariosRepositorio);
	}

	@Bean
	IAutenticarUsuarioUseCase autenticarUsuarioUseCase(UsuarioRepositorioPuerto usuariosRepositorio,
			PasswordEncoder passwordEncoder) {
		return new AutenticarUsuarioUseCaseImpl(usuariosRepositorio, passwordEncoder);
	}

	@Bean
	IActualizarActivoUseCase actualizarActivoUseCase(ActivoRepositorioPuerto activoRepositorio) {
		return new ActualizarActivoUseCaseImpl(activoRepositorio);
	}

	@Bean
	IBuscarActivoPorIdUseCase buscarActivoPorIdUseCase(ActivoRepositorioPuerto activoRepositorio) {
		return new BuscarActivoPorIdUseCaseImpl(activoRepositorio);
	}

	@Bean
	IVisitaTecnicaUseCase visitaTecnicaUseCase(EquipoVisitaRepositorioPuerto equipoVisitaRepositorio) {
		return new VisitaTecnicaUseCaseImpl(equipoVisitaRepositorio);
	}

	// ---- Adaptadores de puertos de servicio ----

	@Bean
	EnviadorCorreoPuerto enviadorCorreoPuerto(
			org.springframework.mail.javamail.JavaMailSender mailSender,
			@org.springframework.beans.factory.annotation.Value("${spring.mail.username:}") String remitente,
			@org.springframework.beans.factory.annotation.Value("${app.mail.from-name:CRESIO - Gestion de Activos}") String nombreRemitente) {
		return new EnviadorCorreoAdaptador(mailSender, remitente, nombreRemitente);
	}

	@Bean
	AlmacenadorArchivosPuerto almacenadorArchivosPuerto(
			@org.springframework.beans.factory.annotation.Value("${mantenimiento.storage.base-path:./data/mantenimientos}") String basePath) {
		return new AlmacenadorArchivosAdaptador(basePath);
	}

	@Bean
	GeneradorPdfPuerto generadorPdfPuerto(PdfMantenimientoService pdfService) {
		return new GeneradorPdfAdaptador(pdfService);
	}

	@Bean
	ServicioNotificacionPuerto servicioNotificacionPuerto(EnviadorCorreoPuerto enviadorCorreo) {
		return new ServicioNotificacionAdaptador(enviadorCorreo);
	}

	@Bean
	ServicioAuditoriaPuerto servicioAuditoriaPuerto() {
		return new ServicioAuditoriaAdaptador();
	}

	// ---- Servicios de infraestructura ----

	@Bean
	PdfMantenimientoService pdfMantenimientoService() {
		return new PdfMantenimientoService();
	}

	@Bean
	CorreoMantenimientoService correoMantenimientoService(
			org.springframework.mail.javamail.JavaMailSender mailSender) {
		return new CorreoMantenimientoService(mailSender);
	}

	@Bean
	MantenimientoArchivoService mantenimientoArchivoService(
			@org.springframework.beans.factory.annotation.Value("${mantenimiento.storage.base-path:./data/mantenimientos}") String basePath) {
		return new MantenimientoArchivoService(basePath);
	}

	@Bean
	MantenimientoInformeService mantenimientoInformeService(
			EquipoRepositorioPuerto equiposRepo,
			CustodioRepositorioPuerto custodiosRepo,
			UsuarioRepositorioPuerto usuariosRepo,
			PdfMantenimientoService pdfService,
			MantenimientoArchivoService archivoService,
			CorreoMantenimientoService correoService) {
		return new MantenimientoInformeService(equiposRepo, custodiosRepo, usuariosRepo, pdfService, archivoService, correoService);
	}

	@Bean
	CorreoSchedulerService correoSchedulerService(EnviadorCorreoPuerto enviadorCorreo) {
		return new CorreoSchedulerService(enviadorCorreo);
	}

	// ---- Servicios de aplicación ----

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
			IUsuariosJpaRepositorio usuariosRepo) {
		return new ActividadPlanificadaService(actividadRepo, usuariosRepo);
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
			IMantenimientosJpaRepositorio mantenimientosRepo) {
		return new NotificacionService(notificacionRepo, usuariosRepo, mantenimientosRepo);
	}

	// ---- GPS / Ubicaciones Técnicos ----

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

	// ---- Repositorio Empresa ----

	@Bean
	EmpresaRepositorioPuerto empresaRepositorio(IEmpresaJpaRepositorio jpaRepo) {
		return new EmpresaRepositorioAdaptador(jpaRepo);
	}

	// ---- Casos de uso adicionales ----

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
