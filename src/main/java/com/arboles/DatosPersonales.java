package com.arboles;

/**
 * La clase DatosPersonales representa los datos personales de una persona,
 * incluyendo su cédula, nombre y edad.
 */
public class DatosPersonales {
    
    private int cedula;
    private String nombre;
    private String edad;

    /**
     * Constructor de la clase DatosPersonales.
     * 
     * @param cedula Número de cédula de la persona.
     * @param nombre Nombre de la persona.
     * @param edad Edad de la persona.
     */
    public DatosPersonales(int cedula, String nombre, String edad) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.edad = edad;
    }

    /**
     * Obtiene el número de cédula de la persona.
     * 
     * @return Número de cédula.
     */
    public int getCedula() {
        return cedula;
    }

    /**
     * Establece el número de cédula de la persona.
     * 
     * @param cedula Número de cédula.
     */
    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    /**
     * Obtiene el nombre de la persona.
     * 
     * @return Nombre de la persona.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la persona.
     * 
     * @param nombre Nombre de la persona.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la edad de la persona.
     * 
     * @return Edad de la persona.
     */
    public String getEdad() {
        return edad;
    }

    /**
     * Establece la edad de la persona.
     * 
     * @param edad Edad de la persona.
     */
    public void setEdad(String edad) {
        this.edad = edad;
    }

    /**
     * Devuelve una representación en forma de cadena de los datos personales.
     * 
     * @return Una cadena con los datos personales.
     */
    @Override
    public String toString() {
        return "DatosPersonales [cedula=" + cedula + ", nombre=" + nombre + ", edad=" + edad + "]";
    }

}
