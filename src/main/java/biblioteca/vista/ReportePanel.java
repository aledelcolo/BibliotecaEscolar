package biblioteca.vista;

import biblioteca.modelo.Estudiante;
import biblioteca.modelo.TipoReporte;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;

public class ReportePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JComboBox<TipoReporte> cmbReporte = new JComboBox<>(TipoReporte.values());
    private final JComboBox<Estudiante> cmbEstudiante = new JComboBox<>();
    private final JButton btnGenerar = Ui.boton("Generar reporte", Ui.PRIMARIO);
    private final DefaultTableModel modelo = Ui.modelo();
    private final JTable tabla = new JTable(modelo);
    private final JLabel lblTitulo = new JLabel(" ");
    private final JLabel lblResumen = new JLabel(" ");

    public ReportePanel() {
        super(new BorderLayout(12, 12));
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(Ui.FONDO);

        JPanel norte = new JPanel(new BorderLayout(0, 10));
        norte.setOpaque(false);
        norte.add(Ui.titulo("Reportes"), BorderLayout.NORTH);
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(Ui.borde(10));
        cmbReporte.setPreferredSize(new Dimension(270, 30));
        cmbEstudiante.setPreferredSize(new Dimension(270, 30));
        filtros.add(new JLabel("Reporte:"));
        filtros.add(cmbReporte);
        filtros.add(new JLabel("Estudiante:"));
        filtros.add(cmbEstudiante);
        filtros.add(btnGenerar);
        norte.add(filtros, BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setBackground(Color.WHITE);
        centro.setBorder(Ui.borde(12));
        lblTitulo.setFont(Ui.fuente(Font.BOLD, 16f));
        lblTitulo.setForeground(Ui.PRIMARIO);
        centro.add(lblTitulo, BorderLayout.NORTH);
        Ui.configurarTabla(tabla);
        tabla.setAutoCreateRowSorter(true);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);
        centro.add(lblResumen, BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);


        cmbEstudiante.setEnabled(false);
        cmbReporte.addActionListener(e ->
                cmbEstudiante.setEnabled(getReporteSeleccionado() == TipoReporte.HISTORIAL_ESTUDIANTE));
    }

    public TipoReporte getReporteSeleccionado() {
        return (TipoReporte) cmbReporte.getSelectedItem();
    }

    public Estudiante getEstudianteSeleccionado() {
        return (Estudiante) cmbEstudiante.getSelectedItem();
    }

    public void setEstudiantes(List<Estudiante> lista) {
        Estudiante previo = getEstudianteSeleccionado();
        cmbEstudiante.setModel(new DefaultComboBoxModel<>(lista.toArray(new Estudiante[0])));
        if (previo != null) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getId() == previo.getId()) {
                    cmbEstudiante.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void setTabla(String titulo, String[] columnas, List<Object[]> filas) {
        modelo.setRowCount(0);
        modelo.setColumnIdentifiers(columnas);
        for (Object[] fila : filas) {
            modelo.addRow(fila);
        }
        for (int i = 0; i < columnas.length; i++) {
            if ("Estado".equals(columnas[i])) {
                tabla.getColumnModel().getColumn(i).setCellRenderer(new Ui.EstadoRenderer());
            }
        }
        lblTitulo.setText(titulo);
        lblResumen.setForeground(Ui.TEXTO_SUAVE);
        lblResumen.setText(filas.size() + " registro(s)");
    }

    public void alGenerar(ActionListener listener) {
        btnGenerar.addActionListener(listener);
    }

    public void mostrarError(String mensaje) {
        lblResumen.setForeground(Ui.PELIGRO);
        lblResumen.setText(mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }
}
