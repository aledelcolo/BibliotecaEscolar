package biblioteca.controlador;

import biblioteca.dao.DAOException;
import biblioteca.dao.GenericDAO;
import biblioteca.modelo.EntidadBase;
import biblioteca.util.ValidacionException;
import biblioteca.vista.PanelCrud;

public abstract class CrudController<T extends EntidadBase> {

    protected final PanelCrud<T> vista;
    protected final GenericDAO<T> dao;

    protected CrudController(PanelCrud<T> vista, GenericDAO<T> dao) {
        this.vista = vista;
        this.dao = dao;
        vista.alGuardar(e -> guardar());
        vista.alEliminar(e -> eliminar());
    }

    public void cargar() {
        try {
            vista.setItems(dao.listar());
        } catch (DAOException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    protected void guardar() {
        try {
            T entidad = vista.leerFormulario();
            boolean eraNueva = entidad.esNueva();
            if (eraNueva) {
                dao.insertar(entidad);
            } else {
                dao.actualizar(entidad);
            }
            despuesDeGuardar(entidad, eraNueva);
            cargar();
            vista.limpiar();
            vista.mostrarExito(eraNueva ? "Registro creado correctamente." : "Registro actualizado correctamente.");
        } catch (ValidacionException | DAOException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    protected void eliminar() {
        T seleccionado = vista.getSeleccionado();
        if (seleccionado == null) {
            vista.mostrarError("Selecciona un registro de la tabla para eliminarlo.");
            return;
        }
        if (!vista.confirmarEliminacion(seleccionado)) {
            return;
        }
        try {
            dao.eliminar(seleccionado.getId());
            cargar();
            vista.limpiar();
            vista.mostrarExito("Registro eliminado correctamente.");
        } catch (DAOException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }

    protected void despuesDeGuardar(T entidad, boolean eraNueva) throws DAOException {
    }
}
