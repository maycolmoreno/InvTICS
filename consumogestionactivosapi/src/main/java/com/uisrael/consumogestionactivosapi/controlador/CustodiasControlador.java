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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.CustodiosRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;
import com.uisrael.consumogestionactivosapi.service.CorreoServicio;
import com.uisrael.consumogestionactivosapi.service.CustodiasExcelService;
import com.uisrael.consumogestionactivosapi.service.CustodiasPdfService;
import com.uisrael.consumogestionactivosapi.service.ICargosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.IDepartamentosServicio;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IUbicacionesServicio;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/custodias")
public class CustodiasControlador {

	private final ICustodiasServicio servicioCustodias;
	private final IEquiposServicio servicioEquipos;
	private final ICustodiosServicio servicioCustodios;
	private final IDepartamentosServicio servicioDepartamento;
	private final ICargosServicio servicioCargo;
	private final IUbicacionesServicio servicioUbicacion;
	private final CustodiasPdfService custodiasPdfService;
	private final CustodiasExcelService custodiasExcelService;
	private final CorreoServicio correoServicio;
	private final SesionUsuario sesionUsuario;

	// =========================================================
	// LISTAR (POR ACTA: custodio + tipo + fecha = 1 acta)
	// =========================================================
	@GetMapping
	public String listarCustodias(Model model) {

		List<CustodiasResponseDTO> lista = servicioCustodias.listarCustodias();

		// Key compuesta: idCustodio + tipo + fechaInicio -> cada grupo = 1 acta
		java.util.function.Function<CustodiasResponseDTO, String> actaKeyFn = x -> {
			int idC = (x != null && x.getFkCustodio() != null) ? x.getFkCustodio().getIdCustodio()
					: (x != null ? x.getIdCustodio() : 0);
			String tipo = (x != null && x.getTipoMovimiento() != null) ? x.getTipoMovimiento() : "ASIGNACION";
			String fecha = (x != null && x.getFechaInicio() != null) ? x.getFechaInicio().toString() : "sin-fecha";
			return idC + "_" + tipo + "_" + fecha;
		};

		Map<String, List<CustodiasResponseDTO>> actaDetalles = lista.stream().filter(x -> x != null)
				.collect(Collectors.groupingBy(actaKeyFn));

		List<ActaResumen> actas = actaDetalles.entrySet().stream().map(entry -> {
			List<CustodiasResponseDTO> items = entry.getValue();
			CustodiasResponseDTO first = items.stream().min(Comparator
					.comparing(CustodiasResponseDTO::getIdCustodiaEquipo, Comparator.nullsLast(Integer::compareTo)))
					.orElse(items.get(0));
			int idC = first.getFkCustodio() != null ? first.getFkCustodio().getIdCustodio() : first.getIdCustodio();
			String tipo = first.getTipoMovimiento() != null ? first.getTipoMovimiento() : "ASIGNACION";
			String etiqueta = switch (tipo) {
			case "ACTA_INICIAL" -> "Acta Inicial";
			case "TRASLADO" -> "Traslado";
			case "BAJA" -> "Baja";
			default -> "Asignacion";
			};
			boolean activa = items.stream().anyMatch(CustodiasResponseDTO::isEstado);
			LocalDate fechaFin = items.stream().map(CustodiasResponseDTO::getFechaFin).filter(f -> f != null)
					.max(Comparator.naturalOrder()).orElse(null);
			ActaResumen r = new ActaResumen();
			r.setKey(entry.getKey());
			r.setIdCustodio(idC);
			r.setCustodio(first.getFkCustodio());
			r.setTipoMovimiento(tipo);
			r.setEtiquetaTipo(etiqueta);
			r.setFechaInicio(first.getFechaInicio());
			r.setFechaFin(fechaFin);
			r.setActiva(activa);
			r.setCantidadEquipos(items.size());
			int minPk = items.stream().map(CustodiasResponseDTO::getIdCustodiaEquipo).filter(pk -> pk != null)
					.min(Integer::compareTo).orElse(0);
			r.setMinPk(minPk);
			return r;
		}).sorted(Comparator.comparingInt(ActaResumen::getMinPk)).toList();

		// Asignar número secuencial 1, 2, 3... según orden de creación
		java.util.List<ActaResumen> actasNumeradas = new java.util.ArrayList<>(actas);
		for (int i = 0; i < actasNumeradas.size(); i++) {
			actasNumeradas.get(i).setNumeroActa(i + 1);
		}
		// Ordenar para mostrar más recientes primero
		actasNumeradas.sort(Comparator.comparingInt(ActaResumen::getMinPk).reversed());

		model.addAttribute("actas", actasNumeradas);
		model.addAttribute("actaDetalles", actaDetalles);
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
	public String nuevaCustodia(Model model) {

		CustodiasRequestDTO custodia = new CustodiasRequestDTO();
		custodia.setEstado(true);

		custodia.setFkCustodio(new CustodiosRequestDTO());
		custodia.getFkCustodio().setIdCustodio(0);

		custodia.setFkEquipo(null);

		cargarFormularioNuevaCustodia(model, custodia, null);

		return "Custodias/nuevoCustodia";
	}

	// =========================================================
	// GUARDAR NUEVA CUSTODIA (FORM NUEVA CUSTODIA)
	// =========================================================
	@PostMapping
	public String guardarCustodia(@ModelAttribute("custodia") CustodiasRequestDTO custodia,
			@RequestParam(name = "tipoActaUi", required = false, defaultValue = "ASIGNACION") String tipoActaUi,
			Model model, HttpSession session) {

		boolean esTraslado = "TRASLADO".equalsIgnoreCase(tipoActaUi);
		boolean esBaja = "BAJA".equalsIgnoreCase(tipoActaUi);

		if (esTraslado) {
			return procesarTrasladoDesdeFormulario(custodia, model, session);
		}
		if (esBaja) {
			return procesarBajaDesdeFormulario(custodia, model, session);
		}

		// ACTA_INICIAL y ASIGNACION se procesan igual (crean custodia nueva)
		String tipoMovimiento = "ACTA_INICIAL".equalsIgnoreCase(tipoActaUi) ? "ACTA_INICIAL" : "ASIGNACION";

		// Validaciones minimas
		if (custodia.getFkCustodio() == null || custodia.getFkCustodio().getIdCustodio() <= 0) {
			cargarFormularioNuevaCustodia(model, custodia, "Debe seleccionar un custodio");
			return "Custodias/nuevoCustodia";
		}

		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			cargarFormularioNuevaCustodia(model, custodia, "Debe seleccionar al menos un equipo");
			return "Custodias/nuevoCustodia";
		}

		Set<Integer> equiposNoDisponibles = obtenerIdsEquiposConCustodiaActiva();
		boolean contieneEquiposNoDisponibles = custodia.getEquiposSeleccionados().stream()
				.anyMatch(equiposNoDisponibles::contains);
		if (contieneEquiposNoDisponibles) {
			cargarFormularioNuevaCustodia(model, custodia,
					"Uno o varios equipos ya estan asociados a otro custodio activo.");
			return "Custodias/nuevoCustodia";
		}

		// Estado activo
		custodia.setEstado(true);
		custodia.setTipoMovimiento(tipoMovimiento);

		// Convertir IDs de equipos a objetos
		List<EquiposRequestDTO> equipos = custodia.getEquiposSeleccionados().stream().distinct().map(id -> {
			EquiposRequestDTO e = new EquiposRequestDTO();
			e.setIdEquipo(id);
			return e;
		}).toList();

		custodia.setEquipos(equipos);
		custodia.setFkEquipo(null);

		// Llamar al servicio
		List<CustodiasResponseDTO> creados = servicioCustodias.crearCustodiaActa(custodia);

		if (creados == null || creados.isEmpty()) {
			cargarFormularioNuevaCustodia(model, custodia, "No se pudo crear la custodia");
			return "Custodias/nuevoCustodia";
		}

		// Guardar en sesion para el acta
		session.setAttribute("ACTA_ENTREGA_RECIENTE", creados);
		session.setAttribute("ACTA_TIPO_MOVIMIENTO", tipoMovimiento);

		return "redirect:/custodias/actaEntrega";
	}

