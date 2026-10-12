package biblioteca.controlador;

import biblioteca.dao.CategoriaDAO;
import biblioteca.dao.EstudianteDAO;
import biblioteca.dao.LibroDAO;
import biblioteca.dao.PrestamoDAO;
import biblioteca.dao.ReporteDAO;
import biblioteca.dao.UsuarioDAO;
import biblioteca.modelo.Usuario;
import biblioteca.servicio.PrestamoService;
import biblioteca.vista.CategoriaPanel;
import biblioteca.vista.EstudiantePanel;
import biblioteca.vista.LibroPanel;
import biblioteca.vista.LoginView;
import biblioteca.vista.MainView;
import biblioteca.vista.PrestamoPanel;
import biblioteca.vista.ReportePanel;

import javax.swing.JButton;
import javax.swing.JComponent;

public class MainController {

    private final Usuario usuario;
    private MainView vista;

    public MainController(Usuario usuario) {
        this.usuario = usuario;
    }

    public void iniciar() {
        vista = new MainView(usuario.getNombre(), usuario.getRol().getEtiqueta());


        LibroDAO libroDAO = new LibroDAO();
        CategoriaDAO categoriaDAO = new CategoriaDAO();
        EstudianteDAO estudianteDAO = new EstudianteDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        ReporteDAO reporteDAO = new ReporteDAO();
        PrestamoService servicioPrestamos = new PrestamoService(prestamoDAO, libroDAO);

        boolean gestor = usuario.esBibliotecario();


        LibroPanel libroPanel = new LibroPanel();
        libroPanel.setSoloLectura(!gestor);
        LibroController libroController = new LibroController(libroPanel, libroDAO, categoriaDAO);
        agregarModulo("libros", gestor ? "Gestión de libros" : "Consulta de libros", libroPanel, libroController::cargar);

        if (gestor) {
            EstudiantePanel estudiantePanel = new EstudiantePanel();
            EstudianteController estudianteController =
                    new EstudianteController(estudiantePanel, estudianteDAO, usuarioDAO);
            agregarModulo("estudiantes", "Gestión de estudiantes", estudiantePanel, estudianteController::cargar);

            CategoriaPanel categoriaPanel = new CategoriaPanel();
            CategoriaController categoriaController = new CategoriaController(categoriaPanel, categoriaDAO);
            agregarModulo("categorias", "Categorías", categoriaPanel, categoriaController::cargar);
        }

        PrestamoPanel prestamoPanel = new PrestamoPanel();
        PrestamoController prestamoController = new PrestamoController(prestamoPanel, servicioPrestamos,
                estudianteDAO, libroDAO, prestamoDAO, usuario);
        agregarModulo("prestamos", "Préstamos y devoluciones", prestamoPanel, prestamoController::cargar);

        if (gestor) {
            ReportePanel reportePanel = new ReportePanel();
            ReporteController reporteController =
                    new ReporteController(reportePanel, reporteDAO, prestamoDAO, estudianteDAO);
            agregarModulo("reportes", "Reportes", reportePanel, reporteController::cargar);
        }

        vista.alCerrarSesion(e -> cerrarSesion());
        vista.mostrarModulo("libros");
        libroController.cargar();
        vista.setVisible(true);
    }

    private void agregarModulo(String id, String titulo, JComponent panel, Runnable alMostrar) {
        JButton boton = vista.agregarModulo(id, titulo, panel);
        boton.addActionListener(e -> alMostrar.run());
    }

    private void cerrarSesion() {
        vista.dispose();
        new LoginController(new LoginView(), new UsuarioDAO()).iniciar();
    }
}
