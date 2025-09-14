
package com.ap4;

/**
 * Clase que implementa un árbol genealógico con operaciones de gestión y consulta.
 * Permite registrar, eliminar, modificar y consultar relaciones familiares.
 */
public class ArbolGenealogico {
    private Nodo raiz;


    /**
     * Devuelve la raíz del árbol genealógico.
     * @return Nodo raíz, o null si el árbol está vacío.
     */
    public Nodo getRaiz() {
        return raiz;
    }

    /**
     * Inserta una nueva persona en el árbol, manteniendo a los hijos ordenados por cédula.
     * @param persona Datos de la persona a registrar
     * @param cedulaPadre Cédula del padre (-1 si es raíz)
     * @return 0 si es la raíz, 1 si inserta correctamente, 2 si el padre no existe, 3 si la cédula ya existe
     */
    public int registrar(DatosPersonales persona, int cedulaPadre) {
        // Árbol vacío: el primer insert siempre es la raíz
        if (raiz == null) {
            raiz = new Nodo(persona);
            return 0;
        }

        // 3) La cédula ya existe en el árbol
        if (existeCedula(raiz, persona.getCedula())) {
            return 3;
        }

        // 2) No existe el padre
        Nodo padre = buscarPorCedula(raiz, cedulaPadre);
        if (padre == null) {
            return 2;
        }

        // Insertar en la lista de hijos del padre ORDENADO por cédula
        Nodo nuevo = new Nodo(persona);
        padre.ligaLista = insertarOrdenadoPorCedula(padre.ligaLista, nuevo);
        return 1;
    }


    private Nodo buscarPorCedula(Nodo actual, int cedula) {
        if (actual == null)
            return null;
        if (actual.datos != null && actual.datos.getCedula() == cedula)
            return actual;

        // Buscar en hijos
        Nodo enHijos = buscarPorCedula(actual.ligaLista, cedula);
        if (enHijos != null)
            return enHijos;

        // Buscar en hermanos
        return buscarPorCedula(actual.liga, cedula);
    }

    // Verifica existencia de cédula
    private boolean existeCedula(Nodo actual, int cedula) {
        return buscarPorCedula(actual, cedula) != null;
    }

    private Nodo insertarOrdenadoPorCedula(Nodo cabeza, Nodo nuevo) {
        if (cabeza == null || nuevo.datos.getCedula() < cabeza.datos.getCedula()) {
            nuevo.liga = cabeza;
            return nuevo;
        }
        Nodo ant = cabeza;
        Nodo act = cabeza.liga;
        while (act != null && act.datos.getCedula() < nuevo.datos.getCedula()) {
            ant = act;
            act = act.liga;
        }
        nuevo.liga = act;
        ant.liga = nuevo;
        return cabeza;
    }


    /**
     * Imprime el árbol genealógico en consola en formato jerárquico.
     */
    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void imprimirRec(Nodo n, int nivel) {
        if (n == null)
            return;
        String tab = "  ".repeat(nivel);
        System.out.println(tab + "- " + n.datos);
        imprimirRec(n.ligaLista, nivel + 1);
        imprimirRec(n.liga, nivel);
    }

