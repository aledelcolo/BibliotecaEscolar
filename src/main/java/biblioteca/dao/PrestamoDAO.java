package biblioteca.dao;

import biblioteca.modelo.Prestamo;
import biblioteca.modelo.PrestamoDetalle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PrestamoDAO extends BaseDAO implements GenericDAO<Prestamo> {

    private String selectDetalle() {
        return "SELECT p.id, p.id_estudiante, p.id_libro, p.fecha_prestamo, p.fecha_devolucion, p.devuelto, "
                + "e.nombre AS nombre_estudiante, e.curso AS curso_estudiante, "
                + "l." + Esquema.col("libros", "titulo") + " AS titulo_libro "
                + "FROM prestamos p "
                + "JOIN estudiantes e ON e.id = p.id_estudiante "
                + "JOIN libros l ON l.id = p.id_libro ";
    }

    private PrestamoDetalle mapearDetalle(ResultSet rs) throws SQLException {
        PrestamoDetalle p = new PrestamoDetalle();
        p.setId(rs.getInt("id"));
        p.setIdEstudiante(rs.getInt("id_estudiante"));
        p.setIdLibro(rs.getInt("id_libro"));
        p.setFechaPrestamo(aFecha(rs.getDate("fecha_prestamo")));
        p.setFechaDevolucion(aFecha(rs.getDate("fecha_devolucion")));
        p.setDevuelto(rs.getBoolean("devuelto"));
        p.setNombreEstudiante(rs.getString("nombre_estudiante"));
        p.setCursoEstudiante(rs.getString("curso_estudiante"));
        p.setTituloLibro(rs.getString("titulo_libro"));
        return p;
    }

    @Override
    public void insertar(Prestamo p) throws DAOException {
        p.setId(insertarBD("INSERT INTO prestamos (id_estudiante, id_libro, fecha_prestamo, fecha_devolucion, devuelto) "
                        + "VALUES (?, ?, ?, ?, ?)",
                p.getIdEstudiante(), p.getIdLibro(), p.getFechaPrestamo(), p.getFechaDevolucion(), p.isDevuelto()));
    }

    @Override
    public void actualizar(Prestamo p) throws DAOException {
        exigirFilas(actualizarBD("UPDATE prestamos SET id_estudiante = ?, id_libro = ?, fecha_prestamo = ?, "
                        + "fecha_devolucion = ?, devuelto = ? WHERE id = ?",
                p.getIdEstudiante(), p.getIdLibro(), p.getFechaPrestamo(), p.getFechaDevolucion(),
                p.isDevuelto(), p.getId()));
    }

    @Override
    public void eliminar(int id) throws DAOException {
        exigirFilas(actualizarBD("DELETE FROM prestamos WHERE id = ?", id));
    }

    @Override
    public Prestamo buscarPorId(int id) throws DAOException {
        return this.<Prestamo>consultarUno(selectDetalle() + "WHERE p.id = ?", this::mapearDetalle, id);
    }

    @Override
    public List<Prestamo> listar() throws DAOException {
        return this.<Prestamo>consultar(selectDetalle() + "ORDER BY p.fecha_prestamo DESC, p.id DESC",
                this::mapearDetalle);
    }

    public List<PrestamoDetalle> listarActivos() throws DAOException {
        return consultar(selectDetalle() + "WHERE p.devuelto = FALSE ORDER BY p.fecha_devolucion, p.id",
                this::mapearDetalle);
    }

    public List<PrestamoDetalle> listarActivosPorEstudiante(int idEstudiante) throws DAOException {
        return consultar(selectDetalle() + "WHERE p.devuelto = FALSE AND p.id_estudiante = ? "
                + "ORDER BY p.fecha_devolucion, p.id", this::mapearDetalle, idEstudiante);
    }

    public List<PrestamoDetalle> listarPorEstudiante(int idEstudiante) throws DAOException {
        return consultar(selectDetalle() + "WHERE p.id_estudiante = ? ORDER BY p.fecha_prestamo DESC, p.id DESC",
                this::mapearDetalle, idEstudiante);
    }

    public boolean marcarDevuelto(int idPrestamo) throws DAOException {
        return actualizarBD("UPDATE prestamos SET devuelto = TRUE WHERE id = ? AND devuelto = FALSE", idPrestamo) > 0;
    }
}
