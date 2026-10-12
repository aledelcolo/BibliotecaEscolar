package biblioteca.vista;

import biblioteca.modelo.Categoria;
import biblioteca.util.ValidacionException;
import biblioteca.util.Validador;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class CategoriaPanel extends PanelCrud<Categoria> {

    private static final long serialVersionUID = 1L;

    private final JTextField txtNombre = new JTextField();

    public CategoriaPanel() {
        inicializar();
    }

    @Override
    protected String titulo() {
        return "Categorías de libros";
    }

    @Override
    protected String[] columnas() {
        return new String[]{"ID", "Nombre"};
    }

    @Override
    protected Object[] fila(Categoria c) {
        return new Object[]{c.getId(), c.getNombre()};
    }

    @Override
    protected void construirFormulario(JPanel f) {
        Ui.fila(f, "Nombre de la categoría *", txtNombre);
    }

    @Override
    protected void cargarFormulario(Categoria c) {
        txtNombre.setText(c.getNombre());
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.setText("");
    }

    @Override
    public Categoria leerFormulario() throws ValidacionException {
        Categoria c = new Categoria();
        c.setId(seleccionado != null ? seleccionado.getId() : 0);
        c.setNombre(Validador.requerido(txtNombre.getText(), "Nombre"));
        return c;
    }
}
