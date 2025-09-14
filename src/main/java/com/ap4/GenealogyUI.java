package com.ap4;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.util.Objects;

/**
 * UI para gestionar y visualizar el Árbol Genealógico usando tu estructura:
 *  - ligaLista = primer hijo
 *  - liga      = siguiente hermano
 *
 * No usa listas ni colecciones en la estructura; solo Swing para la vista.
 */
public class GenealogyUI extends JFrame {

    private final ArbolGenealogico arbol = new ArbolGenealogico();

    // Panel derecho
    private final JTree tree = new JTree();
    private final JTextArea console = new JTextArea(10, 40);

    // Campos Gestión
    private final JTextField tfNombre = new JTextField();
    private final JTextField tfCedula = new JTextField();
    private final JTextField tfEdad   = new JTextField();
    private final JTextField tfPadre  = new JTextField("-1"); // -1 para raíz

    // Campos Actualizar
    private final JTextField tfCedulaActual = new JTextField();
    private final JTextField tfNuevoNombre  = new JTextField();
    private final JTextField tfNuevaCedula  = new JTextField();
    private final JTextField tfNuevaEdad    = new JTextField();

    // Campos Consultas / Niveles
    private final JTextField tfCedulaConsulta = new JTextField();
    private final JTextField tfNivel          = new JTextField();

    public GenealogyUI() {
        super("Gestión de Árboles Genealógicos — Demo");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(1200, 700));
        setLocationRelativeTo(null);

