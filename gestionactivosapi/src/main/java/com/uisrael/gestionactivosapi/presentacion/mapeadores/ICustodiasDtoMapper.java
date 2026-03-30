package com.uisrael.gestionactivosapi.presentacion.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.gestionactivosapi.dominio.entidades.Cargos;
import com.uisrael.gestionactivosapi.dominio.entidades.CategoriaEquipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodias;
import com.uisrael.gestionactivosapi.dominio.entidades.Custodios;
import com.uisrael.gestionactivosapi.dominio.entidades.Departamentos;
import com.uisrael.gestionactivosapi.dominio.entidades.Equipos;
import com.uisrael.gestionactivosapi.dominio.entidades.Marcas;
import com.uisrael.gestionactivosapi.dominio.entidades.Ubicaciones;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CustodiasRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CargosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CategoriaEquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.DepartamentosResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.EquiposResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MarcasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.UbicacionesResponseDTO;

@Mapper(componentModel = "spring")
public interface ICustodiasDtoMapper {

	@Mapping(target = "fkEquipo", expression = "java(mapEquipoReq(dto))")
	@Mapping(target = "fkCustodio", expression = "java(mapCustodioReq(dto))")
	Custodias toDomain(CustodiasRequestDTO dto);

	@Mapping(source = "fkCustodio.idCustodio", target = "idCustodio")
	CustodiasResponseDTO toResponseDto(Custodias custodia);

	default Equipos mapEquipoReq(CustodiasRequestDTO dto) {
		if (dto == null || dto.getEquipos() == null || dto.getEquipos().isEmpty()) {
			return null;
		}
		return new Equipos(dto.getEquipos().get(0).getIdEquipo(),
				null, null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, null,
				null, null, null, false, null, null, null);
	}

	default Custodios mapCustodioReq(CustodiasRequestDTO dto) {
		if (dto == null || dto.getFkCustodio() == null) {
			return null;
		}
		return new Custodios(dto.getFkCustodio().getIdCustodio(),
				null, null, null, null, null, false, null);
	}

	default EquiposResponseDTO map(Equipos e) {
		if (e == null) { return null; }
		EquiposResponseDTO dto = new EquiposResponseDTO();
		dto.setIdEquipo(e.getIdEquipo());
		dto.setCodigoSap(e.getCodigoSap());
		dto.setTipoEquipo(e.getTipoEquipo());
		dto.setModelo(e.getModelo());
		dto.setSerial(e.getSerial());
		dto.setProcesador(e.getProcesador());
		dto.setMemoriaRamGb(e.getMemoriaRamGb());
		dto.setCapacidadAlmacenamientoGb(e.getCapacidadAlmacenamientoGb());
		dto.setSistemaOperativo(e.getSistemaOperativo());
		dto.setLicenciaWindowsActivada(e.getLicenciaWindowsActivada());
		dto.setEtiquetaActivoFijo(e.getEtiquetaActivoFijo());
		dto.setTipoLicenciaOffice(e.getTipoLicenciaOffice());
		dto.setVersionOffice(e.getVersionOffice());
		dto.setUnionDominio(e.getUnionDominio());
		dto.setIp(e.getIp());
		dto.setMac(e.getMac());
		dto.setFechaCompra(e.getFechaCompra());
		dto.setPrecioCompra(e.getPrecioCompra());
		dto.setEstadoEquipo(e.getEstadoEquipo());
		dto.setObservacionEquipo(e.getObservacionEquipo());
		dto.setEstado(e.isEstado());
		dto.setFkMarca(map(e.getFkMarca()));
		dto.setFkCategoria(map(e.getFkCategoria()));
		dto.setFkUbicacion(map(e.getFkUbicacion()));
		return dto;
	}

	default CustodiosResponseDTO map(Custodios c) {
		if (c == null) { return null; }
		CustodiosResponseDTO dto = new CustodiosResponseDTO();
		dto.setIdCustodio(c.getIdCustodio());
		dto.setNombre(c.getNombre());
		dto.setCedula(c.getCedula());
		dto.setCorreo(c.getCorreo());
		dto.setTelefono(c.getTelefono());
		dto.setEstado(c.isEstado());
		dto.setFkDepartamento(map(c.getFkDepartamento()));
		dto.setFkCargo(map(c.getFkCargo()));
		dto.setFkUbicacion(map(c.getFkUbicacion()));
		return dto;
	}

	default DepartamentosResponseDTO map(Departamentos d) {
		if (d == null) return null;
		DepartamentosResponseDTO dto = new DepartamentosResponseDTO();
		dto.setIdDepartamento(d.getIdDepartamento());
		dto.setNombre(d.getNombre());
		dto.setEstado(d.isEstado());
		return dto;
	}

	default CargosResponseDTO map(Cargos cg) {
		if (cg == null) return null;
		CargosResponseDTO dto = new CargosResponseDTO();
		dto.setIdCargo(cg.getIdCargo());
		dto.setNombre(cg.getNombre());
		dto.setEstado(cg.isEstado());
		return dto;
	}

	default UbicacionesResponseDTO map(Ubicaciones u) {
		if (u == null) return null;
		UbicacionesResponseDTO dto = new UbicacionesResponseDTO();
		dto.setIdUbicacion(u.getIdUbicacion());
		dto.setNombre(u.getNombre());
		dto.setAgencia(u.getAgencia());
		dto.setEstado(u.isEstado());
		return dto;
	}

	default MarcasResponseDTO map(Marcas m) {
		if (m == null) { return null; }
		MarcasResponseDTO dto = new MarcasResponseDTO();
		dto.setIdMarca(m.getIdMarca());
		dto.setNombre(m.getNombre());
		dto.setEstado(m.isEstado());
		return dto;
	}

	default CategoriaEquiposResponseDTO map(CategoriaEquipos c) {
		if (c == null) { return null; }
		CategoriaEquiposResponseDTO dto = new CategoriaEquiposResponseDTO();
		dto.setIdCategoria(c.getIdCategoria());
		dto.setNombre(c.getNombre());
		dto.setEstado(c.isEstado());
		return dto;
	}
}

