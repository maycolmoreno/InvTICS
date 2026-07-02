package com.uisrael.consumogestionactivosapi.controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.MovimientoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;
import com.uisrael.consumogestionactivosapi.service.ActaStorageService;
import com.uisrael.consumogestionactivosapi.service.CorreoServicio;
import com.uisrael.consumogestionactivosapi.service.CustodiasExcelService;
import com.uisrael.consumogestionactivosapi.service.CustodiasPdfService;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/custodias")
public class CustodiasControlador {

	// Mismo whitelist que valida BajaActivoRequestDTO en gestionactivosapi (@Pattern)
	private static final Set<String> MOTIVOS_BAJA_VALIDOS = Set.of(
			"DESTRUCCION", "OBSOLESCENCIA", "ROBO_PERDIDA", "DONACION", "DANIO_IRREPARABLE", "OTRO");

	private final ICustodiasServicio servicioCustodias;
	private final IEquiposServicio servicioEquipos;
	private final ICustodiosServicio servicioCustodios;
	private final CustodiasPdfService custodiasPdfService;
	private final CustodiasExcelService custodiasExcelService;
	private final CorreoServicio correoServicio;
	private final SesionUsuario sesionUsuario;
	private final ActaStorageService actaStorageService;
	private final IInventarioOperacionServicio inventarioOperacionServicio;

	// =========================================================
	// LISTAR (POR ACTA: custodio + tipo + fecha = 1 acta)
	// =========================================================
	@GetMapping
	public String listarCustodias(Model model) {

		ICustodiasServicio.ActasAgrupadas resultado = servicioCustodias.agruparPorActa();

		model.addAttribute("actas", resultado.actas());
		model.addAttribute("actaDetalles", resultado.detalles());
		return "Custodias/listarCustodias";
	}

	// =========================================================
	// ELIMINAR / ACTIVAR
	// =========================================================
	@PostMapping("/eliminar-custodia")
	public String eliminarLogico(@RequestParam Integer idCustodiaEquipo) {
		servicioCustodias.actualizarEstado(idCustodiaEquipo, false);
		return "redirect:/custodias";
	}

	@PostMapping("/activar-custodia")
	public String activar(@RequestParam Integer idCustodiaEquipo) {
		servicioCustodias.actualizarEstado(idCustodiaEquipo, true);
		return "redirect:/custodias";
	}

	// =========================
	// FORM NUEVA CUSTODIA (MULTI EQUIPOS)
	// =========================
	@GetMapping("/nueva-custodia")
	public String nuevaCustodia(RedirectAttributes redirect) {
		redirect.addFlashAttribute("info",
				"Las custodias se generan automáticamente al realizar una asignación. Use el módulo de Asignaciones.");
		return "redirect:/inventario/asignaciones";
	}

	// =========================================================
	// GUARDAR NUEVA CUSTODIA (FORM NUEVA CUSTODIA)
	// =========================================================
	@PostMapping
	public String guardarCustodia(@ModelAttribute("custodia") CustodiasRequestDTO custodia,
			@RequestParam(name = "tipoActaUi", required = false, defaultValue = "ASIGNACION") String tipoActaUi,
			Model model, HttpSession session, RedirectAttributes redirectAttributes) {

		if ("TRASLADO".equalsIgnoreCase(tipoActaUi)) {
			redirectAttributes.addFlashAttribute("info",
					"El cambio de custodio se realiza cerrando la custodia activa desde Custodias y asignando el activo desde el módulo de Asignaciones.");
			return "redirect:/custodias";
		}

		if ("BAJA".equalsIgnoreCase(tipoActaUi)) {
			return procesarBajaDesdeFormulario(custodia, session, redirectAttributes);
		}

		if (!"ACTA_INICIAL".equalsIgnoreCase(tipoActaUi)) {
			return procesarAsignacionViaInventario(custodia, session, redirectAttributes);
		}

		// Solo ACTA_INICIAL continúa por el path legacy de custodias
		if (custodia.getFkCustodio() == null || custodia.getFkCustodio().getIdCustodio() <= 0) {
			redirectAttributes.addFlashAttribute("error", "Debe seleccionar un custodio");
			return "redirect:/custodias";
		}
		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un equipo");
			return "redirect:/custodias";
		}
		Set<Integer> equiposNoDisponibles = obtenerIdsEquiposConCustodiaActiva();
		if (custodia.getEquiposSeleccionados().stream().anyMatch(equiposNoDisponibles::contains)) {
			redirectAttributes.addFlashAttribute("error", "Uno o varios equipos ya estan asociados a otro custodio activo.");
			return "redirect:/custodias";
		}

