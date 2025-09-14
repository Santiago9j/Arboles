package com.ap4;

public class Nodo {
    // Atributos de la clase
    int sw = 0;
    Nodo ligaLista;        
    DatosPersonales datos; 
    Nodo liga;

    // Constructor
    Nodo(DatosPersonales datos) {
        this.datos = datos;
    }

    // Getters y Setters
    public int getSw() {
        return sw;
    }

    public void setSw(int sw) {
        this.sw = sw;
    }

    public Nodo getLigaLista() {
        return ligaLista;
    }

    public void setLigaLista(Nodo ligaLista) {
        this.ligaLista = ligaLista;
    }

    public DatosPersonales getDatos() {
        return datos;
    }

    public void setDatos(DatosPersonales datos) {
        this.datos = datos;
    }

    public Nodo getLiga() {
        return liga;
    }

    public void setLiga(Nodo liga) {
        this.liga = liga;
    }

}