	private String procesarTrasladoDesdeFormulario(CustodiasRequestDTO custodia, Model model, HttpSession session) {
		if (custodia.getFkCustodio() == null || custodia.getFkCustodio().getIdCustodio() <= 0) {
			cargarFormularioNuevaCustodia(model, custodia, "Debe seleccionar el custodio destino");
			return "Custodias/nuevoCustodia";
		}

		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			cargarFormularioNuevaCustodia(model, custodia, "Debe seleccionar al menos un equipo para traslado");
			return "Custodias/nuevoCustodia";
		}

		int idCustodioDestino = custodia.getFkCustodio().getIdCustodio();
		LocalDate fechaTraslado = (custodia.getFechaInicio() != null) ? custodia.getFechaInicio() : LocalDate.now();
		String obsTraslado = (custodia.getObservacion() == null) ? "" : custodia.getObservacion().trim();

		List<CustodiasResponseDTO> custodiasActivas = servicioCustodias.listarCustodias().stream()
				.filter(x -> x != null && x.isEstado() && x.getFkEquipo() != null && x.getFkEquipo().getIdEquipo() > 0)
				.toList();

		Map<Integer, CustodiasResponseDTO> activaPorEquipo = new HashMap<>();
		for (CustodiasResponseDTO c : custodiasActivas) {
			activaPorEquipo.putIfAbsent(c.getFkEquipo().getIdEquipo(), c);
		}

