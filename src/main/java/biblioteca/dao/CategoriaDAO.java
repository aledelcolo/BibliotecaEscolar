package biblioteca.dao;

import biblioteca.modelo.Categoria;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoriaDAO extends BaseDAO implements GenericDAO<Categoria> {

    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt("id"), rs.getString("nombre"));
    }

    @Override
    public void insertar(Categoria categoria) throws DAOException {
        categoria.setId(insertarBD("INSERT INTO categorias (nombre) VALUES (?)", categoria.getNombre()));
    }

    @Override
    public void actualizar(Categoria categoria) throws DAOException {
        exigirFilas(actualizarBD("UPDATE categorias SET nombre = ? WHERE id = ?",
                categoria.getNombre(), categoria.getId()));
    }

    @Override
    public void eliminar(int id) throws DAOException {
        exigirFilas(actualizarBD("DELETE FROM categorias WHERE id = ?", id));
    }

    @Override
    public Categoria buscarPorId(int id) throws DAOException {
        return consultarUno("SELECT id, nombre FROM categorias WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Categoria> listar() throws DAOException {
        return consultar("SELECT id, nombre FROM categorias ORDER BY nombre", this::mapear);
    }
}
