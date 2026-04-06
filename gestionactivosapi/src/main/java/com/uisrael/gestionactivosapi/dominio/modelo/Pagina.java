package com.uisrael.gestionactivosapi.dominio.modelo;

import java.util.List;

/**
 * Representación de una página de resultados en el dominio.
 * Sin dependencia de Spring Data.
 */
public record Pagina<T>(
		List<T> contenido,
		int paginaActual,
		int tamanioPagina,
		long totalElementos,
		int totalPaginas) {
}
