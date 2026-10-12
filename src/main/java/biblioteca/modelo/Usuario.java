package biblioteca.modelo;

import biblioteca.util.PasswordUtil;

public class Usuario extends Persona {

    private String contrasena;
    private Rol rol = Rol.ESTUDIANTE;

    public Usuario() {
    }

    public Usuario(int id, String nombre, String rut, String correo, String contrasena, Rol rol) {
        super(id, nombre, rut, correo);
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public boolean validarContrasena(String ingresada) {
        return PasswordUtil.verificar(ingresada, contrasena);
    }

    public boolean esBibliotecario() {
        return rol.puedeGestionar();
    }

    @Override
    public String getTipo() {
        return "Usuario";
    }

    @Override
    public String getDescripcion() {
        return nombre + " - " + rol.getEtiqueta();
    }
}