    /*
     * Eliminar: Eliminar a una persona a partir de su cedula. Si el nodo a eliminar corresponde a un padre,
     * será reemplazdo por su hijo de mayor edad.
     */
    /**
     * Elimina una persona del árbol según su cédula. Si es padre, lo reemplaza su hijo mayor.
     * @param cedula Cédula de la persona a eliminar
     * @return 1 si elimina, 2 si no existe, 3 si el árbol está vacío
     */
    public int eliminar(int cedula) {
        if (raiz == null)
            return 3;

        Nodo objetivo = buscarPorCedula(raiz, cedula);
        if (objetivo == null)
            return 2;

        if (objetivo.ligaLista == null) {
            if (objetivo == raiz) { 
                raiz = null;
                return 1;
            }
            Nodo padre = buscarPadre(raiz, cedula);
            if (padre != null) {
                padre.ligaLista = quitarDeHijos(padre.ligaLista, cedula);
            }
            return 1;
        }

        Nodo padre = (objetivo == raiz) ? null : buscarPadre(raiz, cedula);

        Nodo prevMax = null, max = objetivo.ligaLista;
        Nodo prev = null, cur = objetivo.ligaLista;
        int edadMax = -1;
        while (cur != null) {
            int e = cur.datos.getEdad();
            if (e > edadMax) {
                edadMax = e;
                max = cur;
                prevMax = prev;
            }
            prev = cur;
            cur = cur.liga;
        }

        if (prevMax == null)
            objetivo.ligaLista = max.liga; 
        else
            prevMax.liga = max.liga;


        Nodo restoHijos = objetivo.ligaLista; 
        Nodo hijosDeMax = max.ligaLista;
        max.ligaLista = fusionarListasOrdenadasPorCedula(restoHijos, hijosDeMax);
        max.liga = null; 

        if (padre == null) {
            raiz = max; 
            return 1;
        } else {
            padre.ligaLista = quitarDeHijos(padre.ligaLista, cedula);
            padre.ligaLista = insertarOrdenadoPorCedula(padre.ligaLista, max);
            return 1;
        }
    }


    private Nodo buscarPadre(Nodo actual, int cedulaBuscada) {
        if (actual == null)
            return null;
        Nodo hijo = actual.ligaLista;
        while (hijo != null) {
            if (hijo.datos.getCedula() == cedulaBuscada)
                return actual;
            Nodo enSubarbol = buscarPadre(hijo, cedulaBuscada);
            if (enSubarbol != null)
                return enSubarbol;
            hijo = hijo.liga;
        }
        return null;
    }

    private Nodo quitarDeHijos(Nodo cabeza, int cedulaBuscada) {
        if (cabeza == null)
            return null;
        if (cabeza.datos.getCedula() == cedulaBuscada)
            return cabeza.liga;
        Nodo ant = cabeza, act = cabeza.liga;
        while (act != null && act.datos.getCedula() != cedulaBuscada) {
            ant = act;
            act = act.liga;
        }
        if (act != null)
            ant.liga = act.liga;
        return cabeza;
    }

    private Nodo fusionarListasOrdenadasPorCedula(Nodo a, Nodo b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        Nodo cabeza = null, cola = null;
        while (a != null && b != null) {
            Nodo elegido;
            if (a.datos.getCedula() <= b.datos.getCedula()) {
                elegido = a;
                a = a.liga;
            } else {
                elegido = b;
                b = b.liga;
            }
            elegido.liga = null;
            if (cabeza == null) {
                cabeza = elegido;
                cola = elegido;
            } else {
                cola.liga = elegido;
                cola = elegido;
            }
        }
        Nodo resto = (a != null) ? a : b;
        if (cola == null)
            return resto;
        cola.liga = resto;
        return cabeza;
    }

    /*
     * Actualizar: Modificar cualquiera de los campos (nombre, cédula o edad) de un registro existente.
     */

    /**
     * Modifica los datos de una persona existente.
     * @param cedulaActual Cédula actual
     * @param nuevoNombre Nuevo nombre (opcional)
     * @param nuevaCedula Nueva cédula (opcional)
     * @param nuevaEdad Nueva edad (opcional)
     * @return 1 si modifica, 2 si no existe, 3 si la nueva cédula ya existe
     */
    public int modificar(int cedulaActual, String nuevoNombre, Integer nuevaCedula, Integer nuevaEdad) {
        Nodo objetivo = buscarPorCedula(raiz, cedulaActual);
        if (objetivo == null)
            return 2;

        boolean cambiaCedula = (nuevaCedula != null && nuevaCedula != cedulaActual);
        if (cambiaCedula && existeCedula(raiz, nuevaCedula))
            return 3;

        if (cambiaCedula) {
            if (objetivo != raiz) {
                Nodo padre = buscarPadre(raiz, cedulaActual);
                if (padre != null) {
                    Nodo head = padre.ligaLista;
                    if (head != null) {
                        if (head.datos.getCedula() == cedulaActual) {
                            padre.ligaLista = head.liga;
                        } else {
                            Nodo ant = head, act = head.liga;
                            while (act != null && act.datos.getCedula() != cedulaActual) {
                                ant = act;
                                act = act.liga;
                            }
                            if (act != null)
                                ant.liga = act.liga;
                        }
                    }
                    objetivo.liga = null;
                    objetivo.datos.setCedula(nuevaCedula);
                    padre.ligaLista = insertarOrdenadoPorCedula(padre.ligaLista, objetivo);
                } else {
                    objetivo.datos.setCedula(nuevaCedula);
                }
            } else {
                objetivo.datos.setCedula(nuevaCedula);
            }
        }

        if (nuevoNombre != null)
            objetivo.datos.setNombre(nuevoNombre);
        if (nuevaEdad != null)
            objetivo.datos.setEdad(nuevaEdad);

        return 1;
    }