		boolean hayNoAsignados = custodia.getEquiposSeleccionados().stream()
				.anyMatch(idEquipo -> !activaPorEquipo.containsKey(idEquipo));
		if (hayNoAsignados) {
			cargarFormularioNuevaCustodia(model, custodia,
					"Uno o varios equipos seleccionados no tienen custodia activa para trasladar.");
			return "Custodias/nuevoCustodia";
		}

		boolean hayMismoCustodio = custodia.getEquiposSeleccionados().stream().map(activaPorEquipo::get)
				.filter(x -> x != null).anyMatch(x -> {
					Integer idActual = getIdCustodio(x);
					return idActual != null && idActual == idCustodioDestino;
				});
		if (hayMismoCustodio) {
			cargarFormularioNuevaCustodia(model, custodia, "Uno o varios equipos ya pertenecen al custodio destino.");
			return "Custodias/nuevoCustodia";
		}

		for (Integer idEquipo : custodia.getEquiposSeleccionados().stream().distinct().toList()) {
			CustodiasResponseDTO activa = activaPorEquipo.get(idEquipo);
			if (activa == null)
				continue;

			Integer idCustodioActual = getIdCustodio(activa);
			int idCustodioOrigen = idCustodioActual != null ? idCustodioActual : 0;

			CustodiasRequestDTO upd = new CustodiasRequestDTO();
			upd.setIdCustodio(idCustodioOrigen);
			upd.setFechaInicio(activa.getFechaInicio() != null ? activa.getFechaInicio() : LocalDate.now());
			upd.setFechaFin(fechaTraslado);
			upd.setEstado(false);
			upd.setObservacion(activa.getObservacion() != null ? activa.getObservacion() : "");
			upd.setTipoMovimiento("TRASLADO");

			if (idCustodioOrigen > 0) {
				CustodiosRequestDTO c = new CustodiosRequestDTO();
				c.setIdCustodio(idCustodioOrigen);
				upd.setFkCustodio(c);
			}

			EquiposRequestDTO e = new EquiposRequestDTO();
			e.setIdEquipo(idEquipo);
			upd.setEquipos(List.of(e));

			servicioCustodias.actualizarCustodia(activa.getIdCustodiaEquipo(), upd);
		}

