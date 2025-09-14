package com.ap4;

public class ArbolGenealogico {
    private Nodo raiz;

    public Nodo getRaiz() {
        return raiz;
    }

    /**
     * Registrar: Insertar una nueva persona, manteniendo a los hijos
     * de cada padre ordenados por "cedula".
     *
     * Reglas de retorno:
     * - Si la raíz es nula, el nuevo nodo se vuelve raíz y retorna 0.
     * - Si la cédula del padre no existe, retorna 2 (no inserta).
     * - Si la cédula a insertar ya existe, retorna 3 (no inserta).
     * - Si inserta correctamente, retorna 1.
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

    // --------- Utilidades privadas ---------

    // Busca un nodo por cédula (DFS sobre hijo y hermano)
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

    // Inserta un nodo en la lista (simplemente enlazada) de hijos ordenada por
    // cédula
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
        // (Ya validamos duplicados antes con existeCedula)
        nuevo.liga = act;
        ant.liga = nuevo;
        return cabeza;
    }

    // --------- (Opcional) impresión simple para depurar ---------
    public void imprimir() {
        imprimirRec(raiz, 0);
    }

    private void imprimirRec(Nodo n, int nivel) {
        if (n == null)
            return;
        String tab = "  ".repeat(nivel);
        System.out.println(tab + "- " + n.datos);
        // hijos
        imprimirRec(n.ligaLista, nivel + 1);
        // hermanos
        imprimirRec(n.liga, nivel);
    }

    public int eliminar(int cedula) {
        if (raiz == null)
            return 3;

        Nodo objetivo = buscarPorCedula(raiz, cedula);
        if (objetivo == null)
            return 2;

        // Caso 1: hoja (sin hijos)
        if (objetivo.ligaLista == null) {
            if (objetivo == raiz) { // raíz hoja
                raiz = null;
                return 1;
            }
            Nodo padre = buscarPadre(raiz, cedula);
            if (padre != null) {
                padre.ligaLista = quitarDeHijos(padre.ligaLista, cedula);
            }
            return 1;
        }

        // Caso 2: padre (con hijos) -> reemplazar por su hijo de mayor edad
        Nodo padre = (objetivo == raiz) ? null : buscarPadre(raiz, cedula);

        // Encontrar hijo de mayor edad y su previo
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

        // Quitar max de la lista de hijos del objetivo
        if (prevMax == null)
            objetivo.ligaLista = max.liga; // max era cabeza
        else
            prevMax.liga = max.liga;

        // Fusionar (ordenado por cédula) los "otros hijos" del objetivo con los hijos
        // de max
        Nodo restoHijos = objetivo.ligaLista; // ya sin max
        Nodo hijosDeMax = max.ligaLista;
        max.ligaLista = fusionarListasOrdenadasPorCedula(restoHijos, hijosDeMax);
        max.liga = null; // lo reinsertaremos en la lista de hijos del padre

        if (padre == null) {
            // objetivo era la raíz
            raiz = max; // max pasa a ser la nueva raíz
            return 1;
        } else {
            // quitar objetivo de la lista de hijos del padre e insertar max manteniendo
            // orden
            padre.ligaLista = quitarDeHijos(padre.ligaLista, cedula);
            padre.ligaLista = insertarOrdenadoPorCedula(padre.ligaLista, max);
            return 1;
        }
    }

    /*----------------- Helpers privados -----------------*/

    // Devuelve el padre del nodo con esa cédula (o null si es la raíz o no existe)
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

    // Quita de una lista simple (hijos de un padre) el nodo por cédula y retorna la
    // nueva cabeza
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

    // Fusión en una sola lista ordenada por cédula (reusa nodos, no crea nuevos)
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