    /*
     * Padre: Mostrar el padre de un registro dado su número de cédula
    */

    /**
     * Obtiene los datos del padre de una persona.
     * @param cedulaHijo Cédula del hijo
     * @return DatosPersonales del padre, o null si no existe
     */
    public DatosPersonales obtenerPadre(int cedulaHijo) {
        if (raiz == null)
            return null;
        Nodo padre = buscarPadre(raiz, cedulaHijo); 
        return (padre != null) ? padre.datos : null;
    }

    /**
     * Muestra en consola el padre de una persona.
     * @param cedulaHijo Cédula del hijo
     * @return 1 si lo muestra, 2 si no existe, 3 si árbol vacío, 4 si no tiene padre
     */
    public int mostrarPadre(int cedulaHijo) {
        if (raiz == null)
            return 3;

        Nodo hijo = buscarPorCedula(raiz, cedulaHijo);
        if (hijo == null)
            return 2;

        Nodo padre = buscarPadre(raiz, cedulaHijo);
        if (padre == null)
            return 4;

        System.out.println("Padre de " + cedulaHijo + ": " + padre.datos);
        return 1;
    }

    /*
     * Hijos: Mostrar los hijos de una persona, dada su cédula.
     */
    /**
     * Muestra en consola los hijos de una persona.
     * @param cedulaPadre Cédula del padre
     * @return 1 si los muestra, 2 si no existe, 3 si árbol vacío, 4 si no tiene hijos
     */
    public int mostrarHijos(int cedulaPadre) {
        if (raiz == null)
            return 3;

        Nodo padre = buscarPorCedula(raiz, cedulaPadre);
        if (padre == null)
            return 2;
        if (padre.ligaLista == null)
            return 4;

        System.out.println("Hijos de " + padre.datos.getNombre() + " (" + cedulaPadre + "):");
        Nodo h = padre.ligaLista; 
        while (h != null) { 
            System.out.println("  - " + h.datos);
            h = h.liga;
        }
        return 1;
    }

    /**
     * Devuelve un arreglo con los hijos de una persona.
     * @param cedulaPadre Cédula del padre
     * @return Arreglo de hijos, vacío si no tiene
     */
    public DatosPersonales[] obtenerHijos(int cedulaPadre) {
        if (raiz == null)
            return new DatosPersonales[0];

        Nodo padre = buscarPorCedula(raiz, cedulaPadre);
        if (padre == null || padre.ligaLista == null)
            return new DatosPersonales[0];

        int n = contarHijos(padre);
        DatosPersonales[] hijos = new DatosPersonales[n];

        Nodo h = padre.ligaLista;
        int i = 0;
        while (h != null) {
            hijos[i++] = h.datos;
            h = h.liga;
        }
        return hijos;
    }

    private int contarHijos(Nodo padre) {
        int c = 0;
        Nodo h = padre.ligaLista;
        while (h != null) {
            c++;
            h = h.liga;
        }
        return c;
    }

    /*
     * Hermanos: Mostrar los hermanos de una persona, dada su cédula.
     */