        // Look and feel "moderno"
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.font",   new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.font",    new Font("Segoe UI", Font.PLAIN, 13));
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ignored) {}

        add(buildHeader(), BorderLayout.NORTH);
        add(buildMain(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        refreshTree();
        log("¡Bienvenido! Inserta datos o usa “Datos de Prueba”.");
    }

    private JComponent buildHeader() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Práctica: Gestión de Árboles Genealógicos");
        title.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 20));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btSeed = new JButton("Datos de prueba");
        JButton btClear = new JButton("Reiniciar árbol");

        btSeed.addActionListener(e -> seedDemo());
        btClear.addActionListener(e -> {
            // Reiniciar rápido: eliminar nivel 0
            int code = arbol.eliminarNivel(0);
            refreshTree();
            log("Reiniciado árbol. Código: " + code);
        });

        actions.add(btSeed);
        actions.add(btClear);

        top.add(title, BorderLayout.WEST);
        top.add(actions, BorderLayout.EAST);
        return top;
    }

    private JComponent buildMain() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setResizeWeight(0.45);

        // IZQUIERDA: pestañas de operaciones
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("1) Gestión de Personas", buildTabGestion());
        tabs.addTab("2) Relaciones Familiares", buildTabRelaciones());
        tabs.addTab("3) Estructura del Árbol", buildTabEstructura());
        tabs.addTab("4) Eliminación de Niveles", buildTabNiveles());
        split.setLeftComponent(wrap(tabs));

        // DERECHA: árbol + consola
        JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        right.setResizeWeight(0.65);

        tree.setBorder(new EmptyBorder(8,8,8,8));
        tree.setRowHeight(22);
        right.setTopComponent(new JScrollPane(tree));

        console.setEditable(false);
        console.setLineWrap(true);
        console.setWrapStyleWord(true);
        right.setBottomComponent(new JScrollPane(console));

        split.setRightComponent(right);
        return split;
    }

    private JComponent buildFooter() {
        JPanel foot = new JPanel(new BorderLayout());
        foot.setBorder(new EmptyBorder(8,12,12,12));
        JLabel hint = new JLabel("Consejo: usa el árbol de la derecha para visualizar la estructura.");
        foot.add(hint, BorderLayout.WEST);
        return foot;
    }

    // --------- TABS ---------

    private JComponent buildTabGestion() {
        JPanel p = panelGrid(10);

        p.add(lbl("Nombre:")); p.add(tfNombre);
        p.add(lbl("Cédula:")); p.add(tfCedula);
        p.add(lbl("Edad:"));   p.add(tfEdad);
        p.add(lbl("Cédula del Padre (-1 si raíz):")); p.add(tfPadre);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btRegistrar = new JButton("Registrar");
        JButton btEliminar  = new JButton("Eliminar por Cédula");
        actions.add(btRegistrar);
        actions.add(btEliminar);

        btRegistrar.addActionListener(e -> onRegistrar());
        btEliminar.addActionListener(e -> onEliminar());

        // Actualizar
        JPanel sep = new JPanel(); sep.setPreferredSize(new Dimension(10, 10));
        p.add(sep); p.add(new JLabel());

        p.add(lbl("Cédula Actual:"));     p.add(tfCedulaActual);
        p.add(lbl("Nuevo Nombre (opcional):")); p.add(tfNuevoNombre);
        p.add(lbl("Nueva Cédula (opcional):")); p.add(tfNuevaCedula);
        p.add(lbl("Nueva Edad (opcional):"));   p.add(tfNuevaEdad);

        JButton btActualizar = new JButton("Actualizar");
        JPanel actions2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions2.add(btActualizar);
        btActualizar.addActionListener(e -> onActualizar());

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(12,12,12,12));
        container.add(p);
        container.add(Box.createVerticalStrut(10));
        container.add(actions);
        container.add(Box.createVerticalStrut(10));
        container.add(line());
        container.add(Box.createVerticalStrut(10));
        container.add(actions2);
        return new JScrollPane(container);
    }

    private JComponent buildTabRelaciones() {
        JPanel p = panelGrid(10);

        p.add(lbl("Cédula:")); p.add(tfCedulaConsulta);

        JPanel actions = new JPanel(new GridLayout(3, 2, 8, 8));
        JButton btPadre = new JButton("Padre");
        JButton btHijos = new JButton("Hijos");
        JButton btHermanos = new JButton("Hermanos");
        JButton btAncestros = new JButton("Ancestros");
        JButton btDesc = new JButton("Descendientes");
        JButton btNivel = new JButton("Nivel del Registro");

        actions.add(btPadre);
        actions.add(btHijos);
        actions.add(btHermanos);
        actions.add(btAncestros);
        actions.add(btDesc);
        actions.add(btNivel);

        btPadre.addActionListener(e -> onPadre());
        btHijos.addActionListener(e -> onHijos());
        btHermanos.addActionListener(e -> onHermanos());
        btAncestros.addActionListener(e -> onAncestros());
        btDesc.addActionListener(e -> onDescendientes());
        btNivel.addActionListener(e -> onNivel());

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(12,12,12,12));
        c.add(p);
        c.add(Box.createVerticalStrut(10));
        c.add(actions);
        return new JScrollPane(c);
    }

    private JComponent buildTabEstructura() {
        JPanel p = panelGrid(10);
        p.add(lbl("Nivel:")); p.add(tfNivel);

        JPanel actions = new JPanel(new GridLayout(3, 2, 8, 8));
        JButton btMayorGrado  = new JButton("Nodo con Mayor Grado");
        JButton btMasProfundo = new JButton("Nodo con Mayor Nivel");
        JButton btAltura      = new JButton("Altura del Árbol");
        JButton btRegNivel    = new JButton("Registros por Nivel");
        JButton btRefresh     = new JButton("Refrescar Árbol");
        JButton btImprimir    = new JButton("Imprimir (texto)");

        actions.add(btMayorGrado);
        actions.add(btMasProfundo);
        actions.add(btAltura);
        actions.add(btRegNivel);
        actions.add(btRefresh);
        actions.add(btImprimir);

        btMayorGrado.addActionListener(e -> onMayorGrado());
        btMasProfundo.addActionListener(e -> onMasProfundo());
        btAltura.addActionListener(e -> onAltura());
        btRegNivel.addActionListener(e -> onRegistrosPorNivel());
        btRefresh.addActionListener(e -> refreshTree());
        btImprimir.addActionListener(e -> {
            arbol.imprimir();
            log("(Estructura imprimida en consola estándar)");
        });

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(12,12,12,12));
        c.add(p);
        c.add(Box.createVerticalStrut(10));
        c.add(actions);
        return new JScrollPane(c);
    }

    private JComponent buildTabNiveles() {
        JPanel p = panelGrid(10);

        JTextField tfNivelEliminar = new JTextField();
        p.add(lbl("Nivel a eliminar:"));
        p.add(tfNivelEliminar);

        JButton btEliminarNivel = new JButton("Eliminar Nivel");
        btEliminarNivel.addActionListener(e -> {
            Integer lvl = parseInt(tfNivelEliminar.getText());
            if (lvl == null) { warn("Nivel inválido."); return; }
            int code = arbol.eliminarNivel(lvl);
            refreshTree();
            log("Eliminar Nivel(" + lvl + ") → código: " + code);
        });

        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(new EmptyBorder(12,12,12,12));
        c.add(p);
        c.add(Box.createVerticalStrut(10));
        c.add(btEliminarNivel);
        return new JScrollPane(c);
    }

    // --------- Acciones ---------

    private void onRegistrar() {
        String nombre = tfNombre.getText().trim();
        Integer cedula = parseInt(tfCedula.getText());
        Integer edad   = parseInt(tfEdad.getText());
        Integer padre  = parseInt(tfPadre.getText());

        if (nombre.isEmpty() || cedula == null || edad == null || padre == null) {
            warn("Completa Nombre, Cédula, Edad y Cédula del Padre (-1 para raíz).");
            return;
        }
        int code = arbol.registrar(new DatosPersonales(nombre, cedula, edad), padre);
        refreshTree();
        log("Registrar(" + nombre + ", " + cedula + ", " + edad + "; padre=" + padre + ") → código: " + code);
    }

    private void onEliminar() {
        Integer ced = parseInt(tfCedula.getText());
        if (ced == null) { warn("Cédula inválida."); return; }
        int code = arbol.eliminar(ced);
        refreshTree();
        log("Eliminar(" + ced + ") → código: " + code);
    }

    private void onActualizar() {
        Integer cedAct = parseInt(tfCedulaActual.getText());
        if (cedAct == null) { warn("Cédula actual inválida."); return; }

        String nuevoNombre = tfNuevoNombre.getText().trim();
        Integer nuevaCed   = tfNuevaCedula.getText().trim().isEmpty() ? null : parseInt(tfNuevaCedula.getText());
        Integer nuevaEdad  = tfNuevaEdad.getText().trim().isEmpty()   ? null : parseInt(tfNuevaEdad.getText());

        if ((nuevoNombre.isEmpty()) && nuevaCed == null && nuevaEdad == null) {
            warn("Indica al menos un campo a actualizar.");
            return;
        }
        String nombreArg = nuevoNombre.isEmpty() ? null : nuevoNombre;
        int code = arbol.modificar(cedAct, nombreArg, nuevaCed, nuevaEdad);
        refreshTree();
        log("Actualizar(ced=" + cedAct + ", nombre=" + nombreArg + ", nuevaCed=" + nuevaCed + ", nuevaEdad=" + nuevaEdad + ") → código: " + code);
    }

    private void onPadre() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        DatosPersonales p = arbol.obtenerPadre(c);
        if (p == null) log("Padre de " + c + ": (no existe o es raíz)");
        else log("Padre de " + c + ": " + fmt(p));
    }

    private void onHijos() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        DatosPersonales[] hijos = arbol.obtenerHijos(c);
        if (hijos.length == 0) { log("Hijos de " + c + ": (ninguno / no existe)"); return; }
        log("Hijos de " + c + ":");
        for (DatosPersonales d : hijos) log("  - " + fmt(d));
    }

    private void onHermanos() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        DatosPersonales[] hermanos = arbol.obtenerHermanos(c);
        if (hermanos.length == 0) { log("Hermanos de " + c + ": (ninguno / no existe / es raíz)"); return; }
        log("Hermanos de " + c + ":");
        for (DatosPersonales d : hermanos) log("  - " + fmt(d));
    }

    private void onAncestros() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        DatosPersonales[] anc = arbol.obtenerAncestros(c);
        if (anc.length == 0) { log("Ancestros de " + c + ": (ninguno / no existe / es raíz)"); return; }
        log("Ancestros de " + c + " (padre → raíz):");
        for (DatosPersonales d : anc) log("  - " + fmt(d));
    }

    private void onDescendientes() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        DatosPersonales[] des = arbol.obtenerDescendientes(c);
        if (des.length == 0) { log("Descendientes de " + c + ": (ninguno / no existe)"); return; }
        log("Descendientes de " + c + " (preorden):");
        for (DatosPersonales d : des) log("  - " + fmt(d));
    }

    private void onNivel() {
        Integer c = parseInt(tfCedulaConsulta.getText());
        if (c == null) { warn("Cédula inválida."); return; }
        int nivel = arbol.nivelPersona(c);
        if (nivel < 0) log("Nivel(" + c + "): no existe.");
        else log("Nivel(" + c + ") = " + nivel);
    }

    private void onMayorGrado() {
        DatosPersonales d = arbol.obtenerNodoConMasHijos();
        if (d == null) { log("Árbol vacío."); return; }
        log("Nodo con mayor grado (más hijos): " + fmt(d));
    }

    private void onMasProfundo() {
        DatosPersonales d = arbol.obtenerNodoMasProfundo();
        if (d == null) { log("Árbol vacío."); return; }
        int nivel = arbol.nivelNodoMasProfundo();
        log("Nodo más profundo (nivel " + nivel + "): " + fmt(d));
    }

    private void onAltura() {
        int alt = arbol.alturaArbol();
        int niv = arbol.nivelesArbol();
        log("Altura del árbol (aristas): " + alt + " | Niveles: " + niv);
    }

    private void onRegistrosPorNivel() {
        Integer lvl = parseInt(tfNivel.getText());
        if (lvl == null) { warn("Nivel inválido."); return; }
        DatosPersonales[] registros = arbol.obtenerRegistrosEnNivel(lvl);
        if (registros.length == 0) { log("Nivel " + lvl + ": (sin registros)"); return; }
        log("Registros en el nivel " + lvl + ":");
        for (DatosPersonales d : registros) log("  - " + fmt(d));
    }

    // --------- Util ----------

    private void refreshTree() {
        DefaultMutableTreeNode root = buildSwingTree(arbol.getRaiz());
        tree.setModel(new DefaultTreeModel(root));
        expandAll(tree);
    }

    private DefaultMutableTreeNode buildSwingTree(Nodo n) {
        if (n == null) return new DefaultMutableTreeNode("(árbol vacío)");
        DefaultMutableTreeNode swingNode =
                new DefaultMutableTreeNode(n.datos.getNombre() + "  (" + n.datos.getCedula() + ", " + n.datos.getEdad() + ")");
        // recorrer hijos (ligaLista con hermanos por liga)
        for (Nodo h = n.ligaLista; h != null; h = h.liga) {
            swingNode.add(buildSwingTree(h));
        }
        return swingNode;
    }

    private void expandAll(JTree tree) {
        for (int i = 0; i < tree.getRowCount(); i++) tree.expandRow(i);
    }

    private static JPanel panelGrid(int gap) {
        JPanel p = new JPanel(new GridLayout(0,2, gap, gap));
        p.setOpaque(false);
        return p;
    }
    private static JLabel lbl(String s){ return new JLabel(s); }
    private static JComponent line(){ JSeparator sep = new JSeparator(); sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); return sep; }
    private static JComponent wrap(JComponent c){ JPanel p=new JPanel(new BorderLayout()); p.setBorder(new EmptyBorder(8,8,8,8)); p.add(c); return p; }

    private static Integer parseInt(String s) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception e) { return null; }
    }
    private static String fmt(DatosPersonales d) {
        if (d == null) return "(null)";
        return d.getNombre() + " — CC " + d.getCedula() + " — " + d.getEdad() + " años";
    }

    private void log(String msg) {
        console.append(msg + "\n");
        console.setCaretPosition(console.getDocument().getLength());
    }
    private void warn(String m){ JOptionPane.showMessageDialog(this, m, "Atención", JOptionPane.WARNING_MESSAGE); }

    // ---- Semilla de datos de ejemplo ----
    private void seedDemo() {
        // Limpia todo
        arbol.eliminarNivel(0);

        // Raíz
        arbol.registrar(new DatosPersonales("Oscar", 50, 60), -1);

        // Hijos de Oscar (ordenados por cédula)
        arbol.registrar(new DatosPersonales("Santiago", 30, 25), 50);
        arbol.registrar(new DatosPersonales("Jhoana",   40, 23), 50);
        arbol.registrar(new DatosPersonales("Emilio",   70, 21), 50);

        // Nietos
        arbol.registrar(new DatosPersonales("Tomas",    10,  2), 30);
        arbol.registrar(new DatosPersonales("Rosa",     65,  1), 70);

        // Bisnieta
        arbol.registrar(new DatosPersonales("Laura",    90,  0), 65);

        refreshTree();
        log("Datos de prueba cargados.");
    }
}