    public int modificar(int cedulaActual, String nuevoNombre, Integer nuevaCedula, Integer nuevaEdad) {
        // 1) buscar objetivo
        Nodo objetivo = buscarPorCedula(raiz, cedulaActual);
        if (objetivo == null)
            return 2;

        // 2) validar nueva cédula (si la piden y realmente cambia)
        boolean cambiaCedula = (nuevaCedula != null && nuevaCedula != cedulaActual);
        if (cambiaCedula && existeCedula(raiz, nuevaCedula))
            return 3;

        // 3) si cambia cédula, reubicar manteniendo orden
        if (cambiaCedula) {
            if (objetivo != raiz) {
                // quitar de lista de hijos del padre
                Nodo padre = buscarPadre(raiz, cedulaActual);
                // (si no hay padre, era raíz; se maneja aparte)
                if (padre != null) {
                    Nodo head = padre.ligaLista;
                    if (head != null) {
                        if (head.datos.getCedula() == cedulaActual) {
                            padre.ligaLista = head.liga; // era cabeza
                        } else {
                            Nodo ant = head, act = head.liga;
                            while (act != null && act.datos.getCedula() != cedulaActual) {
                                ant = act;
                                act = act.liga;
                            }
                            if (act != null)
                                ant.liga = act.liga; // desvincular
                        }
                    }
                    objetivo.liga = null; // importante: lo vamos a reinsertar
                    objetivo.datos.setCedula(nuevaCedula);
                    // reinsertar ordenado por cédula
                    padre.ligaLista = insertarOrdenadoPorCedula(padre.ligaLista, objetivo);
                } else {
                    // era raíz: sólo cambiar valor
                    objetivo.datos.setCedula(nuevaCedula);
                }
            } else {
                // raíz: sólo cambiar valor
                objetivo.datos.setCedula(nuevaCedula);
            }
        }

        // 4) aplicar cambios de nombre/edad (no afectan el orden)
        if (nuevoNombre != null)
            objetivo.datos.setNombre(nuevoNombre);
        if (nuevaEdad != null)
            objetivo.datos.setEdad(nuevaEdad);

        return 1;
    }

    public DatosPersonales obtenerPadre(int cedulaHijo) {
        if (raiz == null)
            return null;
        Nodo padre = buscarPadre(raiz, cedulaHijo); // ya implementado en tu clase
        return (padre != null) ? padre.datos : null; // null si no existe o si es raíz
    }

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

    public int mostrarHijos(int cedulaPadre) {
        if (raiz == null)
            return 3;

        Nodo padre = buscarPorCedula(raiz, cedulaPadre);
        if (padre == null)
            return 2;
        if (padre.ligaLista == null)
            return 4;

        System.out.println("Hijos de " + padre.datos.getNombre() + " (" + cedulaPadre + "):");
        Nodo h = padre.ligaLista; // cabeza de la lista de hijos
        while (h != null) { // ya están ordenados por cédula
            System.out.println("  - " + h.datos);
            h = h.liga; // siguiente hermano
        }
        return 1;
    }

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
            hijos[i++] = h.datos; // mantiene el orden por cédula
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