		custodia.setEstado(true);
		custodia.setFechaInicio(fechaTraslado);
		if (!obsTraslado.isBlank()) {
			custodia.setObservacion(obsTraslado);
		}
		List<EquiposRequestDTO> equipos = custodia.getEquiposSeleccionados().stream().distinct().map(id -> {
			EquiposRequestDTO e = new EquiposRequestDTO();
			e.setIdEquipo(id);
			return e;
		}).toList();
		custodia.setEquipos(equipos);
		custodia.setFkEquipo(null);
		custodia.setTipoMovimiento("TRASLADO");

		List<CustodiasResponseDTO> creados = servicioCustodias.crearCustodiaActa(custodia);
		if (creados == null || creados.isEmpty()) {
			cargarFormularioNuevaCustodia(model, custodia, "No se pudo completar el traslado");
			return "Custodias/nuevoCustodia";
		}

		session.setAttribute("ACTA_ENTREGA_RECIENTE", creados);
		session.setAttribute("ACTA_TIPO_MOVIMIENTO", "TRASLADO");

		return "redirect:/custodias/actaEntrega";
	}

	private String procesarBajaDesdeFormulario(CustodiasRequestDTO custodia, Model model, HttpSession session) {
		if (custodia.getEquiposSeleccionados() == null || custodia.getEquiposSeleccionados().isEmpty()) {
			cargarFormularioNuevaCustodia(model, custodia, "Debe seleccionar al menos un equipo para baja");
			return "Custodias/nuevoCustodia";
		}

		LocalDate fechaBaja = (custodia.getFechaFin() != null) ? custodia.getFechaFin()
				: (custodia.getFechaInicio() != null ? custodia.getFechaInicio() : LocalDate.now());
		String obsBaja = (custodia.getObservacion() == null) ? "" : custodia.getObservacion().trim();
		if (obsBaja.isBlank()) {
			obsBaja = "Baja de activo";
		}

		List<CustodiasResponseDTO> custodiasActivas = servicioCustodias.listarCustodias().stream()
				.filter(x -> x != null && x.isEstado() && x.getFkEquipo() != null && x.getFkEquipo().getIdEquipo() > 0)
				.toList();

		Map<Integer, CustodiasResponseDTO> activaPorEquipo = new HashMap<>();
		for (CustodiasResponseDTO c : custodiasActivas) {
			activaPorEquipo.putIfAbsent(c.getFkEquipo().getIdEquipo(), c);
		}

		List<Integer> equiposProcesados = custodia.getEquiposSeleccionados().stream().distinct().toList();

		for (Integer idEquipo : equiposProcesados) {
			CustodiasResponseDTO activa = activaPorEquipo.get(idEquipo);
			if (activa != null) {
				Integer idCustodioActual = getIdCustodio(activa);
				int idCustodio = idCustodioActual != null ? idCustodioActual : 0;

				CustodiasRequestDTO upd = new CustodiasRequestDTO();
				upd.setIdCustodio(idCustodio);
				upd.setFechaInicio(activa.getFechaInicio() != null ? activa.getFechaInicio() : LocalDate.now());
				upd.setFechaFin(fechaBaja);
				upd.setEstado(false);
				upd.setObservacion(obsBaja);
				upd.setTipoMovimiento("BAJA");

				CustodiosRequestDTO c = new CustodiosRequestDTO();
				c.setIdCustodio(idCustodio);
				upd.setFkCustodio(c);

				EquiposRequestDTO e = new EquiposRequestDTO();
				e.setIdEquipo(idEquipo);
				upd.setEquipos(List.of(e));

				servicioCustodias.actualizarCustodia(activa.getIdCustodiaEquipo(), upd);
			}

			servicioEquipos.actualizarEstado(idEquipo, false);
		}

		List<EquiposResponseDTO> equiposBaja = equiposProcesados.stream().map(servicioEquipos::obtenerPorId)
				.filter(e -> e != null).toList();

		session.setAttribute("ACTA_BAJA_EQUIPOS", equiposBaja);
		session.setAttribute("ACTA_BAJA_FECHA", fechaBaja);
		session.setAttribute("ACTA_BAJA_OBSERVACION", obsBaja);

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
		String observacionBaja = (String) session.getAttribute("ACTA_BAJA_OBSERVACION");

		model.addAttribute("equiposBaja", equiposBaja);
		model.addAttribute("fechaBaja", fechaBaja != null ? fechaBaja : LocalDate.now());
		model.addAttribute("observacionBaja", observacionBaja != null ? observacionBaja : "");

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

		model.addAttribute("cabecera", cabecera);
		model.addAttribute("detalles", lista);
		model.addAttribute("form", form);
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
			return "redirect:/custodias";
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
		String observacion = (form.getObservacion() == null || form.getObservacion().isBlank()) ? "Salida de activos"
				: form.getObservacion().trim();

		for (CustodiasResponseDTO activa : seleccionadas) {
			Integer idCustodioActual = getIdCustodio(activa);
			Integer idEquipoActual = getIdEquipo(activa);
			if (idCustodioActual == null || idEquipoActual == null) {
				continue;
			}

			CustodiasRequestDTO upd = new CustodiasRequestDTO();
			upd.setIdCustodio(idCustodioActual);
			upd.setFechaInicio(activa.getFechaInicio() != null ? activa.getFechaInicio() : LocalDate.now());
			upd.setFechaFin(fechaFin);
			upd.setEstado(false);
			upd.setObservacion(observacion);
			upd.setTipoMovimiento("SALIDA");

			CustodiosRequestDTO custodio = new CustodiosRequestDTO();
			custodio.setIdCustodio(idCustodioActual);
			upd.setFkCustodio(custodio);

			EquiposRequestDTO equipo = new EquiposRequestDTO();
			equipo.setIdEquipo(idEquipoActual);
			upd.setEquipos(List.of(equipo));

			servicioCustodias.actualizarCustodia(activa.getIdCustodiaEquipo(), upd);
		}

		session.setAttribute("ACTA_SALIDA_RECIENTE", seleccionadas);
		session.setAttribute("ACTA_SALIDA_FECHA", fechaFin);
		session.setAttribute("ACTA_SALIDA_OBSERVACION", observacion);

		redirectAttributes.addFlashAttribute("exito", "Custodia cerrada correctamente.");
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

		String tipoMov = (String) session.getAttribute("ACTA_TIPO_MOVIMIENTO");
		if (tipoMov == null || tipoMov.isBlank())
			tipoMov = "ASIGNACION";

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

		String tipoMov = (String) session.getAttribute("ACTA_TIPO_MOVIMIENTO");
		if (tipoMov == null || tipoMov.isBlank())
			tipoMov = "ASIGNACION";

		String nombreEntrega = sesionUsuario.getNombreUsuario();
		String deptoEntrega = null;
		if ("TRASLADO".equals(tipoMov)) {
			CustodiosResponseDTO origen = buscarCustodioOrigenTraslado(lista);
			if (origen != null) {
				nombreEntrega = origen.getNombre();
				deptoEntrega = origen.getFkDepartamento() != null ? origen.getFkDepartamento().getNombre() : "";
			}
		}
		byte[] pdfBytes = custodiasPdfService.generarActaEntregaPdfBytes(lista, nombreEntrega, deptoEntrega, tipoMov);

		String nombreArchivo = obtenerNombreArchivoPdf(tipoMov);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + nombreArchivo);
		response.getOutputStream().write(pdfBytes);
		response.getOutputStream().flush();

		enviarCorreoActaEntrega(lista, pdfBytes, tipoMov);
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

		String tipoMov = lista.get(0).getTipoMovimiento();
		if (tipoMov == null || tipoMov.isBlank())
			tipoMov = "ASIGNACION";

		String nombreEntrega = sesionUsuario.getNombreUsuario();
		String deptoEntrega = null;
		if ("TRASLADO".equals(tipoMov)) {
			CustodiosResponseDTO origen = buscarCustodioOrigenTraslado(lista);
			if (origen != null) {
				nombreEntrega = origen.getNombre();
				deptoEntrega = origen.getFkDepartamento() != null ? origen.getFkDepartamento().getNombre() : "";
			}
		}
		byte[] pdfBytes = custodiasPdfService.generarActaEntregaPdfBytes(lista, nombreEntrega, deptoEntrega, tipoMov);

		String nombreArchivo = obtenerNombreArchivoPdf(tipoMov);
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "inline; filename=" + nombreArchivo);
		response.getOutputStream().write(pdfBytes);
		response.getOutputStream().flush();

		enviarCorreoActaEntrega(lista, pdfBytes, tipoMov);
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

		String tipoMov = lista.get(0).getTipoMovimiento();
		if (tipoMov == null || tipoMov.isBlank())
			tipoMov = "ASIGNACION";

		try {
			String nombreEntrega = sesionUsuario.getNombreUsuario();
			String deptoEntrega = null;
			if ("TRASLADO".equals(tipoMov)) {
				CustodiosResponseDTO origen = buscarCustodioOrigenTraslado(lista);
				if (origen != null) {
					nombreEntrega = origen.getNombre();
					deptoEntrega = origen.getFkDepartamento() != null ? origen.getFkDepartamento().getNombre() : "";
				}
			}
			byte[] pdfBytes = custodiasPdfService.generarActaEntregaPdfBytes(lista, nombreEntrega, deptoEntrega,
					tipoMov);
			enviarCorreoActaEntrega(lista, pdfBytes, tipoMov);

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

	private void cargarFormularioNuevaCustodia(Model model, CustodiasRequestDTO custodia, String error) {
		Set<Integer> equiposNoDisponibles = obtenerIdsEquiposConCustodiaActiva();

		var equiposActivos = servicioEquipos.listarEquipos().stream().filter(EquiposResponseDTO::isEstado).toList();

		var custodiosActivos = servicioCustodios.listarCustodios().stream().filter(CustodiosResponseDTO::isEstado)
				.toList();

		var departamentosActivos = servicioDepartamento.listarDepartamentos().stream().filter(d -> d.isEstado())
				.toList();
		var cargosActivos = servicioCargo.listarCargos().stream().filter(c -> c.isEstado()).toList();
		var ubicacionesActivas = servicioUbicacion.listarUbicaciones().stream().filter(u -> u.isEstado()).toList();

		model.addAttribute("listaequipos", equiposActivos);
		model.addAttribute("listacustodios", custodiosActivos);
		model.addAttribute("listadepartamentos", departamentosActivos);
		model.addAttribute("listacargos", cargosActivos);
		model.addAttribute("listaubicaciones", ubicacionesActivas);
		model.addAttribute("custodia", custodia);
		model.addAttribute("equiposNoDisponiblesCount", equiposNoDisponibles.size());
		model.addAttribute("equiposNoDisponiblesIds", equiposNoDisponibles);

		// Mapa equipoId -> custodioId (para filtrar equipos por custodio origen en
		// traslado)
		Map<Integer, Integer> equipoCustodioMap = servicioCustodias.listarCustodias().stream()
				.filter(x -> x != null && x.isEstado() && x.getFkEquipo() != null && x.getFkCustodio() != null)
				.collect(Collectors.toMap(x -> x.getFkEquipo().getIdEquipo(), x -> x.getFkCustodio().getIdCustodio(),
						(a, b) -> a));
		model.addAttribute("equipoCustodioMap", equipoCustodioMap);

		if (error != null && !error.isBlank()) {
			model.addAttribute("error", error);
		}
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

	@lombok.Data
	public static class ActaResumen {
		private String key;
		private int numeroActa;
		private int idCustodio;
		private int minPk;
		private CustodiosResponseDTO custodio;
		private String tipoMovimiento;
		private String etiquetaTipo;
		private LocalDate fechaInicio;
		private LocalDate fechaFin;
		private boolean activa;
		private int cantidadEquipos;
	}

}
