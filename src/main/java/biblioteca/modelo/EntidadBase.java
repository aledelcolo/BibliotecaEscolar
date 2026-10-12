package biblioteca.modelo;

public abstract class EntidadBase {

    protected int id;

    protected EntidadBase() {
    }

    protected EntidadBase(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean esNueva() {
        return id == 0;
    }

    public abstract String getDescripcion();

    @Override
    public String toString() {
        return getDescripcion();
    }
}
