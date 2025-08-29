package com.arboles;

public class Arbol {
    
    private Nodo cabeza;

    public Arbol() {
        this.cabeza = null;
    }

    // Lista de métodos para manipular el árbol que es una lista generalizada simple
    
    // Metodo para insertar un nodo en el árbol manteniendo el arbol ordenado por según su cédula. 
    public void insertarNodo(DatosPersonales datosPersonales) {
        Nodo nuevoNodo = new Nodo(datosPersonales);
        if (cabeza == null || cabeza.getDatosPersonales().getCedula() > datosPersonales.getCedula()) {
            nuevoNodo.setLiga(cabeza);
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.getLiga() != null && actual.getLiga().getDatosPersonales().getCedula() < datosPersonales.getCedula()) {
                actual = actual.getLiga();
            }
            nuevoNodo.setLiga(actual.getLiga());
            actual.setLiga(nuevoNodo);
        }
    }

    // Método para eliminar una persona a partir de su cédula. Si el nodo a eliminar corresponde a 
    // un padre, el nodo a eliminar será reemplazado por su hijo de mayor edad.
    public void eliminarNodo(int cedula) {
        if (cabeza == null) {
            return; // El árbol está vacío
        }

        if (cabeza.getDatosPersonales().getCedula() == cedula) {
            // El nodo a eliminar es la cabeza
            if (cabeza.getSw() == 1 && cabeza.getLigaLista() != null) {
                // Si la cabeza tiene hijos, reemplazarla por el hijo de mayor edad
                Nodo hijoMayorEdad = cabeza.getLigaLista();
                Nodo actual = cabeza.getLigaLista();
                while (actual != null) {
                    if (actual.getDatosPersonales().getEdad().compareTo(hijoMayorEdad.getDatosPersonales().getEdad()) > 0) {
                        hijoMayorEdad = actual;
                    }
                    actual = actual.getLigaLista();
                }
                hijoMayorEdad.setLiga(cabeza.getLiga());
                cabeza = hijoMayorEdad;
            } else {
                // Si la cabeza no tiene hijos, simplemente eliminarla
                cabeza = cabeza.getLiga();
            }
            return;
        }

        Nodo actual = cabeza;
        while (actual.getLiga() != null && actual.getLiga().getDatosPersonales().getCedula() != cedula) {
            actual = actual.getLiga();
        }

        if (actual.getLiga() != null) {
            Nodo nodoAEliminar = actual.getLiga();
            if (nodoAEliminar.getSw() == 1 && nodoAEliminar.getLigaLista() != null) {
                // Si el nodo a eliminar tiene hijos, reemplazarlo por el hijo de mayor edad
                Nodo hijoMayorEdad = nodoAEliminar.getLigaLista();
                Nodo temp = nodoAEliminar.getLigaLista();
                while (temp != null) {
                    if (temp.getDatosPersonales().getEdad().compareTo(hijoMayorEdad.getDatosPersonales().getEdad()) > 0) {
                        hijoMayorEdad = temp;
                    }
                    temp = temp.getLigaLista();
                }
                hijoMayorEdad.setLiga(nodoAEliminar.getLiga());
                actual.setLiga(hijoMayorEdad);
            } else {
                // Si el nodo a eliminar no tiene hijos, simplemente eliminarlo
                actual.setLiga(nodoAEliminar.getLiga());
            }
        }
    }





}
