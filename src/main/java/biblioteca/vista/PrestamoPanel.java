package biblioteca.vista;

import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.PrestamoDetalle;
import biblioteca.util.Fechas;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JComboBox<Libro> cmbLibro = new JComboBox<>();
    private final JLabel lblFechas = new JLabel(" ");
    private final JLabel lblDisponibilidad = new JLabel(" ");
    private final JButton btnPrestar = Ui.boton("Registrar préstamo", Ui.EXITO);
    private final JButton btnDevolver = Ui.boton("Registrar devolución", Ui.ACENTO);
    private final JButton btnActualizar = Ui.boton("Actualizar", Ui.PRIMARIO);
    private final JProgressBar progreso = new JProgressBar();
    private final JLabel lblMensaje = new JLabel(" ");

    private final DefaultTableModel modeloActivos =
            Ui.modelo("ID", "Estudiante", "Curso", "Libro", "Fecha préstamo", "Vence", "Estado");
    private final DefaultTableModel modeloHistorial =
            Ui.modelo("ID", "Libro", "Fecha préstamo", "Vence", "Estado");
    private final JTable tablaActivos = new JTable(modeloActivos);
    private final JTable tablaHistorial = new JTable(modeloHistorial);
    private final TitledBorder bordeHistorial = BorderFactory.createTitledBorder("Historial del estudiante");
    private final JPanel panelHistorial = new JPanel(new BorderLayout());

    private List<PrestamoDetalle> activos = new ArrayList<>();
    private boolean ignorarEventos;
    private boolean bloqueado;

    public PrestamoPanel() {
        super(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(Ui.FONDO);

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setOpaque(false);
        norte.add(Ui.titulo("Préstamos y devoluciones"), BorderLayout.NORTH);
        norte.add(construirFormulario(), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new GridLayout(2, 1, 0, 12));
        centro.setOpaque(false);
        centro.add(construirPanelActivos());
        centro.add(construirPanelHistorial());
        add(centro, BorderLayout.CENTER);

        cmbLibro.addActionListener(e -> refrescarEtiquetas());
        refrescarEtiquetas();
    }

    private JPanel construirFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Nuevo préstamo"), new EmptyBorder(4, 10, 10, 10)));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        p.add(new JLabel("Estudiante:"), c);
        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(cmbEstudiante, c);
        c.gridx = 2;
        c.weightx = 0;
        c.fill = GridBagConstraints.NONE;
        p.add(new JLabel("Libro:"), c);
        c.gridx = 3;
        c.weightx = 1.4;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(cmbLibro, c);

        c.gridy = 1;
        c.gridx = 1;
        c.weightx = 1;
        p.add(lblFechas, c);
        c.gridx = 3;
        c.weightx = 1.4;
        p.add(lblDisponibilidad, c);

        JPanel accion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        accion.setOpaque(false);
        progreso.setPreferredSize(new Dimension(180, 14));
        accion.add(btnPrestar);
        accion.add(progreso);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 4;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        p.add(accion, c);

        c.gridy = 3;
        p.add(lblMensaje, c);
        return p;
    }

    private JPanel construirPanelActivos() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Préstamos activos (libros aún no devueltos)"),
                new EmptyBorder(4, 8, 8, 8)));
        Ui.configurarTabla(tablaActivos);
        tablaActivos.setAutoCreateRowSorter(true);
        tablaActivos.getColumnModel().getColumn(6).setCellRenderer(new Ui.EstadoRenderer());
        p.add(new JScrollPane(tablaActivos), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        botones.add(btnActualizar);
        botones.add(btnDevolver);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    private JPanel construirPanelHistorial() {
        panelHistorial.setBackground(Color.WHITE);
        panelHistorial.setBorder(BorderFactory.createCompoundBorder(bordeHistorial, new EmptyBorder(4, 8, 8, 8)));
        Ui.configurarTabla(tablaHistorial);
        tablaHistorial.setAutoCreateRowSorter(true);
        tablaHistorial.getColumnModel().getColumn(4).setCellRenderer(new Ui.EstadoRenderer());
        panelHistorial.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);
        return panelHistorial;
    }

    private void refrescarEtiquetas() {
        LocalDate hoy = LocalDate.now();
        lblFechas.setText("Préstamo: " + Fechas.formatear(hoy) + "   |   Vence: "
                + Fechas.formatear(hoy.plusDays(Prestamo.DIAS_PRESTAMO)) + " (" + Prestamo.DIAS_PRESTAMO + " días)");
        Libro libro = getLibroSeleccionado();
        if (libro == null) {
            lblDisponibilidad.setText(" ");
        } else if (libro.estaDisponible()) {
            lblDisponibilidad.setForeground(Ui.EXITO);
            lblDisponibilidad.setText("Disponible: " + libro.getStock() + " ejemplar(es)");
        } else {
            lblDisponibilidad.setForeground(Ui.PELIGRO);
            lblDisponibilidad.setText("SIN STOCK DISPONIBLE");
        }
    }




    public void setEstudiantes(List<Estudiante> lista) {
        Estudiante previo = getEstudianteSeleccionado();
        ignorarEventos = true;
        try {
            cmbEstudiante.setModel(new DefaultComboBoxModel<>(lista.toArray(new Estudiante[0])));
            if (previo != null) {
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getId() == previo.getId()) {
                        cmbEstudiante.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } finally {
            ignorarEventos = false;
        }
    }

    public void setLibros(List<Libro> lista) {
        Libro previo = getLibroSeleccionado();
        ignorarEventos = true;
        try {
            cmbLibro.setModel(new DefaultComboBoxModel<>(lista.toArray(new Libro[0])));
            if (previo != null) {
                for (int i = 0; i < lista.size(); i++) {
                    if (lista.get(i).getId() == previo.getId()) {
                        cmbLibro.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } finally {
            ignorarEventos = false;
        }
        refrescarEtiquetas();
    }

    public Estudiante getEstudianteSeleccionado() {
        return (Estudiante) cmbEstudiante.getSelectedItem();
    }

    public Libro getLibroSeleccionado() {
        return (Libro) cmbLibro.getSelectedItem();
    }

    public void setPrestamosActivos(List<PrestamoDetalle> lista) {
        activos = new ArrayList<>(lista);
        tablaActivos.clearSelection();
        modeloActivos.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        for (PrestamoDetalle p : activos) {
            modeloActivos.addRow(new Object[]{p.getId(), p.getNombreEstudiante(), p.getCursoEstudiante(),
                    p.getTituloLibro(), Fechas.formatear(p.getFechaPrestamo()),
                    Fechas.formatear(p.getFechaVencimiento()), p.getEstado(hoy)});
        }
    }

    public PrestamoDetalle getPrestamoActivoSeleccionado() {
        int fila = tablaActivos.getSelectedRow();
        if (fila < 0) {
            return null;
        }
        int indice = tablaActivos.convertRowIndexToModel(fila);
        return indice < activos.size() ? activos.get(indice) : null;
    }

    public void setHistorial(List<PrestamoDetalle> lista, String nombreEstudiante) {
        modeloHistorial.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        for (PrestamoDetalle p : lista) {
            modeloHistorial.addRow(new Object[]{p.getId(), p.getTituloLibro(), Fechas.formatear(p.getFechaPrestamo()),
                    Fechas.formatear(p.getFechaVencimiento()), p.getEstado(hoy)});
        }
        bordeHistorial.setTitle(nombreEstudiante == null
                ? "Historial del estudiante"
                : "Historial de " + nombreEstudiante + " (" + lista.size() + " préstamo(s))");
        panelHistorial.repaint();
    }

    public void setOcupado(boolean ocupado, String mensaje) {
        progreso.setIndeterminate(ocupado);
        btnPrestar.setEnabled(!ocupado && !bloqueado);
        btnDevolver.setEnabled(!ocupado && !bloqueado);
        btnActualizar.setEnabled(!ocupado);
        if (mensaje != null && !mensaje.isEmpty()) {
            mostrarMensaje(mensaje, false);
        }
    }

    public void setModoEstudiante(boolean soloEstudiante) {
        cmbEstudiante.setEnabled(!soloEstudiante);
    }

    public void bloquear(String motivo) {
        bloqueado = true;
        btnPrestar.setEnabled(false);
        btnDevolver.setEnabled(false);
        mostrarMensaje(motivo, true);
    }

    public void desbloquear() {
        bloqueado = false;
        btnPrestar.setEnabled(true);
        btnDevolver.setEnabled(true);
    }

    public void mostrarMensaje(String texto, boolean error) {
        lblMensaje.setForeground(error ? Ui.PELIGRO : Ui.EXITO);
        lblMensaje.setFont(Ui.fuente(Font.BOLD, 13f));
        lblMensaje.setText(Ui.html(texto, 860));
    }

    public void avisarAtraso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Devolución con atraso", JOptionPane.WARNING_MESSAGE);
    }

    public void alPrestar(ActionListener listener) {
        btnPrestar.addActionListener(listener);
    }

    public void alDevolver(ActionListener listener) {
        btnDevolver.addActionListener(listener);
    }

    public void alActualizar(ActionListener listener) {
        btnActualizar.addActionListener(listener);
    }

    public void alCambiarEstudiante(ActionListener listener) {
        cmbEstudiante.addActionListener(e -> {
            if (!ignorarEventos) {
                listener.actionPerformed(e);
            }
        });
    }
}