		custodia.setEstado(true);
		custodia.setTipoMovimiento("ACTA_INICIAL");
		List<EquiposRequestDTO> equipos = custodia.getEquiposSeleccionados().stream().distinct().map(id -> {
			EquiposRequestDTO e = new EquiposRequestDTO();
			e.setIdEquipo(id);
			return e;
		}).toList();
		custodia.setEquipos(equipos);
		custodia.setFkEquipo(null);

		List<CustodiasResponseDTO> creados = servicioCustodias.crearCustodiaActa(custodia);
		if (creados == null || creados.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "No se pudo crear la custodia");
			return "redirect:/custodias";
		}

		session.setAttribute("ACTA_ENTREGA_RECIENTE", creados);
		session.setAttribute("ACTA_TIPO_MOVIMIENTO", "ACTA_INICIAL");
		return "redirect:/custodias/actaEntrega";
	}

	private String procesarAsignacionViaInventario(CustodiasRequestDTO custodia, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (custodia.getFkCustodio() == null || custodia.getFkCustodio().getIdCustodio() <= 0) {
			redirectAttributes.addFlashAttribute("error", "Debe seleccionar un custodio");
			return "redirect:/inventario/asignaciones";
		}
		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un equipo");
			return "redirect:/inventario/asignaciones";
		}

		AsignacionLoteRequestDTO lote = new AsignacionLoteRequestDTO();
		lote.setEquipoIds(custodia.getEquiposSeleccionados().stream().distinct().toList());
		lote.setCustodioId(custodia.getFkCustodio().getIdCustodio());
		lote.setFechaInicio(custodia.getFechaInicio() != null ? custodia.getFechaInicio() : LocalDate.now());
		lote.setObservacion(custodia.getObservacion());

		try {
			AsignacionActivosResponseDTO resultado = inventarioOperacionServicio.asignarActivosLote(lote);
			List<CustodiasResponseDTO> creados = resultado != null ? resultado.getCustodias() : List.of();
			if (creados.isEmpty()) {
				redirectAttributes.addFlashAttribute("error", "No se pudo completar la asignación");
				return "redirect:/inventario/asignaciones";
			}
			session.setAttribute("ACTA_ENTREGA_RECIENTE", creados);
			session.setAttribute("ACTA_TIPO_MOVIMIENTO", "ASIGNACION");
			return "redirect:/custodias/actaEntrega";
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("error", "Error al asignar: " + ex.getMessage());
			return "redirect:/inventario/asignaciones";
		}
	}

	private String procesarBajaDesdeFormulario(CustodiasRequestDTO custodia, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un equipo para baja");
			return "redirect:/custodias";
		}

		LocalDate fechaBaja = (custodia.getFechaFin() != null) ? custodia.getFechaFin()
				: (custodia.getFechaInicio() != null ? custodia.getFechaInicio() : LocalDate.now());
		String motivoBaja = MOTIVOS_BAJA_VALIDOS.contains(custodia.getMotivoBaja())
				? custodia.getMotivoBaja() : "OTRO";
		String obsBaja = (custodia.getObservacion() == null || custodia.getObservacion().isBlank())
				? "Baja de activo" : custodia.getObservacion().trim();
		String autorizadoPor = sesionUsuario.getNombre();

		List<Integer> equipoIds = custodia.getEquiposSeleccionados().stream().distinct().toList();
		List<EquiposResponseDTO> equiposBaja = new ArrayList<>();
		List<String> errores = new ArrayList<>();

		for (Integer idEquipo : equipoIds) {
			BajaActivoRequestDTO req = new BajaActivoRequestDTO();
			req.setEquipoId(idEquipo);
			req.setFechaBaja(fechaBaja);
			req.setMotivo(motivoBaja);
			req.setObservacion(obsBaja);
			req.setAutorizadoPor(autorizadoPor);
			try {
				var activo = inventarioOperacionServicio.darBajaActivo(req);
				equiposBaja.add(EquiposResponseDTO.desdeActivo(activo));
			} catch (Exception ex) {
				errores.add("Equipo #" + idEquipo + ": " + ex.getMessage());
			}
		}

		if (equiposBaja.isEmpty()) {
			redirectAttributes.addFlashAttribute("error",
					"No se pudo dar de baja ningún equipo: " + String.join("; ", errores));
			return "redirect:/custodias";
		}

		session.setAttribute("ACTA_BAJA_EQUIPOS", equiposBaja);
		session.setAttribute("ACTA_BAJA_FECHA", fechaBaja);
		session.setAttribute("ACTA_BAJA_MOTIVO", motivoBaja);
		session.setAttribute("ACTA_BAJA_OBSERVACION", obsBaja);
		session.setAttribute("ACTA_BAJA_RETURN_URL", "/custodias");

		return "redirect:/custodias/actaBaja";
	}

	@GetMapping("/actaBaja")
	public String verActaBaja(Model model, HttpSession session) {
		@SuppressWarnings("unchecked")
		List<EquiposResponseDTO> equiposBaja = (List<EquiposResponseDTO>) session.getAttribute("ACTA_BAJA_EQUIPOS");

		if (equiposBaja == null || equiposBaja.isEmpty()) {
			return "redirect:/custodias";
		}

		LocalDate fechaBaja = (LocalDate) session.getAttribute("ACTA_BAJA_FECHA");
		String motivoBaja = (String) session.getAttribute("ACTA_BAJA_MOTIVO");
		String observacionBaja = (String) session.getAttribute("ACTA_BAJA_OBSERVACION");

		model.addAttribute("equiposBaja", equiposBaja);
		model.addAttribute("fechaBaja", fechaBaja != null ? fechaBaja : LocalDate.now());
		model.addAttribute("motivoBaja", motivoBaja);
		model.addAttribute("observacionBaja", observacionBaja != null ? observacionBaja : "");
		model.addAttribute("returnUrl",
				session.getAttribute("ACTA_BAJA_RETURN_URL") != null
						? (String) session.getAttribute("ACTA_BAJA_RETURN_URL")
						: "/custodias");

		return "Custodias/actaBaja";
	}

	@GetMapping("/cerrar/{idCustodio}")
	public String mostrarCerrarCustodia(@PathVariable Integer idCustodio, @RequestParam(required = false) String tipo,
			@RequestParam(required = false) String fecha, Model model) {
		List<CustodiasResponseDTO> lista = filtrarPorActa(idCustodio, tipo, fecha).stream()
				.filter(CustodiasResponseDTO::isEstado).toList();

		if (lista.isEmpty()) {
			return "redirect:/custodias";
		}

		CustodiasResponseDTO cabecera = lista.get(0);
		CustodiasRequestDTO form = new CustodiasRequestDTO();
		form.setIdCustodio(idCustodio);
		form.setFechaInicio(cabecera.getFechaInicio());
		form.setFechaFin(LocalDate.now());
		form.setObservacion("Salida de activos");

		List<?> bodegas;
		try {
			bodegas = inventarioOperacionServicio.listarBodegas();
		} catch (Exception ex) {
			bodegas = List.of();
		}

		model.addAttribute("cabecera", cabecera);
		model.addAttribute("detalles", lista);
		model.addAttribute("form", form);
		model.addAttribute("bodegas", bodegas);
		model.addAttribute("tipoMovimiento", tipo);
		model.addAttribute("fechaActa", fecha);
		return "Custodias/cerrarCustodia";
	}

	@PostMapping("/cerrar-custodia")
	public String cerrarCustodia(@ModelAttribute("form") CustodiasRequestDTO form,
			RedirectAttributes redirectAttributes, HttpSession session) {
		if (form.getIdCustodio() <= 0) {
			redirectAttributes.addFlashAttribute("error", "No se pudo identificar el custodio a cerrar.");
			return "redirect:/custodias";
		}
		if (form.getDetallesEntregados() == null || form.getDetallesEntregados().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Debes seleccionar al menos un equipo devuelto.");
			return "redirect:/custodias/" + form.getIdCustodio();
		}
		if (form.getBodegaDestinoId() == null) {
			redirectAttributes.addFlashAttribute("error", "Debes seleccionar la bodega de destino.");
			return "redirect:/custodias/cerrar/" + form.getIdCustodio();
		}

		List<CustodiasResponseDTO> seleccionadas = servicioCustodias.listarCustodias().stream()
				.filter(CustodiasResponseDTO::isEstado)
				.filter(c -> c.getIdCustodiaEquipo() > 0
						&& form.getDetallesEntregados().contains(c.getIdCustodiaEquipo()))
				.filter(c -> getIdCustodio(c) != null && getIdCustodio(c).equals(form.getIdCustodio())).toList();

		if (seleccionadas.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "No se encontraron custodias activas para cerrar.");
			return "redirect:/custodias";
		}

		LocalDate fechaFin = form.getFechaFin() != null ? form.getFechaFin() : LocalDate.now();
		String observacion = (form.getObservacion() == null || form.getObservacion().isBlank()) ? "Devolución de activos"
				: form.getObservacion().trim();

		List<String> errores = new ArrayList<>();
		List<CustodiasResponseDTO> procesadas = new ArrayList<>();

		for (CustodiasResponseDTO activa : seleccionadas) {
			Integer idEquipoActual = getIdEquipo(activa);
			if (idEquipoActual == null) continue;

			try {
				DevolucionActivoRequestDTO dev = new DevolucionActivoRequestDTO();
				dev.setEquipoId(idEquipoActual);
				dev.setBodegaId(form.getBodegaDestinoId());
				dev.setFechaDevolucion(fechaFin);
				dev.setObservacion(observacion);
				dev.setEstadoFisicoRetorno(form.getEstadoFisicoRetorno());
				dev.setMotivo("Cierre de custodia");
				inventarioOperacionServicio.devolverActivo(dev);
				procesadas.add(activa);
			} catch (Exception ex) {
				String codigo = (activa.getFkEquipo() != null && activa.getFkEquipo().getCodigoSap() != null)
						? activa.getFkEquipo().getCodigoSap()
						: "#" + idEquipoActual;
				errores.add(codigo + ": " + ex.getMessage());
			}
		}

		if (!errores.isEmpty()) {
			redirectAttributes.addFlashAttribute("error",
					"Algunos activos no pudieron devolverse: " + String.join("; ", errores));
		}

		if (procesadas.isEmpty()) {
			return "redirect:/custodias";
		}

		session.setAttribute("ACTA_SALIDA_RECIENTE", procesadas);
		session.setAttribute("ACTA_SALIDA_FECHA", fechaFin);
		session.setAttribute("ACTA_SALIDA_OBSERVACION", observacion);

		if (errores.isEmpty()) {
			redirectAttributes.addFlashAttribute("exito", "Custodia cerrada correctamente.");
		}
		return "redirect:/custodias/actaSalida";
	}

	@GetMapping("/actaSalida")
	public String verActaSalida(Model model, HttpSession session) {
		@SuppressWarnings("unchecked")
		List<CustodiasResponseDTO> lista = (List<CustodiasResponseDTO>) session.getAttribute("ACTA_SALIDA_RECIENTE");

		if (lista == null || lista.isEmpty()) {
			return "redirect:/custodias";
		}

		CustodiasResponseDTO cabecera = lista.get(0);
		LocalDate fechaSalida = (LocalDate) session.getAttribute("ACTA_SALIDA_FECHA");
		String observacion = (String) session.getAttribute("ACTA_SALIDA_OBSERVACION");

		model.addAttribute("detalles", lista);
		model.addAttribute("idCustodioReal", custodioId(cabecera));
		model.addAttribute("nombreCustodioReal", custodioNombre(cabecera));
		model.addAttribute("fechaSalidaReal", fechaSalida != null ? fechaSalida : LocalDate.now());
		model.addAttribute("observacionReal", observacion != null ? observacion : nvl(cabecera.getObservacion()));
		return "Custodias/actaSalida";
	}

	@GetMapping("/acta-salida/pdf")
	public void descargarActaSalidaPdf(HttpSession session, HttpServletResponse response) throws IOException {
		@SuppressWarnings("unchecked")
		List<CustodiasResponseDTO> lista = (List<CustodiasResponseDTO>) session.getAttribute("ACTA_SALIDA_RECIENTE");

		if (lista == null || lista.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}

		LocalDate fechaSalida = (LocalDate) session.getAttribute("ACTA_SALIDA_FECHA");
		custodiasPdfService.generarActaSalidaPdf(lista, fechaSalida, response);
	}

	@GetMapping("/acta-baja/pdf")
	public void descargarActaBajaPdf(HttpSession session, HttpServletResponse response) throws IOException {
		@SuppressWarnings("unchecked")
		List<EquiposResponseDTO> equiposBaja = (List<EquiposResponseDTO>) session.getAttribute("ACTA_BAJA_EQUIPOS");

		if (equiposBaja == null || equiposBaja.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}

		LocalDate fechaBaja = (LocalDate) session.getAttribute("ACTA_BAJA_FECHA");
		String observacionBaja = (String) session.getAttribute("ACTA_BAJA_OBSERVACION");

		custodiasPdfService.generarActaBajaPdf(equiposBaja, fechaBaja, observacionBaja, response);
	}

	// C4: acta reconstruida desde el MovimientoInventario tipo BAJA persistido.
	// No depende de la sesion: es consultable/descargable en cualquier momento.
	@GetMapping("/acta-baja/ver/{idMovimiento}")
	public String verActaBajaPersistida(@PathVariable Integer idMovimiento, Model model) {
		MovimientoInventarioResponseDTO mov;
		try {
			mov = inventarioOperacionServicio.obtenerMovimiento(idMovimiento);
		} catch (Exception ex) {
			return "redirect:/inventario/bajas";
		}
		if (mov == null || !"BAJA".equals(mov.getTipoMovimiento())) {
			return "redirect:/inventario/bajas";
		}
		model.addAttribute("equiposBaja", List.of(equipoDesdeMovimiento(mov)));
		model.addAttribute("fechaBaja", mov.getFechaEfectiva() != null
				? mov.getFechaEfectiva()
				: (mov.getFechaMovimiento() != null ? mov.getFechaMovimiento().toLocalDate() : LocalDate.now()));
		model.addAttribute("motivoBaja", mov.getMotivo());
		model.addAttribute("observacionBaja", mov.getObservacion() != null ? mov.getObservacion() : "");
		model.addAttribute("autorizadoPor", mov.getRealizadoPor());
		model.addAttribute("returnUrl", "/inventario/bajas");
		return "Custodias/actaBaja";
	}

	@GetMapping("/acta-baja/{idMovimiento}/pdf")
	public void descargarActaBajaPersistidaPdf(@PathVariable Integer idMovimiento, HttpServletResponse response)
			throws IOException {
		MovimientoInventarioResponseDTO mov = inventarioOperacionServicio.obtenerMovimiento(idMovimiento);
		if (mov == null || !"BAJA".equals(mov.getTipoMovimiento())) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		LocalDate fechaBaja = mov.getFechaEfectiva() != null
				? mov.getFechaEfectiva()
				: (mov.getFechaMovimiento() != null ? mov.getFechaMovimiento().toLocalDate() : LocalDate.now());
		custodiasPdfService.generarActaBajaPdf(List.of(equipoDesdeMovimiento(mov)),
				fechaBaja, mov.getObservacion(), response);
	}

	// Enriquece el equipo del acta con modelo/serial (el movimiento solo trae el
	// codigo); si el equipo ya no es consultable, cae a los datos del movimiento.
	private EquiposResponseDTO equipoDesdeMovimiento(MovimientoInventarioResponseDTO mov) {
		if (mov.getEquipoId() != null) {
			try {
				EquiposResponseDTO equipo = servicioEquipos.obtenerPorId(mov.getEquipoId());
				if (equipo != null) {
					return equipo;
				}
			} catch (Exception ex) {
				log.warn("No se pudo enriquecer el equipo {} del acta: {}", mov.getEquipoId(), ex.getMessage());
			}
		}
		EquiposResponseDTO fallback = new EquiposResponseDTO();
		if (mov.getEquipoId() != null) {
			fallback.setIdEquipo(mov.getEquipoId());
		}
		fallback.setCodigoCresio(mov.getEquipoCodigo());
		return fallback;
	}

