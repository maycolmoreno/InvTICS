package com.uisrael.gestionactivosapi.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.gestionactivosapi.aplicacion.casosuso.entradas.ICustodiasUseCase;
import com.uisrael.gestionactivosapi.presentacion.dto.request.CustodiasRequestDTO;
import com.uisrael.gestionactivosapi.presentacion.dto.response.CustodiasResponseDTO;
import com.uisrael.gestionactivosapi.presentacion.mapeadores.ICustodiasDtoMapper;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/custodias")
public class CustodiasControlador {

    private final ICustodiasUseCase custodiasUseCase;
    private final ICustodiasDtoMapper mapper;

    public CustodiasControlador(ICustodiasUseCase custodiasUseCase, ICustodiasDtoMapper mapper) {
        this.custodiasUseCase = custodiasUseCase;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public List<CustodiasResponseDTO> crear(@Valid @RequestBody CustodiasRequestDTO request) {

        return request.getEquipos().stream().map(eq -> {

            CustodiasRequestDTO uno = new CustodiasRequestDTO();
            uno.setFechaInicio(request.getFechaInicio());
            uno.setFechaFin(request.getFechaFin());
            uno.setObservacion(request.getObservacion());
            uno.setEstado(request.isEstado());
            uno.setFkCustodio(request.getFkCustodio());
            uno.setTipoMovimiento(request.getTipoMovimiento());

            // Aqui asignamos un equipo por registro
            uno.setEquipos(List.of(eq));

            return mapper.toResponseDto(
                    custodiasUseCase.crear(mapper.toDomain(uno))
            );

        }).toList();
    }


    @GetMapping
    public List<CustodiasResponseDTO> listar() {
        return custodiasUseCase.listar().stream().map(mapper::toResponseDto).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustodiasResponseDTO> obtenerPorId(@PathVariable int id) {
        return ResponseEntity.ok(mapper.toResponseDto(custodiasUseCase.obtenerPorId(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustodiasResponseDTO> actualizar(@PathVariable int id,
            @Valid @RequestBody CustodiasRequestDTO request) {

        return ResponseEntity.ok(
            mapper.toResponseDto(
                custodiasUseCase.actualizar(id, mapper.toDomain(request))
            )
        );
    }

    @PutMapping("/estado/{id}")
    public ResponseEntity<CustodiasResponseDTO> actualizarEstado(
        @PathVariable int id,
        @RequestBody CustodiasRequestDTO request
    ){

        return ResponseEntity.ok(
            mapper.toResponseDto(
                custodiasUseCase.actualizarEstado(id, mapper.toDomain(request))
            )
        );
    }

    @GetMapping("/conteo-tipo/{tipo}")
    public ResponseEntity<Map<String, Long>> contarPorTipo(@PathVariable String tipo) {
        long count = custodiasUseCase.contarPorTipoMovimiento(tipo);
        return ResponseEntity.ok(Map.of("tipo", count));
    }

}

