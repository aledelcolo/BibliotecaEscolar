package biblioteca.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAO {

    @FunctionalInterface
    protected interface Mapeador<R> {
        R mapear(ResultSet rs) throws SQLException;
    }

    protected Connection conexion() throws DAOException {
        try {
            return DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new DAOException(ErroresSQL.traducir(e), e);
        }
    }

    protected <R> List<R> consultar(String sql, Mapeador<R> mapeador, Object... parametros) throws DAOException {
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                List<R> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new DAOException(ErroresSQL.traducir(e), e);
        }
    }

    protected <R> R consultarUno(String sql, Mapeador<R> mapeador, Object... parametros) throws DAOException {
        List<R> lista = consultar(sql, mapeador, parametros);
        return lista.isEmpty() ? null : lista.get(0);
    }

    protected int actualizarBD(String sql, Object... parametros) throws DAOException {
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            asignar(ps, parametros);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(ErroresSQL.traducir(e), e);
        }
    }

    protected int insertarBD(String sql, Object... parametros) throws DAOException {
        try (PreparedStatement ps = conexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, parametros);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                return claves.next() ? claves.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DAOException(ErroresSQL.traducir(e), e);
        }
    }

    protected void exigirFilas(int filasAfectadas) throws DAOException {
        if (filasAfectadas == 0) {
            throw new DAOException("No se encontró el registro (pudo haber sido eliminado por otro usuario).");
        }
    }

    protected static LocalDate aFecha(Date fecha) {
        return fecha == null ? null : fecha.toLocalDate();
    }

    private static void asignar(PreparedStatement ps, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            Object p = parametros[i];
            if (p instanceof LocalDate) {
                ps.setDate(i + 1, Date.valueOf((LocalDate) p));
            } else {
                ps.setObject(i + 1, p);
            }
        }
    }
}
