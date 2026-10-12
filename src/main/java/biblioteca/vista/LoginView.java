package biblioteca.vista;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JTextField txtIdentificador = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();
    private final JButton btnIngresar = Ui.boton("Ingresar", Ui.PRIMARIO);
    private final JButton btnSalir = Ui.boton("Salir", Ui.TEXTO_SUAVE);
    private final JLabel lblError = new JLabel(" ");
    private final JLabel lblConexion = new JLabel(" ");
    private final JProgressBar progreso = new JProgressBar();

    public LoginView() {
        super("Biblioteca Escolar - Inicio de sesión");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(440, 540);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel raiz = new JPanel(new BorderLayout());
        setContentPane(raiz);


        JPanel cabecera = new JPanel(new GridLayout(2, 1, 0, 4));
        cabecera.setBackground(Ui.PRIMARIO);
        cabecera.setBorder(new EmptyBorder(24, 28, 24, 28));
        JLabel titulo = new JLabel("Biblioteca Escolar");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Ui.fuente(Font.BOLD, 26f));
        JLabel subtitulo = new JLabel("Sistema de gestión de préstamos");
        subtitulo.setForeground(new Color(0xBBDEFB));
        cabecera.add(titulo);
        cabecera.add(subtitulo);
        raiz.add(cabecera, BorderLayout.NORTH);


        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(new EmptyBorder(26, 32, 6, 32));
        Ui.fila(formulario, "Correo o RUT", txtIdentificador);
        Ui.fila(formulario, "Contraseña", txtClave);
        Ui.agregarRelleno(formulario);
        raiz.add(formulario, BorderLayout.CENTER);


        JPanel inferior = new JPanel();
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));
        inferior.setBorder(new EmptyBorder(0, 32, 20, 32));
        lblError.setForeground(Ui.PELIGRO);
        lblError.setAlignmentX(Component.LEFT_ALIGNMENT);
        progreso.setAlignmentX(Component.LEFT_ALIGNMENT);
        progreso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
        JPanel botones = new JPanel(new GridLayout(1, 2, 10, 0));
        botones.add(btnIngresar);
        botones.add(btnSalir);
        botones.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblConexion.setAlignmentX(Component.LEFT_ALIGNMENT);
        inferior.add(lblError);
        inferior.add(Box.createVerticalStrut(8));
        inferior.add(progreso);
        inferior.add(Box.createVerticalStrut(12));
        inferior.add(botones);
        inferior.add(Box.createVerticalStrut(16));
        inferior.add(lblConexion);
        raiz.add(inferior, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnIngresar);
    }

    public String getIdentificador() {
        return txtIdentificador.getText().trim();
    }

    public String getClave() {
        return new String(txtClave.getPassword());
    }

    public void alIngresar(ActionListener listener) {
        btnIngresar.addActionListener(listener);
    }

    public void alSalir(ActionListener listener) {
        btnSalir.addActionListener(listener);
    }

    public void setOcupado(boolean ocupado) {
        btnIngresar.setEnabled(!ocupado);
        txtIdentificador.setEnabled(!ocupado);
        txtClave.setEnabled(!ocupado);
        progreso.setIndeterminate(ocupado);
        if (ocupado) {
            lblError.setText(" ");
        }
    }

    public void mostrarError(String mensaje) {
        lblError.setText(Ui.html(mensaje, 340));
        txtClave.setText("");
        txtClave.requestFocusInWindow();
    }

    public void setEstadoConexion(String texto, Boolean ok) {
        lblConexion.setForeground(ok == null ? Ui.TEXTO_SUAVE : (ok ? Ui.EXITO : Ui.PELIGRO));
        lblConexion.setText(Ui.html("● " + texto, 340));
    }
}
