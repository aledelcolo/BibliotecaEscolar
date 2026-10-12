package biblioteca.modelo;

public abstract class Persona extends EntidadBase {

    protected String nombre;
    protected String rut;
    protected String correo;

    protected Persona() {
    }

    protected Persona(int id, String nombre, String rut, String correo) {
        super(id);
        this.nombre = nombre;
        this.rut = rut;
        this.correo = correo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public abstract String getTipo();

    @Override
    public String getDescripcion() {
        return nombre + " (" + rut + ")";
    }
}
