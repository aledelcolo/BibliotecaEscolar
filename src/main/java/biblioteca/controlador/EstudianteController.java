package biblioteca.controlador;

import biblioteca.dao.DAOException;
import biblioteca.dao.EstudianteDAO;
import biblioteca.dao.UsuarioDAO;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Rol;
import biblioteca.modelo.Usuario;
import biblioteca.vista.EstudiantePanel;

public class EstudianteController extends CrudController<Estudiante> {

    private final EstudiantePanel panel;
    private final UsuarioDAO usuarioDAO;

    public EstudianteController(EstudiantePanel panel, EstudianteDAO estudianteDAO, UsuarioDAO usuarioDAO) {
        super(panel, estudianteDAO);
        this.panel = panel;
        this.usuarioDAO = usuarioDAO;
    }

    @Override
    protected void despuesDeGuardar(Estudiante estudiante, boolean eraNueva) throws DAOException {
        String clave = panel.getClaveAcceso();
        if (!eraNueva || clave.isEmpty()) {
            return;
        }
        if (usuarioDAO.buscarPorCorreoORut(estudiante.getCorreo(), estudiante.getRut()) != null) {
            return; // ya tiene usuario de acceso
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(estudiante.getNombre());
        usuario.setRut(estudiante.getRut());
        usuario.setCorreo(estudiante.getCorreo());
        usuario.setContrasena(clave);
        usuario.setRol(Rol.ESTUDIANTE);
        usuarioDAO.insertar(usuario);
    }
}
