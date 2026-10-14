package com.api.alquilaya.exceptions;

// Se lanza cuando una operacion rompe una regla de negocio (fechas invalidas, solapamientos, duplicados, etc.)
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
