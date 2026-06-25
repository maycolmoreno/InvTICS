package com.uisrael.consumogestionactivosapi.controlador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConfirmarLlegadaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RegistrarEtiquetaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiosResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ActivoInventarioResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.ConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.StockConsumibleResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

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
			String busq = q.toLowerCase().trim();
			return custodiosServicio.listarCustodios().stream()
				.filter(c -> c != null && c.isEstado())
				.filter(c -> busq.isEmpty()
					|| (c.getNombre() != null && c.getNombre().toLowerCase().contains(busq))
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
			RedirectAttributes redirect) {

		List<String> exitos = new ArrayList<>();
		List<String> errores = new ArrayList<>();
		LocalDate fecha = LocalDate.parse(fechaInicio);

		if ((equipoIds == null || equipoIds.isEmpty())
				&& (consumibleStockKeys == null || consumibleStockKeys.isEmpty())) {
			redirect.addFlashAttribute("error", "Seleccione al menos un activo o consumible para asignar.");
			return "redirect:/inventario/asignaciones";
		}

		if (equipoIds != null && !equipoIds.isEmpty()) {
			AsignacionLoteRequestDTO lote = new AsignacionLoteRequestDTO();
			lote.setEquipoIds(equipoIds);
			lote.setCustodioId(custodioId);
			lote.setFechaInicio(fecha);
			lote.setCondicionEntrega(condicionEntrega);
			lote.setRealizadoPor(realizadoPor);
			lote.setObservacion(observacion);
			try {
				var asignados = inventarioOperacionServicio.asignarActivosLote(lote);
				asignados.forEach(a -> exitos.add(a.getCodigoCresio() != null ? a.getCodigoCresio() : "activo"));
			} catch (Exception ex) {
				errores.add(mensajeError(ex));
			}
		}

		if (consumibleStockKeys != null) {
			for (String key : consumibleStockKeys) {
				String[] partes = key.split("_", 2);
				if (partes.length != 2) continue;
				AsignacionConsumibleRequestDTO req = new AsignacionConsumibleRequestDTO();
				req.setConsumibleId(Integer.valueOf(partes[0]));
				req.setCustodioId(custodioId);
				req.setBodegaId(Integer.valueOf(partes[1]));
				req.setCantidad(Integer.valueOf(params.getOrDefault("cantidad_" + key, "1")));
				req.setObservacion(observacion);
				try {
					inventarioOperacionServicio.asignarConsumible(req);
					exitos.add("consumible asignado");
				} catch (Exception ex) {
					errores.add(mensajeError(ex));
				}
			}
		}

		if (!errores.isEmpty()) {
			redirect.addFlashAttribute("error", "Errores en la asignacion: " + String.join("; ", errores));
		}
		if (!exitos.isEmpty()) {
			redirect.addFlashAttribute("success",
					"Asignacion registrada. Activos: " + String.join(", ", exitos) + ".");
		}
		return "redirect:/inventario/asignaciones";
	}

	@PostMapping("/asignaciones/activos")
	public String asignarActivo(@ModelAttribute AsignacionActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.asignarActivo(request);
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
	public String devolverActivo(@ModelAttribute DevolucionActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.devolverActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " devuelto correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo devolver el activo: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
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
	public String darBajaActivo(@ModelAttribute BajaActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.darBajaActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " dado de baja correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo dar de baja el activo: " + ex.getMessage());
		}
		return "redirect:/inventario/bajas";
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
		// custodios eliminados del modelo — ahora se cargan via AJAX en /custodios/buscar
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
		String nombreCompleto = sesionUsuario.getNombre();
		if (nombreCompleto == null || nombreCompleto.isBlank()) {
			nombreCompleto = sesionUsuario.getNombreUsuario();
		}
		if (nombreCompleto == null || nombreCompleto.isBlank()) {
			nombreCompleto = sesionUsuario.getCorreo();
		}
		model.addAttribute("usuarioActual", nombreCompleto != null ? nombreCompleto : "");
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

}
