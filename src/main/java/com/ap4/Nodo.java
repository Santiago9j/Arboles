package com.ap4;

public class Nodo {
    int sw = 0;
    Nodo ligaLista;        // cabeza de hijos
    DatosPersonales datos; // persona
    Nodo liga;             // hermano siguiente

    Nodo(DatosPersonales datos) {
        this.datos = datos;
    }
}
