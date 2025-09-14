package com.ap4;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // ArbolGenealogico arbol = new ArbolGenealogico();

        // arbol.registrar(new DatosPersonales("Jesus", 90, 60), -1);

        // // Hijos de Oscar (retorna 1 en cada inserción correcta)
        // arbol.registrar(new DatosPersonales("Hugo", 80, 25), 90);
        // arbol.registrar(new DatosPersonales("Herlinda",   60, 23), 90);
        // arbol.registrar(new DatosPersonales("Angela",   70, 23), 90);


        // // Hijo de Santiago
        // arbol.registrar(new DatosPersonales("Duvan",    30,  2), 80);
        // arbol.registrar(new DatosPersonales("Sebastian",    10,  2), 30);
        // arbol.registrar(new DatosPersonales("Velentina",    17,  2), 30);

        // arbol.registrar(new DatosPersonales("Laura",    26,  2), 70);

        // arbol.registrar(new DatosPersonales("Jhoana",    21,  2), 60);
        // arbol.registrar(new DatosPersonales("Santiago",    24,  2), 60);


        // arbol.registrar(new DatosPersonales("Estefania",    15,  2), 24);
        // arbol.registrar(new DatosPersonales("Tomas",    7,  2), 24);


        // arbol.registrar(new DatosPersonales("sara",    2,  2), 15);


        SwingUtilities.invokeLater(() -> new GenealogyUI().setVisible(true));
    }
}