    /**
     * Muestra en consola los hermanos de una persona.
     * @param cedula Cédula de la persona
     * @return 1 si los muestra, 2 si no existe, 3 si árbol vacío, 4 si no tiene hermanos
     */
    public int mostrarHermanos(int cedula) {
        if (raiz == null)
            return 3;

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return 2;

        Nodo padre = buscarPadre(raiz, cedula);
        if (padre == null)
            return 4; 

        int cnt = 0;
        for (Nodo h = padre.ligaLista; h != null; h = h.liga) {
            if (h.datos.getCedula() != cedula)
                cnt++;
        }
        if (cnt == 0)
            return 4;

        System.out.println("Hermanos de " + persona.datos.getNombre() + " (" + cedula + "):");
        for (Nodo h = padre.ligaLista; h != null; h = h.liga) {
            if (h.datos.getCedula() != cedula) {
                System.out.println("  - " + h.datos);
            }
        }
        return 1;
    }

    /**
     * Devuelve un arreglo con los hermanos de una persona.
     * @param cedula Cédula de la persona
     * @return Arreglo de hermanos, vacío si no tiene
     */
    public DatosPersonales[] obtenerHermanos(int cedula) {
        if (raiz == null)
            return new DatosPersonales[0];

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return new DatosPersonales[0];

        Nodo padre = buscarPadre(raiz, cedula);
        if (padre == null)
            return new DatosPersonales[0];

        int n = 0;
        for (Nodo h = padre.ligaLista; h != null; h = h.liga) {
            if (h.datos.getCedula() != cedula)
                n++;
        }
        if (n == 0)
            return new DatosPersonales[0];

        DatosPersonales[] hermanos = new DatosPersonales[n];
        int i = 0;
        for (Nodo h = padre.ligaLista; h != null; h = h.liga) {
            if (h.datos.getCedula() != cedula)
                hermanos[i++] = h.datos;
        }
        return hermanos;
    }

    /*
     * Ancestros: Mostrar todos los ancestros de una persona, dada su cédula.
     */
    /**
     * Muestra en consola los ancestros de una persona.
     * @param cedula Cédula de la persona
     * @return 1 si los muestra, 2 si no existe, 3 si árbol vacío, 4 si no tiene ancestros
     */
    public int mostrarAncestros(int cedula) {
        if (raiz == null)
            return 3;

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return 2;

        DatosPersonales[] ancestros = obtenerAncestros(cedula);
        if (ancestros.length == 0)
            return 4;

        System.out.println("Ancestros de " + persona.datos.getNombre() + " (" + cedula + "):");
        for (int i = 0; i < ancestros.length; i++) {
            System.out.println("  - " + ancestros[i]);
        }
        return 1;
    }

    /**
     * Devuelve un arreglo con los ancestros de una persona.
     * @param cedula Cédula de la persona
     * @return Arreglo de ancestros, vacío si no tiene
     */
    public DatosPersonales[] obtenerAncestros(int cedula) {
        if (raiz == null)
            return new DatosPersonales[0];

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return new DatosPersonales[0];

        int n = contarAncestros(cedula);
        if (n == 0)
            return new DatosPersonales[0];

        DatosPersonales[] ancestros = new DatosPersonales[n];
        int i = 0;
        Nodo p = buscarPadre(raiz, cedula);
        while (p != null) {
            ancestros[i++] = p.datos;
            p = buscarPadre(raiz, p.datos.getCedula());
        }
        return ancestros;
    }

    private int contarAncestros(int cedula) {
        int c = 0;
        Nodo p = buscarPadre(raiz, cedula);
        while (p != null) {
            c++;
            p = buscarPadre(raiz, p.datos.getCedula());
        }
        return c;
    }

    /*
     * Descendientes: Mostrar todos los descendientes de una persona, dada su cédula.
     */
    /**
     * Muestra en consola los descendientes de una persona.
     * @param cedula Cédula de la persona
     * @return 1 si los muestra, 2 si no existe, 3 si árbol vacío, 4 si no tiene descendientes
     */
    public int mostrarDescendientes(int cedula) {
        if (raiz == null)
            return 3;

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return 2;

        if (persona.ligaLista == null)
            return 4;

        System.out.println("Descendientes de " + persona.datos.getNombre() + " (" + cedula + "):");
        imprimirDescendencia(persona, 1); 
        return 1;
    }

