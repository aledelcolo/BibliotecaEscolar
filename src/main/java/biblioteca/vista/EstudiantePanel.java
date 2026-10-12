package biblioteca.vista;

import biblioteca.modelo.Estudiante;
import biblioteca.util.ValidacionException;
import biblioteca.util.Validador;

import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class EstudiantePanel extends PanelCrud<Estudiante> {

    private static final long serialVersionUID = 1L;

    private final JTextField txtNombre = new JTextField();
    private final JTextField txtRut = new JTextField();
    private final JTextField txtCurso = new JTextField();
    private final JTextField txtCorreo = new JTextField();
    private final JPasswordField txtClave = new JPasswordField();

    public EstudiantePanel() {
        inicializar();
    }

    @Override
    protected String titulo() {
        return "Gestión de estudiantes";
    }

    @Override
    protected String[] columnas() {
        return new String[]{"ID", "Nombre", "RUT", "Curso", "Correo"};
    }

    @Override
    protected Object[] fila(Estudiante e) {
        return new Object[]{e.getId(), e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()};
    }

    @Override
    protected void construirFormulario(JPanel f) {
        Ui.fila(f, "Nombre completo *", txtNombre);
        Ui.fila(f, "RUT * (12.345.678-5)", txtRut);
        Ui.fila(f, "Curso *", txtCurso);
        Ui.fila(f, "Correo *", txtCorreo);
        Ui.fila(f, "Contraseña de acceso (opcional, solo al crear)", txtClave);
    }

    @Override
    protected void cargarFormulario(Estudiante e) {
        txtNombre.setText(e.getNombre());
        txtRut.setText(e.getRut());
        txtCurso.setText(e.getCurso());
        txtCorreo.setText(e.getCorreo());
        txtClave.setText("");
        txtClave.setEnabled(false);
    }

    @Override
    protected void limpiarFormulario() {
        txtNombre.setText("");
        txtRut.setText("");
        txtCurso.setText("");
        txtCorreo.setText("");
        txtClave.setText("");
        txtClave.setEnabled(true);
    }

    @Override
    public Estudiante leerFormulario() throws ValidacionException {
        Estudiante e = new Estudiante();
        e.setId(seleccionado != null ? seleccionado.getId() : 0);
        e.setNombre(Validador.requerido(txtNombre.getText(), "Nombre"));
        e.setRut(Validador.rut(txtRut.getText(), "RUT"));
        e.setCurso(Validador.requerido(txtCurso.getText(), "Curso"));
        e.setCorreo(Validador.correo(txtCorreo.getText(), "Correo"));
        return e;
    }

    public String getClaveAcceso() {
        return new String(txtClave.getPassword());
    }
}
