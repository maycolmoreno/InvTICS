package com.uisrael.gestionactivosapi.infraestructura.servicios;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.CustodioRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.EquipoRepositorioPuerto;
import com.uisrael.gestionactivosapi.dominio.puertos.repositorios.UsuarioRepositorioPuerto;
import com.uisrael.gestionactivosapi.presentacion.dto.response.MantenimientoManualResponseDTO;

@ExtendWith(MockitoExtension.class)
class MantenimientoInformeServiceTest {

    @Mock
    private EquipoRepositorioPuerto equiposRepo;
    @Mock
    private CustodioRepositorioPuerto custodiosRepo;
    @Mock
    private UsuarioRepositorioPuerto usuariosRepo;
    @Mock
    private PdfMantenimientoService pdfMantenimientoService;
    @Mock
    private MantenimientoArchivoService archivoService;
    @Mock
    private CorreoMantenimientoService correoService;

    private MantenimientoInformeService service;

    private MantenimientoManualResponseDTO mantenimientoSinRelaciones() {
        return MantenimientoManualResponseDTO.builder()
                .idMantenimiento(42)
                .imagenes(List.of())
                .build();
    }

    private void construirService() {
        service = new MantenimientoInformeService(equiposRepo, custodiosRepo, usuariosRepo,
                pdfMantenimientoService, archivoService, correoService);
    }

    @Test
    void siFallaLaGeneracionInvalidaElCacheYPropagaLaExcepcion() {
        construirService();
        MantenimientoManualResponseDTO mantenimiento = mantenimientoSinRelaciones();
        when(pdfMantenimientoService.generarInforme(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("fallo generando PDF"));

        assertThatThrownBy(() -> service.generarGuardarYEnviar(mantenimiento))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("fallo generando PDF");

        verify(archivoService).eliminarPdfSiExiste(42);
        verify(archivoService, never()).guardarPdf(any(), any());
        verify(correoService, never()).enviarInformeMantenimientoConPdf(
                any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void siLaGeneracionFallaAlSoloVerElPdfTambienInvalidaElCache() {
        construirService();
        MantenimientoManualResponseDTO mantenimiento = mantenimientoSinRelaciones();
        when(archivoService.existePdf(42)).thenReturn(false);
        when(pdfMantenimientoService.generarInforme(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("fallo generando PDF"));

        assertThatThrownBy(() -> service.obtenerOGenerarPdf(mantenimiento))
                .isInstanceOf(RuntimeException.class);

        verify(archivoService).eliminarPdfSiExiste(42);
    }

    @Test
    void siLaGeneracionTieneExitoGuardaElNuevoPdfYNoInvalidaNada() {
        construirService();
        MantenimientoManualResponseDTO mantenimiento = mantenimientoSinRelaciones();
        byte[] pdfBytes = {1, 2, 3};
        when(pdfMantenimientoService.generarInforme(any(), any(), any(), any(), any()))
                .thenReturn(pdfBytes);
        when(archivoService.guardarPdf(42, pdfBytes)).thenReturn(Path.of("mantenimiento_42.pdf"));

        service.generarGuardarYEnviar(mantenimiento);

        verify(archivoService).guardarPdf(42, pdfBytes);
        verify(archivoService, never()).eliminarPdfSiExiste(any());
    }

    @Test
    void obtenerOGenerarPdfUsaElCacheExistenteSinRegenerar() {
        construirService();
        MantenimientoManualResponseDTO mantenimiento = mantenimientoSinRelaciones();
        byte[] cacheado = {9, 9, 9};
        when(archivoService.existePdf(42)).thenReturn(true);
        when(archivoService.leerPdf(42)).thenReturn(cacheado);

        byte[] resultado = service.obtenerOGenerarPdf(mantenimiento);

        assertThat(resultado).isEqualTo(cacheado);
        verify(pdfMantenimientoService, never()).generarInforme(any(), any(), any(), any(), any());
    }
}
