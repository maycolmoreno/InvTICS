package com.uisrael.consumogestionactivosapi.controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.AsignacionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BajaActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.BodegaRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.ConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.DevolucionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.OrdenCompraRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.RecepcionConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoActivoRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.inventario.TrasladoConsumibleRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.CustodiasResponseDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.inventario.BodegaResponseDTO;
import com.uisrael.consumogestionactivosapi.service.ICategoriaEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiosServicio;
import com.uisrael.consumogestionactivosapi.service.ICustodiasServicio;
import com.uisrael.consumogestionactivosapi.service.IInventarioOperacionServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inventario")
public class InventarioControlador {

	private final ICustodiasServicio servicioCustodias;
	private final ICustodiosServicio custodiosServicio;
	private final IInventarioOperacionServicio inventarioOperacionServicio;
	private final IMarcasServicio marcasServicio;
	private final ICategoriaEquiposServicio categoriaEquiposServicio;

	@GetMapping("/ingreso-bodega")
	public String ingresoBodega(Model model) {
		cargarModeloIngreso(model);
		return "Inventario/ingresoBodega";
	}

	@PostMapping("/bodegas")
	public String crearBodega(@ModelAttribute BodegaRequestDTO request, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.crearBodega(request);
			redirect.addFlashAttribute("success", "Bodega creada correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo crear la bodega: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
	}

	@PostMapping("/consumibles")
	public String crearConsumible(@ModelAttribute ConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.crearConsumible(request);
			redirect.addFlashAttribute("success", "Consumible creado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo crear el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
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

	@PostMapping("/recepcion/activos")
	public String recibirActivo(@ModelAttribute RecepcionActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.recibirActivo(request);
			redirect.addFlashAttribute("success", "Activo ingresado con codigo " + activo.getCodigoCresio() + ".");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo recibir el activo: " + ex.getMessage());
		}
		return "redirect:/inventario/recepcion";
	}

	@PostMapping("/recepcion/consumibles")
	public String recibirConsumible(@ModelAttribute RecepcionConsumibleRequestDTO request, RedirectAttributes redirect) {
		try {
			inventarioOperacionServicio.recibirConsumible(request);
			redirect.addFlashAttribute("success", "Stock de consumible actualizado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo recibir el consumible: " + ex.getMessage());
		}
		return "redirect:/inventario/recepcion";
	}

	@PostMapping("/asignaciones/activos")
	public String asignarActivo(@ModelAttribute AsignacionActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.asignarActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " asignado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo asignar el activo: " + ex.getMessage());
		}
		return "redirect:/inventario/stock";
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
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " trasladado correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo trasladar el activo: " + ex.getMessage());
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
		return "redirect:/inventario/bajas";
	}

	@PostMapping("/bajas/activos")
	public String darBajaActivo(@ModelAttribute BajaActivoRequestDTO request, RedirectAttributes redirect) {
		try {
			var activo = inventarioOperacionServicio.darBajaActivo(request);
			redirect.addFlashAttribute("success", "Activo " + activo.getCodigoCresio() + " dado de baja correctamente.");
		} catch (Exception ex) {
			redirect.addFlashAttribute("error", "No se pudo dar de baja el activo: " + ex.getMessage());
		}
		return "redirect:/inventario/ingreso-bodega";
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

	private void cargarModeloIngreso(Model model) {
		List<BodegaResponseDTO> bodegas = inventarioOperacionServicio.listarBodegas();
		model.addAttribute("bodegas", bodegas);
		model.addAttribute("consumibles", inventarioOperacionServicio.listarConsumibles());
		model.addAttribute("ordenesCompra", inventarioOperacionServicio.listarOrdenesCompra());
		model.addAttribute("activosEnBodega", inventarioOperacionServicio.listarActivosEnBodega());
		model.addAttribute("custodios", custodiosServicio.listarCustodios());
		model.addAttribute("marcas", marcasServicio.listarMarca());
		model.addAttribute("categorias", categoriaEquiposServicio.listarCategoriaEquipo());
		model.addAttribute("stock", bodegas != null && !bodegas.isEmpty()
				? inventarioOperacionServicio.listarStockPorBodega(bodegas.get(0).getIdBodega())
				: List.of());
		model.addAttribute("movimientos", inventarioOperacionServicio.listarMovimientosRecientes());
		model.addAttribute("custodiasActivas", obtenerCustodiasActivas());
		model.addAttribute("bodegaRequest", new BodegaRequestDTO());
		model.addAttribute("consumibleRequest", new ConsumibleRequestDTO());
		model.addAttribute("ordenCompraRequest", new OrdenCompraRequestDTO());
		model.addAttribute("recepcionActivoRequest", new RecepcionActivoRequestDTO());
		model.addAttribute("recepcionConsumibleRequest", new RecepcionConsumibleRequestDTO());
		model.addAttribute("asignacionActivoRequest", new AsignacionActivoRequestDTO());
		model.addAttribute("asignacionConsumibleRequest", new AsignacionConsumibleRequestDTO());
		model.addAttribute("devolucionActivoRequest", new DevolucionActivoRequestDTO());
		model.addAttribute("devolucionConsumibleRequest", new DevolucionConsumibleRequestDTO());
		model.addAttribute("trasladoActivoRequest", new TrasladoActivoRequestDTO());
		model.addAttribute("trasladoConsumibleRequest", new TrasladoConsumibleRequestDTO());
		model.addAttribute("bajaActivoRequest", new BajaActivoRequestDTO());
	}
}
