package biblioteca.controlador;

import biblioteca.dao.CategoriaDAO;
import biblioteca.dao.DAOException;
import biblioteca.dao.LibroDAO;
import biblioteca.modelo.Libro;
import biblioteca.vista.LibroPanel;

public class LibroController extends CrudController<Libro> {

    private final LibroPanel panel;
    private final CategoriaDAO categoriaDAO;

    public LibroController(LibroPanel panel, LibroDAO libroDAO, CategoriaDAO categoriaDAO) {
        super(panel, libroDAO);
        this.panel = panel;
        this.categoriaDAO = categoriaDAO;
    }

    @Override
    public void cargar() {
        try {
            panel.setCategorias(categoriaDAO.listar());
        } catch (DAOException ex) {
            panel.mostrarError(ex.getMessage());
        }
        super.cargar();
    }
}
