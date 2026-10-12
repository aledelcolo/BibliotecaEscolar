package biblioteca.modelo;

public interface Prestable {

    boolean estaDisponible();

    int getStock();

    void reducirStock();

    void aumentarStock();
}
