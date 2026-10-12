package biblioteca.controlador;

import biblioteca.dao.CategoriaDAO;
import biblioteca.modelo.Categoria;
import biblioteca.vista.CategoriaPanel;

public class CategoriaController extends CrudController<Categoria> {

    public CategoriaController(CategoriaPanel panel, CategoriaDAO categoriaDAO) {
        super(panel, categoriaDAO);
    }
}
