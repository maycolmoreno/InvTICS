package com.uisrael.gestionactivosapi.infraestructura.configuracion;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EmpresaRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
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
import com.uisrael.gestionactivosapi.infraestructura.repositorios.IEmpresaJpaRepositorio;
import com.uisrael.gestionactivosapi.infraestructura.persistencia.repositorios.EmpresaRepositorioAdaptador;
import com.uisrael.gestionactivosapi.infraestructura.servicios.CorreoMantenimientoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.CorreoSchedulerService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PushNotificacionService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoArchivoService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.MantenimientoInformeService;
import com.uisrael.gestionactivosapi.infraestructura.servicios.PdfMantenimientoService;

@Configuration
public class ServiciosAdaptadoresConfig {

	// ---- Adaptadores de puertos de servicio ----

	@Bean
	EnviadorCorreoPuerto enviadorCorreoPuerto(
			JavaMailSender mailSender,
			@Value("${spring.mail.username:}") String remitente,
			@Value("${app.mail.from-name:CRESIO - Gestion de Activos}") String nombreRemitente) {
		return new EnviadorCorreoAdaptador(mailSender, remitente, nombreRemitente);
	}

	@Bean
	AlmacenadorArchivosPuerto almacenadorArchivosPuerto(
			@Value("${mantenimiento.storage.base-path:./data/mantenimientos}") String basePath) {
		return new AlmacenadorArchivosAdaptador(basePath);
	}

	@Bean
	GeneradorPdfPuerto generadorPdfPuerto(PdfMantenimientoService pdfService) {
		return new GeneradorPdfAdaptador(pdfService);
	}

	@Bean
	ServicioNotificacionPuerto servicioNotificacionPuerto(EnviadorCorreoPuerto enviadorCorreo,
			PushNotificacionService pushNotificacionService) {
		return new ServicioNotificacionAdaptador(enviadorCorreo, pushNotificacionService);
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
	CorreoMantenimientoService correoMantenimientoService(JavaMailSender mailSender) {
		return new CorreoMantenimientoService(mailSender);
	}

	@Bean
	MantenimientoArchivoService mantenimientoArchivoService(
			@Value("${mantenimiento.storage.base-path:./data/mantenimientos}") String basePath) {
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

	// ---- Repositorio Empresa ----

	@Bean
	EmpresaRepositorioPuerto empresaRepositorio(IEmpresaJpaRepositorio jpaRepo) {
		return new EmpresaRepositorioAdaptador(jpaRepo);
	}
}
