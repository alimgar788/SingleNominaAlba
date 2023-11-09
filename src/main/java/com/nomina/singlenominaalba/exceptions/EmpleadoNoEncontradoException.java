package com.nomina.singlenominaalba.exceptions;
/**
 * La clase EmpleadoNoEncontradoException representa una excepción lanzada cuando no se encuentra un empleado.
 */
public class EmpleadoNoEncontradoException extends Throwable {
    /**
     * Construye una nueva excepción con el mensaje especificado.
     *
     * @param s El mensaje de la excepción.
     */
    public EmpleadoNoEncontradoException(String s) {
        super(s);
    }
}