//=========================================================
//ACTA ENTREGA (HTML) - lee la sesion creada por el POST de nueva custodia
//=========================================================
	@GetMapping("/actaEntrega")
	public String verActaEntrega(Model model, HttpSession session) {

		@SuppressWarnings("unchecked")
		List<CustodiasResponseDTO> lista = (List<CustodiasResponseDTO>) session.getAttribute("ACTA_ENTREGA_RECIENTE");

		if (lista == null || lista.isEmpty()) {
			return "redirect:/custodias";
		}

		// cabecera = menor idCustodiaEquipo
		CustodiasResponseDTO cabecera = lista.stream()
				.min(Comparator.comparing(CustodiasResponseDTO::getIdCustodiaEquipo)).orElse(lista.get(0));

		String tipoMov = nvl((String) session.getAttribute("ACTA_TIPO_MOVIMIENTO"), "ASIGNACION");

		model.addAttribute("cabecera", cabecera);
		model.addAttribute("detalles", lista);
		model.addAttribute("tipoMovimiento", tipoMov);

		return "Custodias/actaEntrega";
	}

	@GetMapping("/acta-entrega/pdf")
	public void descargarActaEntregaPdf(HttpSession session, HttpServletResponse response) throws IOException {
		@SuppressWarnings("unchecked")
		List<CustodiasResponseDTO> lista = (List<CustodiasResponseDTO>) session.getAttribute("ACTA_ENTREGA_RECIENTE");
		if (lista == null || lista.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		escribirActaEntregaPdf(lista, nvl((String) session.getAttribute("ACTA_TIPO_MOVIMIENTO"), "ASIGNACION"), response);
	}

	@GetMapping("/acta-entrega/pdf/acta/{idCustodio}")
	public void descargarActaEntregaPdfActa(@PathVariable Integer idCustodio,
			@RequestParam(required = false) String tipo, @RequestParam(required = false) String fecha,
			HttpServletResponse response) throws IOException {
		List<CustodiasResponseDTO> lista = filtrarPorActa(idCustodio, tipo, fecha);
		if (lista.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		String rutaExistente = lista.get(0).getRutaActaPdf();
		if (rutaExistente != null && !rutaExistente.isBlank() && actaStorageService.existeActa(rutaExistente)) {
			byte[] pdfGuardado = actaStorageService.leerActaPdf(rutaExistente);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "inline; filename=" + rutaExistente);
			response.getOutputStream().write(pdfGuardado);
			response.getOutputStream().flush();
			return;
		}
		escribirActaEntregaPdf(lista, nvl(lista.get(0).getTipoMovimiento(), "ASIGNACION"), response);
	}

//=========================================================
// REENVIAR CORREO AL CUSTODIO
//=========================================================
	@PostMapping("/reenviar-correo/acta/{idCustodio}")
	public String reenviarCorreoActa(@PathVariable Integer idCustodio, @RequestParam(required = false) String tipo,
			@RequestParam(required = false) String fecha, RedirectAttributes redirectAttributes) {
		List<CustodiasResponseDTO> lista = filtrarPorActa(idCustodio, tipo, fecha);

		if (lista.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "No se encontraron custodias para este custodio.");
			return "redirect:/custodias";
		}

		String tipoMov = nvl(lista.get(0).getTipoMovimiento(), "ASIGNACION");

		try {
			enviarCorreoActaEntrega(lista, generarPdfActaEntrega(lista, tipoMov), tipoMov);

			String correo = lista.get(0).getFkCustodio().getCorreo();
			if (correo != null && !correo.isBlank()) {
				redirectAttributes.addFlashAttribute("exito", "Correo reenviado a " + correo);
			} else {
				redirectAttributes.addFlashAttribute("error", "El custodio no tiene correo registrado.");
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error al reenviar correo: " + e.getMessage());
		}

		return "redirect:/custodias";
	}

//=========================================================
//ACTA ENTREGA (HTML) POR CUSTODIO
//=========================================================
	@GetMapping("/acta-entrega/ver/{idCustodio}")
	public String verActaEntregaActa(@PathVariable Integer idCustodio, @RequestParam(required = false) String tipo,
			@RequestParam(required = false) String fecha, Model model) {

		List<CustodiasResponseDTO> lista = filtrarPorActa(idCustodio, tipo, fecha);

		if (lista.isEmpty()) {
			return "redirect:/custodias";
		}

		// cabecera = PK menor
		CustodiasResponseDTO cabecera = lista.get(0);

		String tipoMov = cabecera.getTipoMovimiento();
		if (tipoMov == null || tipoMov.isBlank())
			tipoMov = "ASIGNACION";

		model.addAttribute("cabecera", cabecera);
		model.addAttribute("detalles", lista);
		model.addAttribute("tipoMovimiento", tipoMov);

		return "Custodias/actaEntrega";
	}

	@GetMapping("/reporte")
	public String reporteCustodias(@RequestParam(required = false) Integer custodioId,
			@RequestParam(required = false) Integer equipoId, Model model) {

		var listaCustodios = servicioCustodios.listarCustodios().stream().filter(CustodiosResponseDTO::isEstado)
				.sorted(Comparator.comparing(CustodiosResponseDTO::getIdCustodio)).toList();

		var listaEquipos = servicioEquipos.listarEquipos().stream().filter(EquiposResponseDTO::isEstado)
				.sorted(Comparator.comparing(EquiposResponseDTO::getIdEquipo)).toList();

		List<CustodiasResponseDTO> data = filtrarCustodias(custodioId, equipoId);

		model.addAttribute("listaCustodios", listaCustodios);
		model.addAttribute("listaEquipos", listaEquipos);
		model.addAttribute("custodioSeleccionado", custodioId);
		model.addAttribute("equipoSeleccionado", equipoId);
		model.addAttribute("listaCustodias", data);

		return "Custodias/reporteCustodias";
	}

// =========================================================
// EXCEL REPORTE
// =========================================================
	@GetMapping("/reporte/excel")
	public ResponseEntity<byte[]> descargarExcelCustodias(@RequestParam(required = false) Integer custodioId,
			@RequestParam(required = false) Integer equipoId) {

		List<CustodiasResponseDTO> data = filtrarCustodias(custodioId, equipoId);

		byte[] bytes = custodiasExcelService.generarReporteExcel(data, custodioId, equipoId);

		String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
		String filename = "reporte_custodias_" + timestamp + ".xlsx";

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(bytes);
	}

// =========================================================
// HELPERS PRIVADOS
// =========================================================

	private List<CustodiasResponseDTO> filtrarCustodias(Integer custodioId, Integer equipoId) {
		List<CustodiasResponseDTO> data = servicioCustodias.listarCustodias();

		if (custodioId != null && custodioId > 0) {
			data = data.stream().filter(x -> getIdCustodio(x) != null && getIdCustodio(x).equals(custodioId)).toList();
		}

		if (equipoId != null && equipoId > 0) {
			data = data.stream().filter(x -> getIdEquipo(x) != null && getIdEquipo(x).equals(equipoId)).toList();
		}

		return data.stream().sorted(Comparator.comparing(CustodiasResponseDTO::getIdCustodiaEquipo,
				Comparator.nullsLast(Integer::compareTo))).toList();
	}

	private Integer getIdCustodio(CustodiasResponseDTO x) {
		if (x == null)
			return null;
		if (x.getFkCustodio() != null)
			return x.getFkCustodio().getIdCustodio();
		return x.getIdCustodio();
	}

	private void enviarCorreoActaEntrega(List<CustodiasResponseDTO> lista, byte[] pdfBytes, String tipoMov) {
		CustodiasResponseDTO cab = lista.get(0);
		if (cab.getFkCustodio() == null)
			return;

		String correo = cab.getFkCustodio().getCorreo();
		String nombre = cab.getFkCustodio().getNombre() != null ? cab.getFkCustodio().getNombre() : "Custodio";
		Integer idCustodio = cab.getFkCustodio().getIdCustodio();
		String numActa = String.format("%09d", idCustodio != null ? idCustodio : 0);

		correoServicio.enviarActaAsignacion(correo, nombre, numActa, pdfBytes, tipoMov);
	}

	private String obtenerNombreArchivoPdf(String tipoMov) {
		return switch (tipoMov) {
		case "ACTA_INICIAL" -> "Acta_Inicial.pdf";
		case "TRASLADO" -> "Acta_Traslado.pdf";
		case "BAJA" -> "Acta_Baja.pdf";
		default -> "Acta_Asignacion.pdf";
		};
	}

	private Integer getIdEquipo(CustodiasResponseDTO x) {
		if (x == null)
			return null;
		if (x.getFkEquipo() != null)
			return x.getFkEquipo().getIdEquipo();
		return null;
	}

	private Integer custodioId(CustodiasResponseDTO x) {
		if (x == null)
			return null;
		if (x.getFkCustodio() != null)
			return x.getFkCustodio().getIdCustodio();
		return x.getIdCustodio();
	}

	private String custodioNombre(CustodiasResponseDTO x) {
		if (x == null || x.getFkCustodio() == null)
			return "";
		return nvl(x.getFkCustodio().getNombre());
	}

	private String nvl(String s) {
		return s == null ? "" : s;
	}

	private String nvl(String s, String fallback) {
		return (s == null || s.isBlank()) ? fallback : s;
	}

	private byte[] generarPdfActaEntrega(List<CustodiasResponseDTO> lista, String tipoMov) throws IOException {
		String nombreEntrega = sesionUsuario.getNombre();
		String deptoEntrega = sesionUsuario.getDepartamento();
		if ("TRASLADO".equals(tipoMov)) {
			CustodiosResponseDTO origen = buscarCustodioOrigenTraslado(lista);
			if (origen != null) {
				nombreEntrega = origen.getNombre();
				deptoEntrega = origen.getFkDepartamento() != null ? origen.getFkDepartamento().getNombre() : "";
			}
		}
		return custodiasPdfService.generarActaEntregaPdfBytes(lista, nombreEntrega, deptoEntrega, tipoMov);
	}

	private void escribirActaEntregaPdf(List<CustodiasResponseDTO> lista, String tipoMov,
			HttpServletResponse response) throws IOException {
		byte[] pdfBytes = generarPdfActaEntrega(lista, tipoMov);
		guardarYRegistrarActa(pdfBytes, tipoMov, lista);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + obtenerNombreArchivoPdf(tipoMov));
		response.getOutputStream().write(pdfBytes);
		response.getOutputStream().flush();
		enviarCorreoActaEntrega(lista, pdfBytes, tipoMov);
	}

	private Set<Integer> obtenerIdsEquiposConCustodiaActiva() {
		return servicioCustodias.listarCustodias().stream().filter(CustodiasResponseDTO::isEstado)
				.map(CustodiasResponseDTO::getFkEquipo).filter(e -> e != null && e.getIdEquipo() > 0)
				.map(EquiposResponseDTO::getIdEquipo).collect(Collectors.toSet());
	}

	/**
	 * Busca el custodio origen de un traslado: el custodio que tenía los equipos
	 * antes de que se crearan las custodias de traslado (destino).
	 */
	private CustodiosResponseDTO buscarCustodioOrigenTraslado(List<CustodiasResponseDTO> listaTraslado) {
		if (listaTraslado == null || listaTraslado.isEmpty())
			return null;

		Set<Integer> equipoIds = listaTraslado.stream().filter(x -> x.getFkEquipo() != null)
				.map(x -> x.getFkEquipo().getIdEquipo()).collect(Collectors.toSet());

		LocalDate fechaTraslado = listaTraslado.get(0).getFechaInicio();
		int idCustodioDestino = listaTraslado.get(0).getFkCustodio() != null
				? listaTraslado.get(0).getFkCustodio().getIdCustodio()
				: 0;

		return servicioCustodias.listarCustodias().stream()
				.filter(x -> x != null && !x.isEstado() && x.getFkEquipo() != null
						&& equipoIds.contains(x.getFkEquipo().getIdEquipo()) && x.getFkCustodio() != null
						&& x.getFkCustodio().getIdCustodio() != idCustodioDestino
						&& (fechaTraslado == null || fechaTraslado.equals(x.getFechaFin())))
				.findFirst().map(CustodiasResponseDTO::getFkCustodio).orElse(null);
	}

	private List<CustodiasResponseDTO> filtrarPorActa(Integer idCustodio, String tipo, String fecha) {
		return servicioCustodias.listarCustodias().stream()
				.filter(x -> x != null && x.getFkCustodio() != null && x.getFkCustodio().getIdCustodio() == idCustodio)
				.filter(x -> {
					String xTipo = x.getTipoMovimiento() != null ? x.getTipoMovimiento() : "ASIGNACION";
					return tipo == null || tipo.isBlank() || tipo.equals(xTipo);
				}).filter(x -> {
					String xFecha = x.getFechaInicio() != null ? x.getFechaInicio().toString() : "sin-fecha";
					return fecha == null || fecha.isBlank() || fecha.equals(xFecha);
				}).sorted(Comparator.comparing(CustodiasResponseDTO::getIdCustodiaEquipo)).toList();
	}

	private void guardarYRegistrarActa(byte[] pdfBytes, String tipoMov, List<CustodiasResponseDTO> lista) {
		try {
			CustodiasResponseDTO cab = lista.get(0);
			int idCustodio = cab.getFkCustodio() != null ? cab.getFkCustodio().getIdCustodio() : cab.getIdCustodio();
			LocalDate fecha = cab.getFechaInicio();

			String nombreArchivo = actaStorageService.guardarActaPdf(pdfBytes, tipoMov, idCustodio, fecha);

			List<Integer> ids = lista.stream()
					.map(CustodiasResponseDTO::getIdCustodiaEquipo)
					.filter(id -> id > 0)
					.toList();

			actaStorageService.registrarRutaEnCustodias(ids, nombreArchivo);
		} catch (Exception e) {
			org.slf4j.LoggerFactory.getLogger(getClass())
					.error("Error al guardar acta PDF en disco: {}", e.getMessage());
		}
	}

	// =========================================================
	// SUBIR / DESCARGAR ACTA FIRMADA (PDF escaneado)
	// =========================================================
	@PostMapping("/acta-firmada/{idCustodia}")
	public String subirActaFirmada(@PathVariable int idCustodia,
			@RequestParam("archivo") MultipartFile archivo,
			RedirectAttributes redirectAttributes) {
		try {
			servicioCustodias.subirActaFirmada(idCustodia, archivo);
			redirectAttributes.addFlashAttribute("mensajeExito", "Acta firmada subida correctamente");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("mensajeError", "Error al subir el acta firmada: " + e.getMessage());
		}
		return "redirect:/custodias";
	}

	@GetMapping("/acta-firmada/{idCustodia}")
	public ResponseEntity<byte[]> descargarActaFirmada(@PathVariable int idCustodia) {
		byte[] pdf = servicioCustodias.descargarActaFirmada(idCustodia);
		if (pdf == null || pdf.length == 0) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"acta_firmada_" + idCustodia + ".pdf\"")
				.body(pdf);
	}

}
