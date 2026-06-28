package com.uisrael.consumogestionactivosapi.modelo.dto.response.operacional;

public class CustodioTopDTO {
    private String nombre;
    private String ubicacion;
    private long cantidad;
    private String iniciales;

    public CustodioTopDTO(String nombre, String ubicacion, long cantidad) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.cantidad = cantidad;
        this.iniciales = iniciales(nombre);
    }

    private static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "??";
        String[] parts = nombre.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public String getNombre() { return nombre; }
    public String getUbicacion() { return ubicacion; }
    public long getCantidad() { return cantidad; }
    public String getIniciales() { return iniciales; }
}
