package biblioteca.controlador;

import biblioteca.dao.DatabaseConnection;
import biblioteca.dao.ErroresSQL;
import biblioteca.dao.UsuarioDAO;
import biblioteca.hilos.TareaSegundoPlano;
import biblioteca.modelo.Usuario;
import biblioteca.vista.LoginView;

public class LoginController {

    private final LoginView vista;
    private final UsuarioDAO usuarioDAO;

    public LoginController(LoginView vista, UsuarioDAO usuarioDAO) {
        this.vista = vista;
        this.usuarioDAO = usuarioDAO;
        vista.alIngresar(e -> ingresar());
        vista.alSalir(e -> System.exit(0));
    }

    public void iniciar() {
        vista.setVisible(true);
        verificarConexion();
    }

    private void verificarConexion() {
        vista.setEstadoConexion("Conectando con la base de datos...", null);
        TareaSegundoPlano.lanzar("Hilo-Conexion",
                () -> {
                    DatabaseConnection bd = DatabaseConnection.getInstance();
                    bd.getConnection();
                    return bd.getDestino();
                },
                destino -> vista.setEstadoConexion("Conectado a " + destino, true),
                error -> vista.setEstadoConexion(ErroresSQL.traducir(error), false));
    }

    private void ingresar() {
        String identificador = vista.getIdentificador();
        String clave = vista.getClave();
        if (identificador.isEmpty() || clave.isEmpty()) {
            vista.mostrarError("Ingresa tu correo o RUT y tu contraseña.");
            return;
        }
        vista.setOcupado(true);
        TareaSegundoPlano.lanzar("Hilo-Login",
                () -> usuarioDAO.autenticar(identificador, clave),
                usuario -> {
                    vista.setOcupado(false);
                    if (usuario == null) {
                        vista.mostrarError("Credenciales incorrectas. Revisa tu correo/RUT y tu contraseña.");
                    } else {
                        abrirSistema(usuario);
                    }
                },
                error -> {
                    vista.setOcupado(false);
                    vista.mostrarError(ErroresSQL.traducir(error));
                });
    }

    private void abrirSistema(Usuario usuario) {
        vista.dispose();
        new MainController(usuario).iniciar();
    }
}
