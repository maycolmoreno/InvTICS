package com.uisrael.gestionactivosapi.dominio.validacion;

import java.text.Normalizer;
import java.util.Locale;

public final class TextNormalizer {

	private TextNormalizer() {
	}

	public static String normalizeForComparison(String value) {
		if (value == null) {
			return "";
		}
		return Normalizer.normalize(value, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.toLowerCase(Locale.ROOT)
				.trim();
	}
}