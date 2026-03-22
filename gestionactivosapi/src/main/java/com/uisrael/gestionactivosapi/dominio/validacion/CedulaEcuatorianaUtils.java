package com.uisrael.gestionactivosapi.dominio.validacion;

public final class CedulaEcuatorianaUtils {

    private CedulaEcuatorianaUtils() {
    }

    public static boolean esValida(String cedula) {
        if (cedula == null) {
            return false;
        }

        String valor = cedula.trim();
        if (!valor.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(valor.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(valor.charAt(2));
        if (tercerDigito < 0 || tercerDigito > 5) {
            return false;
        }

        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(valor.charAt(i));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
        }

        int digitoVerificadorEsperado = (10 - (suma % 10)) % 10;
        int digitoVerificadorActual = Character.getNumericValue(valor.charAt(9));
        return digitoVerificadorEsperado == digitoVerificadorActual;
    }
}
