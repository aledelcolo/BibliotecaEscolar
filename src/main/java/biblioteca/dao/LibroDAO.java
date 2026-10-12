package biblioteca.dao;

import biblioteca.modelo.Libro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class LibroDAO extends BaseDAO implements GenericDAO<Libro> {

    private String select() {
        return "SELECT l.id, l." + Esquema.col("libros", "titulo") + " AS titulo, l.autor, l.isbn, l.editorial, "
                + "l.stock, l.id_categoria, c.nombre AS categoria "
                + "FROM libros l LEFT JOIN categorias c ON c.id = l.id_categoria ";
    }

    private Libro mapear(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setId(rs.getInt("id"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setIsbn(rs.getString("isbn"));
        l.setEditorial(rs.getString("editorial"));
        l.setStock(rs.getInt("stock"));
        l.setIdCategoria(rs.getInt("id_categoria"));
        l.setNombreCategoria(rs.getString("categoria"));
        return l;
    }

    @Override
    public void insertar(Libro l) throws DAOException {
        l.setId(insertarBD("INSERT INTO libros (" + Esquema.col("libros", "titulo")
                        + ", autor, isbn, editorial, stock, id_categoria) VALUES (?, ?, ?, ?, ?, ?)",
                l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(), l.getStock(), l.getIdCategoria()));
    }

    @Override
    public synchronized void actualizar(Libro l) throws DAOException {
        exigirFilas(actualizarBD("UPDATE libros SET " + Esquema.col("libros", "titulo")
                        + " = ?, autor = ?, isbn = ?, editorial = ?, stock = ?, id_categoria = ? WHERE id = ?",
                l.getTitulo(), l.getAutor(), l.getIsbn(), l.getEditorial(), l.getStock(),
                l.getIdCategoria(), l.getId()));
    }

    @Override
    public void eliminar(int id) throws DAOException {
        exigirFilas(actualizarBD("DELETE FROM libros WHERE id = ?", id));
    }

    @Override
    public Libro buscarPorId(int id) throws DAOException {
        return consultarUno(select() + "WHERE l.id = ?", this::mapear, id);
    }

    @Override
    public List<Libro> listar() throws DAOException {
        return consultar(select() + "ORDER BY titulo", this::mapear);
    }

    public synchronized boolean descontarStock(int idLibro) throws DAOException {
        return actualizarBD("UPDATE libros SET stock = stock - 1 WHERE id = ? AND stock > 0", idLibro) > 0;
    }

    public synchronized void reponerStock(int idLibro) throws DAOException {
        exigirFilas(actualizarBD("UPDATE libros SET stock = stock + 1 WHERE id = ?", idLibro));
    }
}
