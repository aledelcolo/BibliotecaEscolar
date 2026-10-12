package biblioteca.vista;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public final class Ui {

    public static final Color PRIMARIO = new Color(0x1F4E79);
    public static final Color ACENTO = new Color(0x2E86C1);
    public static final Color EXITO = new Color(0x2E7D32);
    public static final Color PELIGRO = new Color(0xC62828);
    public static final Color ALERTA = new Color(0xEF6C00);
    public static final Color FONDO = new Color(0xF4F6F8);
    public static final Color LATERAL = new Color(0x1B2A41);
    public static final Color TEXTO_SUAVE = new Color(0x607D8B);
    public static final Color BORDE = new Color(0xCFD8DC);

    private Ui() {
    }

    public static void aplicarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {

        }
        List<Object> claves = new ArrayList<>();
        Enumeration<Object> todas = UIManager.getDefaults().keys();
        while (todas.hasMoreElements()) {
            claves.add(todas.nextElement());
        }
        for (Object clave : claves) {
            Object valor = UIManager.get(clave);
            if (valor instanceof FontUIResource) {
                UIManager.put(clave, new FontUIResource(((Font) valor).deriveFont(13f)));
            }
        }
    }

    public static Font fuente(int estilo, float tamano) {
        Font base = UIManager.getFont("Label.font");
        if (base == null) {
            base = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
        }
        return base.deriveFont(estilo, tamano);
    }

    public static JLabel titulo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(fuente(Font.BOLD, 22f));
        etiqueta.setForeground(PRIMARIO);
        return etiqueta;
    }

    public static JButton boton(String texto, Color fondo) {
        JButton boton = new JButton(texto);
        boton.setBackground(fondo);
        boton.setForeground(Color.WHITE);
        boton.setFont(fuente(Font.BOLD, 13f));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBorder(new EmptyBorder(9, 16, 9, 16));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addPropertyChangeListener("enabled",
                e -> boton.setBackground(boton.isEnabled() ? fondo : new Color(0x9E9E9E)));
        return boton;
    }

    public static void fila(JPanel panel, String etiqueta, Component campo) {
        int indice = panel.getComponentCount() / 2;
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;
        c.gridy = indice * 2;
        c.insets = new Insets(indice == 0 ? 0 : 10, 0, 3, 0);
        panel.add(new JLabel(etiqueta), c);
        c.gridy = indice * 2 + 1;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(campo, c);
    }

    public static void agregarRelleno(JPanel panel) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 999;
        c.weighty = 1;
        panel.add(Box.createVerticalGlue(), c);
    }

    public static DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columna) {
                Object valor = getRowCount() > 0 ? getValueAt(0, columna) : null;
                return valor != null ? valor.getClass() : Object.class;
            }
        };
    }

    public static void configurarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(0xE3E8EC));
        tabla.setSelectionBackground(new Color(0xBBDEFB));
        tabla.setSelectionForeground(Color.BLACK);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setDefaultRenderer(new CabeceraRenderer());
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 32));
    }

    public static String html(String texto, int ancho) {
        String limpio = texto == null ? "" : texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
        return "<html><body style='width:" + ancho + "px'>" + limpio + "</body></html>";
    }

    public static void alCambiar(JTextComponent campo, Runnable accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                accion.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                accion.run();
            }
        });
    }

    private static class CabeceraRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            super.getTableCellRendererComponent(tabla, valor, false, false, fila, columna);
            setOpaque(true);
            setBackground(PRIMARIO);
            setForeground(Color.WHITE);
            setFont(fuente(Font.BOLD, 13f));
            setBorder(new EmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    public static class EstadoRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable tabla, Object valor, boolean seleccionada,
                                                       boolean foco, int fila, int columna) {
            Component c = super.getTableCellRendererComponent(tabla, valor, seleccionada, foco, fila, columna);
            String texto = valor == null ? "" : valor.toString();
            c.setFont(fuente(Font.BOLD, 13f));
            if (!seleccionada) {
                if (texto.startsWith("ATRASADO")) {
                    c.setForeground(PELIGRO);
                } else if (texto.startsWith("VENCE")) {
                    c.setForeground(ALERTA);
                } else if (texto.startsWith("DEVUELTO")) {
                    c.setForeground(TEXTO_SUAVE);
                } else {
                    c.setForeground(EXITO);
                }
            }
            return c;
        }
    }

    public static javax.swing.border.Border borde(int margen) {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDE), new EmptyBorder(margen, margen, margen, margen));
    }
}
