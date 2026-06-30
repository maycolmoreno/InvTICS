package com.uisrael.consumogestionactivosapi.controlador;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionLoteRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarRecepcionStockRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.EnviarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RetornarReparacionRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AdoptarInventarioInicialRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConfirmarLlegadaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarEtiquetaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.EquiposResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.AsignacionActivosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;
import com.uisrael.consumogestionactivosapi.service.ActaStorageService;
import com.uisrael.consumogestionactivosapi.service.CustodiasPdfService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClientResponseException;
import com.uisrael.consumogestionactivosapi.security.SesionUsuario;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioControlador {

	private static final String DEPARTAMENTO_TIC = "TECNOLOGÍAS E INNOVACIÓN";

	private static String mensajeError(Exception ex) {
		if (ex instanceof RestClientResponseException rce) {
			String body = rce.getResponseBodyAsString();
			if (body != null && body.contains("\"message\"")) {
				try {
					int ini = body.indexOf("\"message\"") + 10;
					int desde = body.indexOf('"', ini) + 1;
					int hasta = body.indexOf('"', desde);
					if (desde > 0 && hasta > desde) {
						return body.substring(desde, hasta);
					}
				} catch (Exception ignored) {}
			}
		}
		return ex.getMessage();
	}

	private final ICustodiasServicio servicioCustodias;
	private final ICustodiosServicio custodiosServicio;
	private final IInventarioOperacionServicio inventarioOperacionServicio;
	private final IMarcasServicio marcasServicio;
	private final ICategoriaEquiposServicio categoriaEquiposServicio;
	private final CustodiasPdfService custodiasPdfService;
	private final ActaStorageService actaStorageService;
	private final SesionUsuario sesionUsuario;

	@GetMapping("/catalogos")
	public String catalogos(Model model) {
		cargarModeloCatalogos(model);
		return "Inventario/catalogos";
	}

	@GetMapping("/ingreso-bodega")
	public String ingresoBodegaLegacy() {
		return "redirect:/inventario/catalogos";
	}

	@PostMapping("/bodegas")
	public String crearBodega(@ModelAttribute BodegaRequestDTO request, RedirectAttributes redirect) {
		Integer custodioId = request.getCustodioResponsableId();
		if (custodioId == null) {
			redirect.addFlashAttribute("error", "Debe seleccionar un custodio responsable.");
			return "redirect:/inventario/catalogos";
		}
		try {
			CustodiosResponseDTO custodio = custodiosServicio.obtenerPorId(custodioId);
			if (!custodio.isEstado()) {
				redirect.addFlashAttribute("error", "El custodio responsable debe estar activo.");
				return "redirect:/inventario/catalogos";
			}
			String depto = custodio.getFkCargo() != null && custodio.getFkCargo().getFkDepartamento() != null
					? custodio.getFkCargo().getFkDepartamento().getNombre()
					: null;
			if (!DEPARTAMENTO_TIC.equalsIgnoreCase(depto)) {
				redirect.addFlashAttribute("error",
						"El custodio responsable debe pertenecer al departamento TECNOLOGÍAS E INNOVACIÓN.");
				return "redirect:/inventario/catalogos";
			}
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo verificar el custodio: " + ex.getMessage());
			return "redirect:/inventario/catalogos";
		}
		try {
			inventarioOperacionServicio.crearBodega(request);
			redirect.addFlashAttribute("success", "Bodega creada correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo crear la bodega: " + ex.getMessage());
		}
		return "redirect:/inventario/catalogos";
	}

	@PostMapping("/consumibles")
	public String crearConsumible(@ModelAttribute ConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.crearConsumible(request);
			redirect.addFlashAttribute("success", "Consumible creado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo crear el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/catalogos";
	}

	@PostMapping("/consumibles/{id}/actualizar")
	public String actualizarConsumible(@PathVariable Integer id,
			@ModelAttribute ConsumibleRequestDTO request,
			RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.actualizarConsumible(id, request);
			redirect.addFlashAttribute("success", "Consumible actualizado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo actualizar el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/catalogos";
	}

	@PostMapping("/consumibles/{id}/estado")
	public String cambiarEstadoConsumible(@PathVariable Integer id,
			@ModelAttribute ConsumibleRequestDTO request,
			RedirectAttributes redirect) {
		boolean estado = Boolean.TRUE.equals(request.getEstado());
		try {
			inventarioOperacionServicio.cambiarEstadoConsumible(id, estado);
			redirect.addFlashAttribute("success", estado
					? "Consumible reactivado correctamente."
					: "Consumible dado de baja correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo cambiar el estado del consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/catalogos";
	}

	@PostMapping("/ordenes-compra")
	public String crearOrdenCompra(@ModelAttribute OrdenCompraRequestDTO request, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.crearOrdenCompra(request);
			redirect.addFlashAttribute("success", "Orden de compra creada correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo crear la orden de compra: " + ex.getMessage());
		}
		return "redirect:/inventario/compras";
	}

	@PostMapping("/ordenes-compra/{id}/confirmar-recepcion")
	public String confirmarRecepcionOrden(@PathVariable Integer id,
			RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.confirmarRecepcionOrden(id);
			redirect.addFlashAttribute("success", "Recepcion de orden de compra confirmada.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo confirmar la recepcion: " + ex.getMessage());
		}
		return "redirect:/inventario/compras";
	}

	@PostMapping("/ordenes-compra/{id}/cancelar")
	public String cancelarOrdenCompra(@PathVariable Integer id, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.cancelarOrdenCompra(id);
			redirect.addFlashAttribute("success", "Orden de compra cancelada correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo cancelar la orden: " + ex.getMessage());
		}
		return "redirect:/inventario/compras";
	}

	@GetMapping("/asignaciones")
	public String asignaciones(Model model) {
		cargarModeloAsignaciones(model);
		return "Inventario/asignaciones";
	}

	@GetMapping(value = "/custodios/buscar", produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> buscarCustodios(
			@RequestParam(name = "q", defaultValue = "") String q) {
		try {
			String busq = sinTildes(q.toLowerCase().trim());
			return custodiosServicio.listarCustodios().stream()
				.filter(c -> c != null && c.isEstado())
				.filter(c -> busq.isEmpty()
					|| (c.getNombre() != null && sinTildes(c.getNombre().toLowerCase()).contains(busq))
					|| (c.getCedula() != null && c.getCedula().contains(busq)))
				.limit(12)
				.map(c -> {
					Map<String, Object> m = new LinkedHashMap<>();
					m.put("id", c.getIdCustodio());
					m.put("nombre", c.getNombre() != null ? c.getNombre() : "");
					m.put("cedula", c.getCedula() != null ? c.getCedula() : "");
					return m;
				})
				.toList();
		} catch (Exception ex) {
			return List.of();
		}
	}

	private static String sinTildes(String s) {
		if (s == null) return "";
		return Normalizer.normalize(s, Normalizer.Form.NFD)
			.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	@PostMapping("/asignaciones/lote")
	public String asignarLote(
			@RequestParam Integer custodioId,
			@RequestParam(required = false) List<Integer> equipoIds,
			@RequestParam(required = false) List<String> consumibleStockKeys,
			@RequestParam String condicionEntrega,
			@RequestParam String fechaInicio,
			@RequestParam(required = false) String realizadoPor,
			@RequestParam(required = false) String observacion,
			@RequestParam Map<String, String> params,
			RedirectAttributes redirect,
			HttpSession session) {

		if (equipoIds == null || equipoIds.isEmpty()) {
			redirect.addFlashAttribute("error", "Seleccione al menos un activo para asignar. Para entregar consumibles use el módulo Stock.");
			return "redirect:/inventario/asignaciones";
		}

		boolean hayConsumiblesIgnorados = consumibleStockKeys != null && !consumibleStockKeys.isEmpty();

		LocalDate fecha;
		try {
			fecha = LocalDate.parse(fechaInicio);
		} catch (Exception e) {
			redirect.addFlashAttribute("error", "Fecha de inicio inválida: " + fechaInicio);
			return "redirect:/inventario/asignaciones";
		}

		List<String> errores = new ArrayList<>();
		AsignacionActivosResponseDTO asignacion = null;
		String advertenciaActa = null;

		AsignacionLoteRequestDTO lote = new AsignacionLoteRequestDTO();
		lote.setEquipoIds(equipoIds);
		lote.setCustodioId(custodioId);
		lote.setFechaInicio(fecha);
		lote.setCondicionEntrega(condicionEntrega);
		lote.setRealizadoPor(realizadoPor);
		lote.setObservacion(observacion);
		try {
			asignacion = inventarioOperacionServicio.asignarActivosLote(lote);
			try {
				registrarActaAsignacion(asignacion != null ? asignacion.getCustodias() : null, realizadoPor, fecha);
			} catch (Exception actaEx) {
				advertenciaActa = "Asignacion registrada, pero no se pudo generar el acta: " + mensajeError(actaEx);
			}
		} catch (Exception ex) {
			errores.add(mensajeError(ex));
		}

		if (!errores.isEmpty()) {
			redirect.addFlashAttribute("error", "Errores en la asignacion: " + String.join("; ", errores));
			return "redirect:/inventario/asignaciones";
		}

		int totalAsignados = (asignacion != null && asignacion.getActivos() != null)
				? asignacion.getActivos().size() : equipoIds.size();
		String msgExito = "Asignación registrada exitosamente. " + totalAsignados + " activo(s) asignado(s).";
		String advertencia = advertenciaActa;
		if (hayConsumiblesIgnorados) {
			String msgConsumibles = "Los consumibles seleccionados no se procesaron: use el módulo Stock para entregarlos.";
			advertencia = advertencia != null ? advertencia + " | " + msgConsumibles : msgConsumibles;
		}
		if (advertencia != null) {
			redirect.addFlashAttribute("warning", advertencia);
		}
		redirect.addFlashAttribute("success", msgExito);
		return "redirect:/inventario/asignaciones";
	}

	@GetMapping("/asignaciones/resultado")
	public String resultadoAsignacion(RedirectAttributes redirect) {
		return "redirect:/inventario/asignaciones";
	}

	@PostMapping("/asignaciones/activos")
	public String asignarActivo(@ModelAttribute AsignacionActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.asignarActivo(request);
			try {
				LocalDate fecha = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDate.now();
				List<CustodiasResponseDTO> custodias = servicioCustodias.listarCustodias().stream()
						.filter(c -> c != null && c.isEstado()
								&& c.getFkEquipo() != null
								&& request.getEquipoId().equals(c.getFkEquipo().getIdEquipo()))
						.toList();
				registrarActaAsignacion(custodias, request.getRealizadoPor(), fecha);
			} catch (Exception actaEx) {
				redirect.addFlashAttribute("warning",
						"Asignación registrada, pero no se pudo generar el acta: " + mensajeError(actaEx));
			}
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " asignado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo asignar el activo: " + mensajeError(ex));
		}
		return "redirect:/inventario/asignaciones";
	}

	@PostMapping("/asignaciones/consumibles")
	public String asignarConsumible(@ModelAttribute AsignacionConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			var stock = inventarioOperacionServicio.asignarConsumible(request);
			redirect.addFlashAttribute("success", "Consumible entregado. Stock restante: " + stock.getCantidad() + ".");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo entregar el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
	}

	@PostMapping("/devoluciones/activos")
	public String devolverActivo(@ModelAttribute DevolucionActivoRequestDTO request,
			@RequestParam(required = false) String returnTo,
			RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.devolverActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " devuelto correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo devolver el activo: " + ex.getMessage());
		}
		return redirectLocal(returnTo, "/inventario/stock");
	}

	@PostMapping("/devoluciones/consumibles")
	public String devolverConsumible(@ModelAttribute DevolucionConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			var stock = inventarioOperacionServicio.devolverConsumible(request);
			redirect.addFlashAttribute("success", "Consumible devuelto. Stock actual: " + stock.getCantidad() + ".");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo devolver el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/traslados";
	}

	@PostMapping("/traslados/activos")
	public String trasladarActivo(@ModelAttribute TrasladoActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.trasladarActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " en transito. Confirme la llegada cuando arribe a destino.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo iniciar el traslado: " + ex.getMessage());
		}
		return "redirect:/inventario/traslados";
	}

	@PostMapping("/traslados/activos/confirmar")
	public String confirmarLlegadaActivo(@ModelAttribute ConfirmarLlegadaActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.confirmarLlegadaActivo(request);
			redirect.addFlashAttribute("success", "Llegada del activo " + activo.getCodigoCresio() + " confirmada. Ahora esta EN_BODEGA.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo confirmar la llegada: " + ex.getMessage());
		}
		return "redirect:/inventario/traslados";
	}

	@PostMapping("/traslados/consumibles")
	public String trasladarConsumible(@ModelAttribute TrasladoConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			var stock = inventarioOperacionServicio.trasladarConsumible(request);
			redirect.addFlashAttribute("success", "Consumible trasladado. Stock destino: " + stock.getCantidad() + ".");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo trasladar el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
	}

	@PostMapping("/activos/{id}/etiqueta")
	public String registrarEtiqueta(@PathVariable Integer id,
			@ModelAttribute RegistrarEtiquetaRequestDTO request,
			RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.registrarEtiqueta(id, request);
			redirect.addFlashAttribute("success",
					"Etiqueta registrada correctamente para " + (activo.getCodigoCresio() != null ? activo.getCodigoCresio() : "el activo") + ".");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo registrar la etiqueta: " + mensajeError(ex));
		}
		return "redirect:/activos/equipos/" + id + "/expediente";
	}

	@PostMapping("/bajas/activos")
	public String darBajaActivo(@ModelAttribute BajaActivoRequestDTO request,
			@RequestParam(required = false) String returnTo,
			RedirectAttributes redirect,
			HttpSession session) {
		try {
			request.setAutorizadoPor(sesionUsuario.getNombre());
			var activo = inventarioOperacionServicio.darBajaActivo(request);

			EquiposResponseDTO equipoDto = EquiposResponseDTO.desdeActivo(activo);

			session.setAttribute("ACTA_BAJA_EQUIPOS",    List.of(equipoDto));
			session.setAttribute("ACTA_BAJA_FECHA",       request.getFechaBaja() != null
			                                               ? request.getFechaBaja()
			                                               : LocalDate.now());
			session.setAttribute("ACTA_BAJA_MOTIVO",      request.getMotivo());
			session.setAttribute("ACTA_BAJA_OBSERVACION", request.getObservacion());
			session.setAttribute("ACTA_BAJA_RETURN_URL",  "/inventario/bajas");

			redirect.addFlashAttribute("success", "Activo dado de baja correctamente.");
			return "redirect:/custodias/actaBaja";
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo dar de baja el activo: " + mensajeError(ex));
			return redirectLocal(returnTo, "/inventario/bajas");
		}
	}

	private String redirectLocal(String returnTo, String fallback) {
		if (returnTo != null && returnTo.startsWith("/") && !returnTo.startsWith("//")) {
			return "redirect:" + returnTo;
		}
		return "redirect:" + fallback;
	}

	@GetMapping("/reparaciones")
	public String reparaciones(Model model) {
		try {
			model.addAttribute("activosEnReparacion", inventarioOperacionServicio.listarActivosEnReparacion());
		} catch (Exception ex) {
			model.addAttribute("activosEnReparacion", List.of());
			model.addAttribute("error", "No se pudo cargar la lista de activos en reparacion: " + ex.getMessage());
		}
		try {
			model.addAttribute("bodegas", inventarioOperacionServicio.listarBodegas());
		} catch (Exception ex) {
			model.addAttribute("bodegas", List.of());
		}
		model.addAttribute("enviarReparacionRequest", new EnviarReparacionRequestDTO());
		model.addAttribute("retornarReparacionRequest", new RetornarReparacionRequestDTO());
		try {
			model.addAttribute("activosEnBodega", inventarioOperacionServicio.listarActivosEnBodega());
		} catch (Exception ex) {
			model.addAttribute("activosEnBodega", List.of());
		}
		return "Inventario/reparaciones";
	}

	@PostMapping("/reparaciones/enviar")
	public String enviarAReparacion(@ModelAttribute EnviarReparacionRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.enviarAReparacion(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " enviado a reparacion.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo enviar a reparacion: " + ex.getMessage());
		}
		return "redirect:/inventario/reparaciones";
	}

	@PostMapping("/reparaciones/retornar")
	public String retornarDeReparacion(@ModelAttribute RetornarReparacionRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.retornarDeReparacion(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " retornado a bodega.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo retornar de reparacion: " + ex.getMessage());
		}
		return "redirect:/inventario/reparaciones";
	}

	@PostMapping("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/stock")
	public String recibirStock(@PathVariable Integer idOC,
			@PathVariable Integer idDetalle,
			@ModelAttribute RegistrarRecepcionStockRequestDTO request,
			RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.registrarRecepcionStock(idOC, idDetalle, request);
			redirect.addFlashAttribute("success", "Stock recibido correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo registrar la recepcion de stock: " + ex.getMessage());
		}
		return "redirect:/inventario/ordenes-compra/" + idOC + "/gestionar";
	}

	@PostMapping("/ordenes-compra/{idOC}/detalles/{idDetalle}/recepciones/activo")
	public String recibirActivo(@PathVariable Integer idOC,
			@PathVariable Integer idDetalle,
			@ModelAttribute RegistrarRecepcionActivoRequestDTO request,
			RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.registrarRecepcionActivo(idOC, idDetalle, request);
			redirect.addFlashAttribute("success", "Activo ingresado a bodega correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo registrar la recepcion del activo: " + ex.getMessage());
		}
		return "redirect:/inventario/ordenes-compra/" + idOC + "/gestionar";
	}

	@GetMapping("/por-sucursal")
	public String inventarioPorSucursal(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerSucursal,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((sucursal, lista) -> conteo.put(sucursal,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porSucursal";
	}

	@GetMapping("/por-departamento")
	public String inventarioPorDepartamento(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerDepartamento,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((depto, lista) -> conteo.put(depto,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porDepartamento";
	}

	@GetMapping("/por-custodio")
	public String inventarioPorCustodio(Model model) {
		List<CustodiasResponseDTO> activas = obtenerCustodiasActivas();

		Map<String, List<CustodiasResponseDTO>> agrupado = activas.stream()
				.collect(Collectors.groupingBy(this::extraerCustodio,
						LinkedHashMap::new, Collectors.toList()));

		Map<String, Long> conteo = new LinkedHashMap<>();
		agrupado.forEach((custodio, lista) -> conteo.put(custodio,
				lista.stream().map(c -> c.getFkEquipo().getIdEquipo()).distinct().count()));

		model.addAttribute("agrupado", agrupado);
		model.addAttribute("conteo", conteo);
		model.addAttribute("totalEquipos", activas.stream()
				.map(c -> c.getFkEquipo().getIdEquipo()).distinct().count());
		return "Inventario/porCustodio";
	}

	private List<CustodiasResponseDTO> obtenerCustodiasActivas() {
		List<CustodiasResponseDTO> todas = servicioCustodias.listarCustodias();
		if (todas == null) return new ArrayList<>();
		return todas.stream()
				.filter(c -> c.isEstado() && c.getFkEquipo() != null)
				.toList();
	}

	private String extraerSucursal(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null && c.getFkCustodio().getFkUbicacion() != null) {
			String nombre = c.getFkCustodio().getFkUbicacion().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin sucursal";
	}

	private String extraerDepartamento(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null && c.getFkCustodio().getFkDepartamento() != null) {
			String nombre = c.getFkCustodio().getFkDepartamento().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin departamento";
	}

	private String extraerCustodio(CustodiasResponseDTO c) {
		if (c.getFkCustodio() != null) {
			String nombre = c.getFkCustodio().getNombre();
			if (nombre != null && !nombre.isBlank()) return nombre;
		}
		return "Sin custodio";
	}

	private void registrarActaAsignacion(List<CustodiasResponseDTO> custodias,
			String realizadoPor, LocalDate fecha) throws Exception {
		if (custodias == null || custodias.isEmpty()) {
			throw new IllegalStateException("la API no devolvio custodias para el acta");
		}
		int idCustodio = custodias.stream()
				.mapToInt(c -> c.getFkCustodio() != null
						? c.getFkCustodio().getIdCustodio()
						: c.getIdCustodio())
				.filter(id -> id > 0)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("la custodia no contiene custodio valido"));
		CustodiasResponseDTO cabecera = custodias.get(0);
		List<Integer> idsCustodias = custodias.stream()
				.map(CustodiasResponseDTO::getIdCustodiaEquipo)
				.filter(id -> id > 0)
				.toList();
		if (idsCustodias.isEmpty()) {
			throw new IllegalStateException("la API no devolvio identificadores de custodia");
		}
		LocalDate fechaActa = cabecera.getFechaInicio() != null ? cabecera.getFechaInicio() : fecha;
		String nombreEntrega = realizadoPor != null && !realizadoPor.isBlank() ? realizadoPor : usuarioActual();
		byte[] pdfBytes = custodiasPdfService.generarActaEntregaPdfBytes(
				custodias, nombreEntrega, DEPARTAMENTO_TIC, "ASIGNACION");
		String nombreArchivo = actaStorageService.guardarActaPdf(pdfBytes, "ASIGNACION", idCustodio, fechaActa);
		actaStorageService.registrarRutaEnCustodias(idsCustodias, nombreArchivo);
	}

	private void cargarModeloAsignaciones(Model model) {
		Set<Integer> equiposConCustodiaActiva = equiposConCustodiaActiva();
		try {
			model.addAttribute("activosEnBodega", inventarioOperacionServicio.listarActivosEnBodega().stream()
					.filter(a -> a != null
							&& Boolean.TRUE.equals(a.getEtiquetado())
							&& !equiposConCustodiaActiva.contains(a.getIdEquipo()))
					.toList());
		}
		catch (Exception e) { model.addAttribute("activosEnBodega", List.of()); }
		Set<Integer> consumiblesActivos = consumiblesActivos();
		List<BodegaResponseDTO> bodegas;
		try { bodegas = inventarioOperacionServicio.listarBodegas(); }
		catch (Exception e) { bodegas = List.of(); }
		model.addAttribute("bodegas", bodegas);
		List<StockConsumibleResponseDTO> stockConsumibles = new ArrayList<>();
		for (BodegaResponseDTO bodega : bodegas) {
			if (bodega != null && bodega.isEstado() && bodega.getIdBodega() != null) {
				try {
					stockConsumibles.addAll(inventarioOperacionServicio.listarStockPorBodega(bodega.getIdBodega()));
				} catch (Exception ignored) {}
			}
		}
		model.addAttribute("stockConsumibles", stockConsumibles.stream()
				.filter(s -> s != null && consumiblesActivos.contains(s.getConsumibleId()))
				.toList());
		String nombreCompleto = usuarioActual();
		model.addAttribute("usuarioActual", nombreCompleto != null ? nombreCompleto : "");
	}

	private String usuarioActual() {
		String nombreCompleto = sesionUsuario.getNombre();
		if (nombreCompleto == null || nombreCompleto.isBlank()) {
			nombreCompleto = sesionUsuario.getNombreUsuario();
		}
		if (nombreCompleto == null || nombreCompleto.isBlank()) {
			nombreCompleto = sesionUsuario.getCorreo();
		}
		return nombreCompleto != null ? nombreCompleto : "";
	}

	private Set<Integer> equiposConCustodiaActiva() {
		try {
			return servicioCustodias.listarCustodias().stream()
					.filter(c -> c != null && c.isEstado() && c.getFkEquipo() != null)
					.map(c -> c.getFkEquipo().getIdEquipo())
					.collect(Collectors.toSet());
		} catch (Exception e) {
			return Set.of();
		}
	}

	private Set<Integer> consumiblesActivos() {
		try {
			return inventarioOperacionServicio.listarConsumibles().stream()
					.filter(ConsumibleResponseDTO::isEstado)
					.map(ConsumibleResponseDTO::getIdConsumible)
					.collect(Collectors.toSet());
		} catch (Exception e) {
			return new HashSet<>();
		}
	}

	private void cargarModeloCatalogos(Model model) {
		model.addAttribute("bodegas", inventarioOperacionServicio.listarBodegas());
		model.addAttribute("consumibles", inventarioOperacionServicio.listarConsumibles());
		model.addAttribute("custodios", custodiosServicio.listarCustodios());
		model.addAttribute("bodegaRequest", new BodegaRequestDTO());
		model.addAttribute("consumibleRequest", new ConsumibleRequestDTO());
	}

	@GetMapping("/inventario-inicial")
	public String inventarioInicial(Model model) {
		try {
			model.addAttribute("equiposSinInventario", inventarioOperacionServicio.listarSinInventario());
		} catch (Exception e) {
			model.addAttribute("equiposSinInventario", new java.util.ArrayList<>());
			model.addAttribute("errorCarga", "No se pudieron cargar los equipos: " + mensajeError(e));
		}
		try {
			model.addAttribute("bodegas", inventarioOperacionServicio.listarBodegas());
		} catch (Exception e) {
			model.addAttribute("bodegas", new java.util.ArrayList<>());
		}
		try {
			model.addAttribute("custodios", custodiosServicio.listarCustodios());
		} catch (Exception e) {
			model.addAttribute("custodios", new java.util.ArrayList<>());
		}
		return "Inventario/inventarioInicial";
	}

	@PostMapping("/activos/{id}/adoptar")
	public String adoptarInventarioInicial(@PathVariable Integer id,
			@ModelAttribute AdoptarInventarioInicialRequestDTO request,
			RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.adoptarInventarioInicial(id, request);
			redirect.addFlashAttribute("success",
					"Activo adoptado correctamente" + (activo.getCodigoCresio() != null ? ": " + activo.getCodigoCresio() : "."));
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo adoptar el activo: " + mensajeError(ex));
		}
		return "redirect:/inventario/inventario-inicial";
	}

}
