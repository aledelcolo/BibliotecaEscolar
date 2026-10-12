package biblioteca.dao;

import biblioteca.modelo.Estudiante;
import biblioteca.util.Validador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class EstudianteDAO extends BaseDAO implements GenericDAO<Estudiante> {

    private static final String SELECT = "SELECT id, nombre, rut, curso, correo FROM estudiantes ";

    private Estudiante mapear(ResultSet rs) throws SQLException {
        return new Estudiante(rs.getInt("id"), rs.getString("nombre"), rs.getString("rut"),
                rs.getString("curso"), rs.getString("correo"));
    }

    @Override
    public void insertar(Estudiante e) throws DAOException {
        e.setId(insertarBD("INSERT INTO estudiantes (nombre, rut, curso, correo) VALUES (?, ?, ?, ?)",
                e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo()));
    }

    @Override
    public void actualizar(Estudiante e) throws DAOException {
        exigirFilas(actualizarBD("UPDATE estudiantes SET nombre = ?, rut = ?, curso = ?, correo = ? WHERE id = ?",
                e.getNombre(), e.getRut(), e.getCurso(), e.getCorreo(), e.getId()));
    }

    @Override
    public void eliminar(int id) throws DAOException {
        exigirFilas(actualizarBD("DELETE FROM estudiantes WHERE id = ?", id));
    }

    @Override
    public Estudiante buscarPorId(int id) throws DAOException {
        return consultarUno(SELECT + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Estudiante> listar() throws DAOException {
        return consultar(SELECT + "ORDER BY nombre", this::mapear);
    }

    public Estudiante buscarPorRutOCorreo(String rut, String correo) throws DAOException {
        return consultarUno(SELECT + "WHERE correo = ? OR rut = ? OR REPLACE(REPLACE(rut, '.', ''), '-', '') = ?",
                this::mapear, correo, rut, Validador.normalizarRut(rut));
    }
}
