package biblioteca.modelo;

public class Libro extends EntidadBase implements Prestable {

    private String titulo;
    private String autor;
    private String isbn;
    private String editorial;
    private int stock;
    private int idCategoria;
    private String nombreCategoria;

    public Libro() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    @Override
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    @Override
    public boolean estaDisponible() {
        return stock > 0;
    }

    @Override
    public void reducirStock() {
        if (!estaDisponible()) {
            throw new IllegalStateException("No hay stock disponible de \"" + titulo + "\".");
        }
        stock--;
    }

    @Override
    public void aumentarStock() {
        stock++;
    }

    @Override
    public String getDescripcion() {
        return titulo + " - " + autor + " (stock: " + stock + ")";
    }
}
