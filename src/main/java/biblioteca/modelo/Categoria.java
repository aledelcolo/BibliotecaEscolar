package biblioteca.modelo;

public class Categoria extends EntidadBase {

    private String nombre;

    public Categoria() {
    }

    public Categoria(int id, String nombre) {
        super(id);
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getDescripcion() {
        return nombre;
    }
}
