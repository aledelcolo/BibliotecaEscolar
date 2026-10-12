package biblioteca.vista;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JPanel menu = new JPanel();
    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final JButton btnCerrarSesion = Ui.boton("Cerrar sesión", Ui.PELIGRO);
    private final Map<String, JButton> botones = new LinkedHashMap<>();

    public MainView(String nombreUsuario, String rol) {
        super("Sistema de Gestión de Biblioteca Escolar");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1220, 760);
        setMinimumSize(new Dimension(1060, 650));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        setContentPane(raiz);


        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Ui.PRIMARIO);
        barra.setBorder(new EmptyBorder(10, 20, 10, 20));
        JLabel titulo = new JLabel("Biblioteca Escolar");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(Ui.fuente(Font.BOLD, 20f));
        JLabel lblUsuario = new JLabel(nombreUsuario + "   |   " + rol);
        lblUsuario.setForeground(Color.WHITE);
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        derecha.setOpaque(false);
        derecha.add(lblUsuario);
        derecha.add(btnCerrarSesion);
        barra.add(titulo, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);
        raiz.add(barra, BorderLayout.NORTH);


        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBackground(Ui.LATERAL);
        menu.setBorder(new EmptyBorder(16, 0, 16, 0));
        menu.setPreferredSize(new Dimension(230, 0));
        raiz.add(menu, BorderLayout.WEST);


        contenido.setBackground(Ui.FONDO);
        raiz.add(contenido, BorderLayout.CENTER);
    }

    public JButton agregarModulo(String id, String texto, JComponent panel) {
        JButton boton = new JButton(texto);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setForeground(Color.WHITE);
        boton.setBackground(Ui.LATERAL);
        boton.setFont(Ui.fuente(Font.PLAIN, 14f));
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setOpaque(true);
        boton.setBorder(BorderFactory.createEmptyBorder(13, 24, 13, 12));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> mostrarModulo(id));
        menu.add(boton);
        botones.put(id, boton);
        contenido.add(panel, id);
        return boton;
    }

    public void mostrarModulo(String id) {
        cartas.show(contenido, id);
        for (Map.Entry<String, JButton> entrada : botones.entrySet()) {
            entrada.getValue().setBackground(entrada.getKey().equals(id) ? Ui.ACENTO : Ui.LATERAL);
        }
    }

    public void alCerrarSesion(ActionListener listener) {
        btnCerrarSesion.addActionListener(listener);
    }
}
