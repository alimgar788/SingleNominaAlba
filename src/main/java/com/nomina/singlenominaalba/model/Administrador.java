package com.nomina.singlenominaalba.model;

import lombok.Getter;
import lombok.Setter;

/**
 * La clase Administrador representa a un administrador del sistema.
 */
@Setter
@Getter
public class Administrador {

    private String dni;
    private String email;
    private String contrasenya;

    /**
     * Constructor de la clase Administrador.
     *
     * @param dni         El DNI del administrador.
     * @param email       El email del administrador.
     * @param contrasenya La contraseña del administrador.
     */
    public Administrador(String dni, String email, String contrasenya) {
        this.dni = dni;
        this.email = email;
        this.contrasenya = contrasenya;
    }
}
