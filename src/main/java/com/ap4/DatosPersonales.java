package com.ap4;

public class DatosPersonales {
    // Atributos de la clase
    private String nombre;
    private int cedula;
    private int edad;

    // Constructor
    public DatosPersonales(String nombre, int cedula, int edad) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.edad = edad;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public int getCedula() {
        return cedula;
    }

    public int getEdad() {
        return edad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "DatosPersonales{nombre='" + nombre + "', cedula=" + cedula + ", edad=" + edad + "}";
    }

}
