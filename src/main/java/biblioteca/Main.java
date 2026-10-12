package biblioteca;

import biblioteca.controlador.LoginController;
import biblioteca.dao.DatabaseConnection;
import biblioteca.dao.UsuarioDAO;
import biblioteca.vista.LoginView;
import biblioteca.vista.Ui;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        Ui.aplicarLookAndFeel();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseConnection.getInstance().cerrar()));


        SwingUtilities.invokeLater(() -> new LoginController(new LoginView(), new UsuarioDAO()).iniciar());
    }
}
