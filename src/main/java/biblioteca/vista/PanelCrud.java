package biblioteca.vista;

import biblioteca.modelo.EntidadBase;
import biblioteca.util.ValidacionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PanelCrud<T extends EntidadBase> extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultTableModel modeloTabla = Ui.modelo();
    private final JTable tabla = new JTable(modeloTabla);
    private final TableRowSorter<DefaultTableModel> ordenador = new TableRowSorter<>(modeloTabla);
    private final JTextField txtBuscar = new JTextField(18);
    private final JPanel panelFormulario = new JPanel(new GridBagLayout());
    private final JPanel panelLateral = new JPanel(new BorderLayout());
    private final JLabel lblEstado = new JLabel(" ");
    protected final JButton btnNuevo = Ui.boton("Nuevo", Ui.ACENTO);
    protected final JButton btnGuardar = Ui.boton("Guardar", Ui.EXITO);
    protected final JButton btnEliminar = Ui.boton("Eliminar", Ui.PELIGRO);

    private List<T> items = new ArrayList<>();
    protected T seleccionado;

    protected PanelCrud() {
        super(new BorderLayout(12, 12));
    }




    protected abstract String titulo();

    protected abstract String[] columnas();

    protected abstract Object[] fila(T item);

    protected abstract void construirFormulario(JPanel formulario);

    protected abstract void cargarFormulario(T item);

    protected abstract void limpiarFormulario();

    public abstract T leerFormulario() throws ValidacionException;

    protected final void inicializar() {
        setBorder(new EmptyBorder(16, 16, 16, 16));
        setBackground(Ui.FONDO);


        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        encabezado.add(Ui.titulo(titulo()), BorderLayout.WEST);
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        busqueda.setOpaque(false);
        busqueda.add(new JLabel("Buscar:"));
        busqueda.add(txtBuscar);
        encabezado.add(busqueda, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);


        modeloTabla.setColumnIdentifiers(columnas());
        tabla.setRowSorter(ordenador);
        Ui.configurarTabla(tabla);
        add(new JScrollPane(tabla), BorderLayout.CENTER);


        panelFormulario.setOpaque(false);
        construirFormulario(panelFormulario);
        Ui.agregarRelleno(panelFormulario);

        JPanel tarjeta = new JPanel(new BorderLayout(0, 12));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(Ui.borde(14));
        JLabel encabezadoForm = new JLabel("Datos del registro");
        encabezadoForm.setFont(Ui.fuente(Font.BOLD, 15f));
        tarjeta.add(encabezadoForm, BorderLayout.NORTH);
        tarjeta.add(panelFormulario, BorderLayout.CENTER);
        JPanel botones = new JPanel(new GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        tarjeta.add(botones, BorderLayout.SOUTH);

        panelLateral.setOpaque(false);
        panelLateral.setPreferredSize(new Dimension(340, 0));
        panelLateral.add(tarjeta, BorderLayout.CENTER);
        add(panelLateral, BorderLayout.EAST);


        lblEstado.setFont(Ui.fuente(Font.BOLD, 13f));
        add(lblEstado, BorderLayout.SOUTH);


        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int fila = tabla.getSelectedRow();
            if (fila < 0) {
                return;
            }
            int indice = tabla.convertRowIndexToModel(fila);
            if (indice >= 0 && indice < items.size()) {
                seleccionado = items.get(indice);
                cargarFormulario(seleccionado);
            }
        });
        btnNuevo.addActionListener(e -> {
            limpiar();
            lblEstado.setText(" ");
        });
        Ui.alCambiar(txtBuscar, this::aplicarFiltro);
    }

    private void aplicarFiltro() {
        String texto = txtBuscar.getText().trim();
        ordenador.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?iu)" + Pattern.quote(texto)));
    }




    public void setItems(List<T> nuevos) {
        items = new ArrayList<>(nuevos);
        tabla.clearSelection();
        modeloTabla.setRowCount(0);
        for (T item : items) {
            modeloTabla.addRow(fila(item));
        }
        seleccionado = null;
    }

    public T getSeleccionado() {
        return seleccionado;
    }

    public void limpiar() {
        tabla.clearSelection();
        seleccionado = null;
        limpiarFormulario();
    }

    public void alGuardar(ActionListener listener) {
        btnGuardar.addActionListener(listener);
    }

    public void alEliminar(ActionListener listener) {
        btnEliminar.addActionListener(listener);
    }

    public boolean confirmarEliminacion(T item) {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que deseas eliminar \"" + item.getDescripcion() + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    public void mostrarExito(String mensaje) {
        lblEstado.setForeground(Ui.EXITO);
        lblEstado.setText(mensaje);
    }

    public void mostrarError(String mensaje) {
        lblEstado.setForeground(Ui.PELIGRO);
        lblEstado.setText(mensaje);
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    public void setSoloLectura(boolean soloLectura) {
        panelLateral.setVisible(!soloLectura);
    }
}