    private void imprimirDescendencia(Nodo nodo, int nivel) {
        for (Nodo h = nodo.ligaLista; h != null; h = h.liga) {
            String tab = "  ".repeat(nivel);
            System.out.println(tab + "- " + h.datos);
            imprimirDescendencia(h, nivel + 1);
        }
    }


    /**
     * Devuelve un arreglo con los descendientes de una persona.
     * @param cedula Cédula de la persona
     * @return Arreglo de descendientes, vacío si no tiene
     */
    public DatosPersonales[] obtenerDescendientes(int cedula) {
        if (raiz == null)
            return new DatosPersonales[0];

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null || persona.ligaLista == null)
            return new DatosPersonales[0];

        int n = contarDescendientesDesde(persona);
        DatosPersonales[] out = new DatosPersonales[n];
        llenarDescendientesDesde(persona, out, 0);
        return out;
    }

    private int contarDescendientesDesde(Nodo nodo) {
        int c = 0;
        for (Nodo h = nodo.ligaLista; h != null; h = h.liga) {
            c += 1; 
            c += contarDescendientesDesde(h);
        }
        return c;
    }

    private int llenarDescendientesDesde(Nodo nodo, DatosPersonales[] out, int idx) {
        for (Nodo h = nodo.ligaLista; h != null; h = h.liga) {
            out[idx++] = h.datos;
            idx = llenarDescendientesDesde(h, out, idx);
        }
        return idx;
    }

    /*
     * Nodo con Mayor Grado: Mostrar la información del nodo que tiene el mayor número de hijos.
     */
    /**
     * Muestra en consola el nodo con mayor número de hijos.
     * @return 1 si lo muestra, 3 si árbol vacío
     */
    public int mostrarNodoConMasHijos() {
        if (raiz == null)
            return 3;
        MaxRef ref = inicializarMax();
        dfsMaxHijos(raiz, ref);
        System.out.println("Nodo con más hijos (" + ref.hijos + "): " + ref.nodo.datos);
        return 1;
    }

    /**
     * Devuelve los datos del nodo con mayor número de hijos.
     * @return DatosPersonales del nodo, o null si árbol vacío
     */
    public DatosPersonales obtenerNodoConMasHijos() {
        if (raiz == null)
            return null;
        MaxRef ref = inicializarMax();
        dfsMaxHijos(raiz, ref);
        return ref.nodo.datos;
    }

    private static final class MaxRef {
        Nodo nodo;
        int hijos;
    }

    private MaxRef inicializarMax() {
        MaxRef r = new MaxRef();
        r.nodo = raiz;
        r.hijos = contarHijosNodo(raiz);
        return r;
    }


    private void dfsMaxHijos(Nodo actual, MaxRef ref) {
        if (actual == null)
            return;
        int cnt = contarHijosNodo(actual);
        if (cnt > ref.hijos) {
            ref.hijos = cnt;
            ref.nodo = actual;
        }
        dfsMaxHijos(actual.ligaLista, ref);
        dfsMaxHijos(actual.liga, ref);
    }

    private int contarHijosNodo(Nodo n) {
        int c = 0;
        for (Nodo h = n.ligaLista; h != null; h = h.liga)
            c++;
        return c;
    }
    
    /*
     * Nodo con Mayor Nivel: Mostrar la información del nodo que se encuentra más profundo en el árbol.
     */
    /**
     * Muestra en consola el nodo más profundo del árbol.
     * @return 1 si lo muestra, 3 si árbol vacío
     */
    public int mostrarNodoMasProfundo() {
        if (raiz == null)
            return 3;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        System.out.println("Nodo más profundo (nivel " + ref.depth + "): " + ref.nodo.datos);
        return 1;
    }

    /**
     * Devuelve los datos del nodo más profundo del árbol.
     * @return DatosPersonales del nodo, o null si árbol vacío
     */
    public DatosPersonales obtenerNodoMasProfundo() {
        if (raiz == null)
            return null;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        return ref.nodo.datos;
    }

    /**
     * Devuelve el nivel del nodo más profundo del árbol.
     * @return Nivel (entero), o -1 si árbol vacío
     */
    public int nivelNodoMasProfundo() {
        if (raiz == null)
            return -1;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        return ref.depth;
    }

    private static final class DeepRef {
        Nodo nodo;
        int depth;

        DeepRef(Nodo n, int d) {
            this.nodo = n;
            this.depth = d;
        }
    }

    private void dfsMasProfundo(Nodo actual, int depth, DeepRef ref) {
        if (actual == null)
            return;
        if (depth > ref.depth) {
            ref.depth = depth;
            ref.nodo = actual;
        }
        dfsMasProfundo(actual.ligaLista, depth + 1, ref); 
        dfsMasProfundo(actual.liga, depth, ref);
    }

    /*
     * Altura del Arbol: Calcular y mmostrar la altura total del árbol.
     */

    /**
     * Calcula la altura del árbol (número de aristas desde la raíz al nodo más profundo).
     * @return Altura del árbol, -1 si vacío
     */
    public int alturaArbol() {
        return alturaDesde(raiz);
    }

    private int alturaDesde(Nodo n) {
        if (n == null)
            return -1; 
        int maxHijo = -1;
        for (Nodo h = n.ligaLista; h != null; h = h.liga) {
            int altHijo = alturaDesde(h);
            if (altHijo > maxHijo)
                maxHijo = altHijo;
        }
        return maxHijo + 1; 
    }


    /**
     * Devuelve el número de niveles del árbol (altura + 1).
     * @return Niveles del árbol
     */
    public int nivelesArbol() {
        int alt = alturaArbol();
        return (alt < 0) ? 0 : alt + 1;
    }


    /**
     * Muestra en consola la altura y los niveles del árbol.
     * @return 1 si lo muestra, 3 si árbol vacío
     */
    public int mostrarAlturaArbol() {
        if (raiz == null) {
            System.out.println("Árbol vacío. Altura = -1, Niveles = 0.");
            return 3;
        }
        int alt = alturaArbol();
        System.out.println("Altura (aristas): " + alt + " | Niveles: " + (alt + 1));
        return 1;
    }

    /*
     * Nivel de un Registro: Determinar y mostrar el nivel al que pertenece una persona especifica, dada su cédula.
     */
    /**
     * Muestra en consola el nivel de una persona según su cédula.
     * @param cedula Cédula de la persona
     * @return 1 si lo muestra, 2 si no existe, 3 si árbol vacío
     */
    public int mostrarNivelPersona(int cedula) {
        if (raiz == null)
            return 3;
        int nivel = nivelPersona(cedula);
        if (nivel < 0)
            return 2;
        System.out.println("Nivel de la persona con cédula " + cedula + " = " + nivel);
        return 1;
    }



    /**
     * Devuelve el nivel de una persona según su cédula.
     * @param cedula Cédula de la persona
     * @return Nivel (entero), o -1 si no existe
     */
    public int nivelPersona(int cedula) {
        return nivelDesde(raiz, cedula, 0);
    }


    private int nivelDesde(Nodo actual, int cedula, int nivelActual) {
        if (actual == null)
            return -1;

        if (actual.datos != null && actual.datos.getCedula() == cedula) {
            return nivelActual;
        }
        int enHijos = nivelDesde(actual.ligaLista, cedula, nivelActual + 1);
        if (enHijos >= 0)
            return enHijos;

        return nivelDesde(actual.liga, cedula, nivelActual);
    }

    /*
     * Registros por Nivel: Mostrar todos los registros ubicados en un nivel particular del arbol
     */
    /**
     * Muestra en consola los registros ubicados en un nivel específico del árbol.
     * @param nivelObjetivo Nivel a consultar
     * @return 1 si los muestra, 3 si árbol vacío, 4 si nivel inválido o vacío
     */
    public int mostrarRegistrosEnNivel(int nivelObjetivo) {
        if (raiz == null)
            return 3;
        if (nivelObjetivo < 0)
            return 4;

        int cant = contarEnNivelDesde(raiz, nivelObjetivo, 0);
        if (cant == 0)
            return 4;

        System.out.println("Registros en el nivel " + nivelObjetivo + ":");
        imprimirNivelDesde(raiz, nivelObjetivo, 0);
        return 1;
    }


    /**
     * Devuelve un arreglo con los registros de un nivel específico.
     * @param nivelObjetivo Nivel a consultar
     * @return Arreglo de registros, vacío si no hay
     */
    public DatosPersonales[] obtenerRegistrosEnNivel(int nivelObjetivo) {
        if (raiz == null || nivelObjetivo < 0)
            return new DatosPersonales[0];

        int n = contarEnNivelDesde(raiz, nivelObjetivo, 0);
        if (n == 0)
            return new DatosPersonales[0];

        DatosPersonales[] out = new DatosPersonales[n];
        llenarNivelDesde(raiz, nivelObjetivo, 0, out, 0);
        return out;
    }

    private int contarEnNivelDesde(Nodo actual, int target, int nivelActual) {
        if (actual == null)
            return 0;

        int c = (nivelActual == target) ? 1 : 0;

        if (nivelActual < target) {
            c += contarEnNivelDesde(actual.ligaLista, target, nivelActual + 1);
        }
        c += contarEnNivelDesde(actual.liga, target, nivelActual);

        return c;
    }

    private void imprimirNivelDesde(Nodo actual, int target, int nivelActual) {
        if (actual == null)
            return;

        if (nivelActual == target) {
            System.out.println("  - " + actual.datos);
            imprimirNivelDesde(actual.liga, target, nivelActual);
            return;
        }

        if (nivelActual < target) {
            imprimirNivelDesde(actual.ligaLista, target, nivelActual + 1);
        }
        imprimirNivelDesde(actual.liga, target, nivelActual);
    }

    private int llenarNivelDesde(Nodo actual, int target, int nivelActual,
            DatosPersonales[] out, int idx) {
        if (actual == null)
            return idx;

        if (nivelActual == target) {
            out[idx++] = actual.datos;
            return llenarNivelDesde(actual.liga, target, nivelActual, out, idx);
        }

        if (nivelActual < target) {
            idx = llenarNivelDesde(actual.ligaLista, target, nivelActual + 1, out, idx);
        }
        return llenarNivelDesde(actual.liga, target, nivelActual, out, idx);
    }

    /*
     * Eliminar Nivel: Eliminar todos los nodos que se encuentren en un nivel específico del árbol.
     */
    /**
     * Elimina todos los nodos que se encuentren en un nivel específico del árbol.
     * @param nivelObjetivo Nivel a eliminar
     * @return 1 si elimina, 3 si árbol vacío, 4 si nivel inválido o vacío
     */
    public int eliminarNivel(int nivelObjetivo) {
        if (raiz == null)
            return 3;
        if (nivelObjetivo < 0)
            return 4;

        int cuantos = contarEnNivelDesdeV2(raiz, nivelObjetivo, 0);
        if (cuantos == 0)
            return 4;

        if (nivelObjetivo == 0) {
            raiz = null;
            return 1;
        }

        eliminarNivelDesde(raiz, 0, nivelObjetivo);
        return 1;
    }

    private boolean eliminarNivelDesde(Nodo actual, int nivelActual, int target) {
        if (actual == null)
            return false;
        boolean elim = false;

        if (nivelActual == target - 1) {
            if (actual.ligaLista != null) {
                actual.ligaLista = null;
                elim = true;
            }
            return eliminarNivelDesde(actual.liga, nivelActual, target) || elim;
        }

        if (nivelActual < target - 1) {
            boolean eHijos = eliminarNivelDesde(actual.ligaLista, nivelActual + 1, target);
            boolean eHerm = eliminarNivelDesde(actual.liga, nivelActual, target);
            return eHijos || eHerm;
        }

        return eliminarNivelDesde(actual.liga, nivelActual, target) || elim;
    }

    private int contarEnNivelDesdeV2(Nodo actual, int target, int nivelActual) {
        if (actual == null)
            return 0;
        int c = (nivelActual == target) ? 1 : 0;
        if (nivelActual < target) {
            c += contarEnNivelDesdeV2(actual.ligaLista, target, nivelActual + 1);
        }
        c += contarEnNivelDesdeV2(actual.liga, target, nivelActual);
        return c;
    }
}
