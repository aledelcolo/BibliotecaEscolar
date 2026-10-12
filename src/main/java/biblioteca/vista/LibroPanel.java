package biblioteca.vista;

import biblioteca.modelo.Categoria;
import biblioteca.modelo.Libro;
import biblioteca.util.ValidacionException;
import biblioteca.util.Validador;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.util.List;

public class LibroPanel extends PanelCrud<Libro> {

    private static final long serialVersionUID = 1L;

    private final JTextField txtTitulo = new JTextField();
    private final JTextField txtAutor = new JTextField();
    private final JTextField txtIsbn = new JTextField();
    private final JTextField txtEditorial = new JTextField();
    private final JComboBox<Categoria> cmbCategoria = new JComboBox<>();
    private final JSpinner spnStock = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));

    public LibroPanel() {
        inicializar();
    }

    @Override
    protected String titulo() {
        return "Gestión de libros";
    }

    @Override
    protected String[] columnas() {
        return new String[]{"ID", "Título", "Autor", "ISBN", "Editorial", "Categoría", "Stock"};
    }

    @Override
    protected Object[] fila(Libro l) {
        return new Object[]{l.getId(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(),
                l.getNombreCategoria(), l.getStock()};
    }

    @Override
    protected void construirFormulario(JPanel f) {
        Ui.fila(f, "Título *", txtTitulo);
        Ui.fila(f, "Autor *", txtAutor);
        Ui.fila(f, "ISBN *", txtIsbn);
        Ui.fila(f, "Editorial", txtEditorial);
        Ui.fila(f, "Categoría *", cmbCategoria);
        Ui.fila(f, "Stock disponible *", spnStock);
    }

    @Override
    protected void cargarFormulario(Libro l) {
        txtTitulo.setText(l.getTitulo());
        txtAutor.setText(l.getAutor());
        txtIsbn.setText(l.getIsbn());
        txtEditorial.setText(l.getEditorial());
        spnStock.setValue(l.getStock());
        for (int i = 0; i < cmbCategoria.getItemCount(); i++) {
            if (cmbCategoria.getItemAt(i).getId() == l.getIdCategoria()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    protected void limpiarFormulario() {
        txtTitulo.setText("");
        txtAutor.setText("");
        txtIsbn.setText("");
        txtEditorial.setText("");
        spnStock.setValue(1);
        cmbCategoria.setSelectedIndex(cmbCategoria.getItemCount() > 0 ? 0 : -1);
    }

    @Override
    public Libro leerFormulario() throws ValidacionException {
        Libro l = new Libro();
        l.setId(seleccionado != null ? seleccionado.getId() : 0);
        l.setTitulo(Validador.requerido(txtTitulo.getText(), "Título"));
        l.setAutor(Validador.requerido(txtAutor.getText(), "Autor"));
        l.setIsbn(Validador.requerido(txtIsbn.getText(), "ISBN"));
        l.setEditorial(txtEditorial.getText().trim());
        Categoria categoria = (Categoria) cmbCategoria.getSelectedItem();
        if (categoria == null) {
            throw new ValidacionException("Selecciona una categoría (si no existe ninguna, créala en el módulo Categorías).");
        }
        l.setIdCategoria(categoria.getId());
        l.setStock((Integer) spnStock.getValue());
        return l;
    }

    public void setCategorias(List<Categoria> categorias) {
        DefaultComboBoxModel<Categoria> modelo = new DefaultComboBoxModel<>();
        for (Categoria c : categorias) {
            modelo.addElement(c);
        }
        cmbCategoria.setModel(modelo);
    }
}
