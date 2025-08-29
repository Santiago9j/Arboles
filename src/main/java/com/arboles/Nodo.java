package com.arboles;

/**
 * La clase Nodo representa un nodo en una estructura de datos enlazada.
 * Cada nodo puede contener datos personales, un enlace a otro nodo y un enlace a una lista.
 */
public class Nodo {

    private int sw; // Indicador de estado del nodo
    private Nodo ligaLista; // Enlace a una lista
    private DatosPersonales datosPersonales; // Datos personales asociados al nodo
    private Nodo liga; // Enlace a otro nodo

    /**
     * Constructor de la clase Nodo.
     * 
     * @param datosPersonales Los datos personales asociados al nodo.
     */
    public Nodo(DatosPersonales datosPersonales) {
        this.sw = 0;
        this.datosPersonales = datosPersonales;
        this.liga = null;
        this.ligaLista = null;
    }

    /**
     * Obtiene el indicador de estado del nodo.
     * 
     * @return El valor del indicador de estado.
     */
    public int getSw() {
        return sw;
    }

    /**
     * Establece el indicador de estado del nodo.
     * 
     * @param sw El nuevo valor del indicador de estado.
     */
    public void setSw(int sw) {
        this.sw = sw;
    }

    /**
     * Obtiene el enlace a la lista asociada al nodo.
     * 
     * @return El enlace a la lista.
     */
    public Nodo getLigaLista() {
        return ligaLista;
    }

    /**
     * Establece el enlace a la lista asociada al nodo.
     * 
     * @param ligaLista El nuevo enlace a la lista.
     */
    public void setLigaLista(Nodo ligaLista) {
        this.ligaLista = ligaLista;
    }

    /**
     * Obtiene los datos personales asociados al nodo.
     * 
     * @return Los datos personales.
     */
    public DatosPersonales getDatosPersonales() {
        return datosPersonales;
    }

    /**
     * Establece los datos personales asociados al nodo.
     * 
     * @param datosPersonales Los nuevos datos personales.
     */
    public void setDatosPersonales(DatosPersonales datosPersonales) {
        this.datosPersonales = datosPersonales;
    }

    /**
     * Obtiene el enlace a otro nodo.
     * 
     * @return El enlace a otro nodo.
     */
    public Nodo getLiga() {
        return liga;
    }

    /**
     * Establece el enlace a otro nodo.
     * 
     * @param liga El nuevo enlace a otro nodo.
     */
    public void setLiga(Nodo liga) {
        this.liga = liga;
    }

}
