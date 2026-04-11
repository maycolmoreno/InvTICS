package com.uisrael.consumogestionactivosapi.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.uisrael.consumogestionactivosapi.modelo.dto.request.CategoriaEquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.EquiposRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.FilaImportDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.request.MarcasRequestDTO;
import com.uisrael.consumogestionactivosapi.modelo.dto.response.MarcasResponseDTO;
import com.uisrael.consumogestionactivosapi.service.IEquiposServicio;
import com.uisrael.consumogestionactivosapi.service.IImportarServicio;
import com.uisrael.consumogestionactivosapi.service.IMarcasServicio;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImportarServicioImpl implements IImportarServicio {

    private static final Logger logger = LoggerFactory.getLogger(ImportarServicioImpl.class);
    private final IEquiposServicio servicioEquipos;
    private final IMarcasServicio servicioMarcas;

    /**
     * Parsea el Excel y construye las filas de preview.
     * Las marcas NO se crean aqui; marcaId queda en 0 si no existe
     * y se crearan automaticamente en importarEquipos().
     */
    @Override
    public List<FilaImportDTO> parsearExcel(MultipartFile archivo, int categoriaIdDefecto) throws Exception {
        List<FilaImportDTO> filas = new ArrayList<>();
        Map<String, Integer> marcasMap = buildMarcasMap();

        try (InputStream is = archivo.getInputStream();
             Workbook wb = new XSSFWorkbook(is)) {

            DataFormatter formatter = new DataFormatter();

            for (int s = 0; s < wb.getNumberOfSheets(); s++) {
                Sheet sheet = wb.getSheetAt(s);

                // La fila 0 se reserva para cabecera (si existe) y se omite
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String sucursal = getCellValue(formatter, row, 0);
                    String cantStr  = getCellValue(formatter, row, 1);
                    String desc     = getCellValue(formatter, row, 2);
                    String codId    = getCellValue(formatter, row, 3);
                    String marca    = getCellValue(formatter, row, 4);
                    String modelo   = getCellValue(formatter, row, 5);
                    String serie    = getCellValue(formatter, row, 6);
                    String estado   = getCellValue(formatter, row, 7);
                    String estacion = getCellValue(formatter, row, 8);
                    String etqStr   = getCellValue(formatter, row, 9);
                    String obs      = getCellValue(formatter, row, 10);

                    // Omitir filas completamente vacias
                    if (desc.isBlank() && modelo.isBlank() && serie.isBlank()) continue;

                    int cant = 1;
                    try {
                        cant = (int) Double.parseDouble(cantStr.replace(",", "."));
                    } catch (NumberFormatException ignored) {}
                    if (cant < 1) cant = 1;

                    // Resolver marca existente (marcaId=0 indica que se creara al confirmar)
                    String marcaKey = marca.trim().toLowerCase();
                    int marcaId = marcasMap.getOrDefault(marcaKey, 0);

                    boolean esEtiqueta = etqStr.trim().equalsIgnoreCase("SI")
                            || etqStr.trim().equalsIgnoreCase("S\u00CD");

                    FilaImportDTO fila = new FilaImportDTO();
                    fila.setSucursal(sucursal);
                    fila.setCant(cant);
                    fila.setDescripcion(desc);
                    fila.setCodIdentificacion(codId);
                    fila.setMarca(marca.isBlank() ? "" : marca.trim().toUpperCase());
                    fila.setMarcaId(marcaId);
                    fila.setModelo(modelo);
                    fila.setSerie(serie);
                    fila.setEstadoEquipo(estado);
                    fila.setEstacion(estacion);
                    fila.setEtiqueta(esEtiqueta);
                    fila.setObservacion(obs);
                    fila.setCategoriaId(categoriaIdDefecto);

                    filas.add(fila);
                }
            }
        }

        return filas;
    }

    /**
     * Crea los equipos en la API.  Cada fila se expande segun su campo "cant".
     * Las marcas con marcaId=0 se crean aqui antes de procesar sus equipos.
     */
    @Override
    public int importarEquipos(List<FilaImportDTO> filas) {
        int count = 0;
        Map<String, Integer> marcasMap = buildMarcasMap();

        for (FilaImportDTO fila : filas) {

            // Si la marca aun no existe, crearla ahora
            if (fila.getMarcaId() == 0 && !fila.getMarca().isBlank()) {
                String key = fila.getMarca().trim().toLowerCase();
                if (!marcasMap.containsKey(key)) {
                    MarcasRequestDTO nueva = new MarcasRequestDTO();
                    nueva.setNombre(fila.getMarca().trim().toUpperCase());
                    nueva.setEstado(true);
                    try {
                        servicioMarcas.nuevaMarca(nueva);
                        marcasMap = buildMarcasMap();
                    } catch (Exception e) {
                        logger.warn("No se pudo crear la marca '{}': {}", fila.getMarca(), e.getMessage());
                        marcasMap = buildMarcasMap();
                    }
                }
                fila.setMarcaId(marcasMap.getOrDefault(key, 0));
            }

            for (int i = 0; i < fila.getCant(); i++) {
                try {
                    EquiposRequestDTO dto = buildEquipoDto(fila, i, fila.getCant());
                    servicioEquipos.crearEquipo(dto);
                    count++;
                } catch (Exception e) {
                    logger.error("Error importando equipo (fila '{}', unidad {}): {}", fila.getDescripcion(), (i + 1), e.getMessage());
                }
            }
        }
        return count;
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private EquiposRequestDTO buildEquipoDto(FilaImportDTO fila, int index, int total) {
        EquiposRequestDTO dto = new EquiposRequestDTO();

        dto.setModelo(fila.getDescripcion() != null ? fila.getDescripcion() : fila.getModelo());
        dto.setEstadoEquipo(fila.getEstadoEquipo());
        dto.setEstado(true);

        // Serial: si hay mas de 1 unidad, agregar sufijo para evitar duplicados
        String serial = fila.getSerie() != null ? fila.getSerie() : "";
        if (total > 1 && !serial.isBlank()) {
            serial = serial + String.format("-%02d", index + 1);
        }
        dto.setSerial(serial.isBlank() ? null : serial);

        // Codigo SAP: mismo tratamiento que el serial
        String codSap = fila.getCodIdentificacion() != null ? fila.getCodIdentificacion() : "";
        if (total > 1 && !codSap.isBlank()) {
            codSap = codSap + String.format("-%02d", index + 1);
        }
        dto.setCodigoSap(codSap.isBlank() ? null : codSap);

        // Observacion: incluir sucursal y nombre de estacion
        StringBuilder obsBuilder = new StringBuilder();
        if (fila.getSucursal() != null && !fila.getSucursal().isBlank()) {
            obsBuilder.append("[").append(fila.getSucursal()).append("] ");
        }
        if (fila.getEstacion() != null && !fila.getEstacion().isBlank()) {
            obsBuilder.append("Estacion: ").append(fila.getEstacion()).append(". ");
        }
        if (fila.getObservacion() != null) {
            obsBuilder.append(fila.getObservacion());
        }
        dto.setObservacionEquipo(obsBuilder.toString().trim());

        // FK Marca
        MarcasRequestDTO marca = new MarcasRequestDTO();
        marca.setIdMarca(fila.getMarcaId());
        dto.setFkMarca(marca);

        // FK Categoria
        CategoriaEquiposRequestDTO cat = new CategoriaEquiposRequestDTO();
        cat.setIdCategoria(fila.getCategoriaId());
        dto.setFkCategoria(cat);

        return dto;
    }

    private Map<String, Integer> buildMarcasMap() {
        Map<String, Integer> map = new HashMap<>();
        for (MarcasResponseDTO m : servicioMarcas.listarMarca()) {
            map.put(m.getNombre().trim().toLowerCase(), m.getIdMarca());
        }
        return map;
    }

    private String getCellValue(DataFormatter fmt, Row row, int col) {
        Cell cell = row.getCell(col, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
        return fmt.formatCellValue(cell).trim();
    }
}