    public int mostrarHermanos(int cedula) {
        if (raiz == null)
            return 3;

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return 2;

        Nodo padre = buscarPadre(raiz, cedula);
        if (padre == null)
            return 4; // la raíz no tiene hermanos

        // contar hermanos (todos los hijos del padre excepto el propio)
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
                hermanos[i++] = h.datos; // ya vienen ordenados por cédula
        }
        return hermanos;
    }

    // 1) Imprimir ancestros en consola.
    // Códigos: 1 = OK, 2 = cédula no existe, 3 = árbol vacío, 4 = es raíz (sin
    // ancestros)
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
        // De más cercano (padre) a más lejano (raíz)
        for (int i = 0; i < ancestros.length; i++) {
            System.out.println("  - " + ancestros[i]);
        }
        // Si quisieras imprimir desde la raíz hasta el padre, invierte el for:
        // for (int i = ancestros.length - 1; i >= 0; i--) { ... }

        return 1;
    }

    // 2) Devolver ancestros como arreglo (sin colecciones).
    // Orden: [padre, abuelo, bisabuelo, ..., raíz]
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

    // 1) Imprimir todos los descendientes (jerárquico, respetando el orden por
    // cédula).
    // Códigos: 1=OK, 2=cédula no existe, 3=árbol vacío, 4=no tiene descendientes
    public int mostrarDescendientes(int cedula) {
        if (raiz == null)
            return 3;

        Nodo persona = buscarPorCedula(raiz, cedula);
        if (persona == null)
            return 2;

        if (persona.ligaLista == null)
            return 4;

        System.out.println("Descendientes de " + persona.datos.getNombre() + " (" + cedula + "):");
        imprimirDescendencia(persona, 1); // imprime hijos, nietos, ...
        return 1;
    }

    private void imprimirDescendencia(Nodo nodo, int nivel) {
        // recorre hijos (ya ordenados por cédula) y baja en profundidad
        for (Nodo h = nodo.ligaLista; h != null; h = h.liga) {
            String tab = "  ".repeat(nivel);
            System.out.println(tab + "- " + h.datos);
            imprimirDescendencia(h, nivel + 1);
        }
    }

    // 2) Devolver todos los descendientes como arreglo (preorden por niveles de
    // hijos).
    // Si no tiene descendientes / no existe / árbol vacío, retorna arreglo de
    // longitud 0.
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
            c += 1; // el hijo
            c += contarDescendientesDesde(h); // y todos sus descendientes
        }
        return c;
    }

    private int llenarDescendientesDesde(Nodo nodo, DatosPersonales[] out, int idx) {
        for (Nodo h = nodo.ligaLista; h != null; h = h.liga) {
            out[idx++] = h.datos; // preorden: primero el hijo
            idx = llenarDescendientesDesde(h, out, idx); // luego su subárbol
        }
        return idx;
    }

    // En ArbolGenealogico

    // 1) Imprimir el nodo con más hijos.
    // Códigos: 1=OK, 3=árbol vacío
    public int mostrarNodoConMasHijos() {
        if (raiz == null)
            return 3;
        MaxRef ref = inicializarMax();
        dfsMaxHijos(raiz, ref);
        System.out.println("Nodo con más hijos (" + ref.hijos + "): " + ref.nodo.datos);
        return 1;
    }

    // 2) Devolver sus datos (o null si árbol vacío)
    public DatosPersonales obtenerNodoConMasHijos() {
        if (raiz == null)
            return null;
        MaxRef ref = inicializarMax();
        dfsMaxHijos(raiz, ref);
        return ref.nodo.datos;
    }

    // ------- Helpers privados -------

    // Estructura mínima para llevar el máximo encontrado
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

    // DFS: actualiza ref si encuentra un nodo con más hijos (empates: se queda el
    // primero en preorden)
    private void dfsMaxHijos(Nodo actual, MaxRef ref) {
        if (actual == null)
            return;
        int cnt = contarHijosNodo(actual);
        if (cnt > ref.hijos) {
            ref.hijos = cnt;
            ref.nodo = actual;
        }
        dfsMaxHijos(actual.ligaLista, ref); // bajar a hijos
        dfsMaxHijos(actual.liga, ref); // recorrer hermanos
    }

    // Cuenta hijos directos de un nodo (lista simple de hermanos en ligaLista)
    private int contarHijosNodo(Nodo n) {
        int c = 0;
        for (Nodo h = n.ligaLista; h != null; h = h.liga)
            c++;
        return c;
    }
    // En ArbolGenealogico

    // 1) Imprimir el nodo más profundo.
    // Códigos: 1=OK, 3=árbol vacío
    public int mostrarNodoMasProfundo() {
        if (raiz == null)
            return 3;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        System.out.println("Nodo más profundo (nivel " + ref.depth + "): " + ref.nodo.datos);
        return 1;
    }

    // 2) Obtener sus datos (o null si el árbol está vacío)
    public DatosPersonales obtenerNodoMasProfundo() {
        if (raiz == null)
            return null;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        return ref.nodo.datos;
    }

    // (Opcional) Solo el nivel de profundidad máxima (-1 si vacío)
    public int nivelNodoMasProfundo() {
        if (raiz == null)
            return -1;
        DeepRef ref = new DeepRef(raiz, 0);
        dfsMasProfundo(raiz, 0, ref);
        return ref.depth;
    }

    // ---------- Helpers privados ----------
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
        dfsMasProfundo(actual.ligaLista, depth + 1, ref); // bajar a hijos
        dfsMasProfundo(actual.liga, depth, ref); // recorrer hermanos al mismo nivel
    }

    // 1) Altura del árbol (en aristas). Vacío => -1
    public int alturaArbol() {
        return alturaDesde(raiz);
    }

    private int alturaDesde(Nodo n) {
        if (n == null)
            return -1; // convención para vacío
        int maxHijo = -1;
        for (Nodo h = n.ligaLista; h != null; h = h.liga) {
            int altHijo = alturaDesde(h);
            if (altHijo > maxHijo)
                maxHijo = altHijo;
        }
        return maxHijo + 1; // hoja => 0
    }

    // 2) Niveles del árbol (altura + 1). Vacío => 0
    public int nivelesArbol() {
        int alt = alturaArbol();
        return (alt < 0) ? 0 : alt + 1;
    }

    // 3) Mostrar altura y niveles. Códigos: 1=OK, 3=árbol vacío
    public int mostrarAlturaArbol() {
        if (raiz == null) {
            System.out.println("Árbol vacío. Altura = -1, Niveles = 0.");
            return 3;
        }
        int alt = alturaArbol();
        System.out.println("Altura (aristas): " + alt + " | Niveles: " + (alt + 1));
        return 1;
    }

    // 1) Mostrar nivel en consola.
    // Códigos: 1 = OK, 2 = cédula no existe, 3 = árbol vacío
    public int mostrarNivelPersona(int cedula) {
        if (raiz == null)
            return 3;
        int nivel = nivelPersona(cedula);
        if (nivel < 0)
            return 2;
        System.out.println("Nivel de la persona con cédula " + cedula + " = " + nivel);
        return 1;
    }

    // 2) Devolver el nivel (0 para la raíz).
    // Retorna -1 si la cédula no existe o el árbol está vacío.
    public int nivelPersona(int cedula) {
        return nivelDesde(raiz, cedula, 0);
    }

    // ---------- Helper DFS (sin colecciones) ----------
    private int nivelDesde(Nodo actual, int cedula, int nivelActual) {
        if (actual == null)
            return -1;

        if (actual.datos != null && actual.datos.getCedula() == cedula) {
            return nivelActual;
        }
        // Buscar en hijos (aumenta nivel)
        int enHijos = nivelDesde(actual.ligaLista, cedula, nivelActual + 1);
        if (enHijos >= 0)
            return enHijos;

        // Buscar en hermanos (mismo nivel)
        return nivelDesde(actual.liga, cedula, nivelActual);
    }

    // 1) Imprimir todos los registros de un nivel.
    // Códigos: 1=OK, 3=árbol vacío, 4=sin registros en ese nivel (o nivel inválido)
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

    // 2) Devolver los registros de un nivel como arreglo (sin colecciones).
    // Si no hay registros / árbol vacío / nivel inválido, retorna arreglo de
    // longitud 0.
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

    // --------- Helpers privados (DFS en hijo-izquierdo / hermano-derecho)
    // ---------
    private int contarEnNivelDesde(Nodo actual, int target, int nivelActual) {
        if (actual == null)
            return 0;

        int c = (nivelActual == target) ? 1 : 0;

        // Solo bajamos a hijos si aún no alcanzamos el nivel objetivo
        if (nivelActual < target) {
            c += contarEnNivelDesde(actual.ligaLista, target, nivelActual + 1);
        }
        // Siempre recorremos hermanos al mismo nivel
        c += contarEnNivelDesde(actual.liga, target, nivelActual);

        return c;
    }

    private void imprimirNivelDesde(Nodo actual, int target, int nivelActual) {
        if (actual == null)
            return;

        if (nivelActual == target) {
            System.out.println("  - " + actual.datos);
            // No bajamos a hijos; solo seguimos hermanos
            imprimirNivelDesde(actual.liga, target, nivelActual);
            return;
        }

        // Bajar a hijos si aún no alcanzamos el nivel
        if (nivelActual < target) {
            imprimirNivelDesde(actual.ligaLista, target, nivelActual + 1);
        }
        // Recorrer hermanos
        imprimirNivelDesde(actual.liga, target, nivelActual);
    }

    private int llenarNivelDesde(Nodo actual, int target, int nivelActual,
            DatosPersonales[] out, int idx) {
        if (actual == null)
            return idx;

        if (nivelActual == target) {
            out[idx++] = actual.datos; // mantiene orden left-to-right
            return llenarNivelDesde(actual.liga, target, nivelActual, out, idx);
        }

        if (nivelActual < target) {
            idx = llenarNivelDesde(actual.ligaLista, target, nivelActual + 1, out, idx);
        }
        return llenarNivelDesde(actual.liga, target, nivelActual, out, idx);
    }

    // En ArbolGenealogico

    // Elimina todos los nodos en 'nivelObjetivo' (y sus subárboles).
    public int eliminarNivel(int nivelObjetivo) {
        if (raiz == null)
            return 3;
        if (nivelObjetivo < 0)
            return 4;

        int cuantos = contarEnNivelDesdeV2(raiz, nivelObjetivo, 0);
        if (cuantos == 0)
            return 4;

        if (nivelObjetivo == 0) { // borrar toda la raíz (y todo el árbol)
            raiz = null;
            return 1;
        }

        eliminarNivelDesde(raiz, 0, nivelObjetivo);
        return 1;
    }

    // --------- Helpers ---------

    // Recorre y, cuando está en el nivel padre (target-1), corta la lista de hijos.
    private boolean eliminarNivelDesde(Nodo actual, int nivelActual, int target) {
        if (actual == null)
            return false;
        boolean elim = false;

        if (nivelActual == target - 1) {
            if (actual.ligaLista != null) {
                actual.ligaLista = null; // corta TODOS los nodos del nivel target (y sus subárboles)
                elim = true;
            }
            // continuar con hermanos en el mismo nivel padre
            return eliminarNivelDesde(actual.liga, nivelActual, target) || elim;
        }

        if (nivelActual < target - 1) {
            boolean eHijos = eliminarNivelDesde(actual.ligaLista, nivelActual + 1, target);
            boolean eHerm = eliminarNivelDesde(actual.liga, nivelActual, target);
            return eHijos || eHerm;
        }

        // nivelActual > target-1 (no debería ocurrir con el flujo normal), avanzar
        // hermanos
        return eliminarNivelDesde(actual.liga, nivelActual, target) || elim;
    }

    // Cuenta cuántos nodos hay exactamente en 'target'
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
