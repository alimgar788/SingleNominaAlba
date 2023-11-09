package com.nomina.singlenominaalba.model;

import lombok.Getter;
import lombok.Setter;

/**
 * La clase Persona representa a una persona con un nombre, Dni y sexo
 */

@Setter
@Getter
public class Persona {
    public String nombre, dni;
    public Character sexo;

    /**
     * Constructor con todos los parámetros
     * @param nombre
     * @param dni
     * @param sexo
     */
    public Persona(String nombre, String dni, Character sexo){
        this.nombre = nombre;
        this.dni = dni;
        this.sexo = sexo;
    }
    /**
     * Constructor con dos los parámetros
     * @param nombre
     * @param sexo
     */
    public Persona(String nombre, Character sexo) {
        this.nombre = nombre;
        this.sexo = sexo;
    }

    /**
     * Método que establecer el dni de la persona
     * @param dni
     */
    public void setDni(String dni){
        this.dni = dni;
    }

    /**
     * Método que imprime el nombre y el dni de la persona
     */
    public void imprime(){
        System.out.println("Nombre: " + nombre + ", Dni: " + dni + ".");
    }
}
