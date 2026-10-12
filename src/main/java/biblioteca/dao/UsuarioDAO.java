package biblioteca.dao;

import biblioteca.modelo.Rol;
import biblioteca.modelo.Usuario;
import biblioteca.util.PasswordUtil;
import biblioteca.util.Validador;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UsuarioDAO extends BaseDAO implements GenericDAO<Usuario> {

    private static final int LARGO_HASH = 64;

    private String colClave() {
        return Esquema.col("usuarios", "contrasena");
    }

    private String select() {
        return "SELECT id, nombre, rut, correo, " + colClave() + " AS contrasena, rol FROM usuarios ";
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        return new Usuario(rs.getInt("id"), rs.getString("nombre"), rs.getString("rut"),
                rs.getString("correo"), rs.getString("contrasena"), Rol.desdeTexto(rs.getString("rol")));
    }

    public Usuario autenticar(String identificador, String claveIngresada) throws DAOException {
        String id = identificador == null ? "" : identificador.trim();
        List<Usuario> candidatos = consultar(
                select() + "WHERE correo = ? OR rut = ? OR REPLACE(REPLACE(rut, '.', ''), '-', '') = ?",
                this::mapear, id, id, Validador.normalizarRut(id));
        for (Usuario u : candidatos) {
            if (u.validarContrasena(claveIngresada)) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarPorCorreoORut(String correo, String rut) throws DAOException {
        return consultarUno(select() + "WHERE correo = ? OR rut = ? OR REPLACE(REPLACE(rut, '.', ''), '-', '') = ?",
                this::mapear, correo, rut, Validador.normalizarRut(rut));
    }

    @Override
    public void insertar(Usuario u) throws DAOException {
        int id = insertarBD("INSERT INTO usuarios (nombre, rut, correo, " + colClave() + ", rol) VALUES (?, ?, ?, ?, ?)",
                u.getNombre(), u.getRut(), u.getCorreo(), almacenar(u.getContrasena()), u.getRol().getValorBD());
        u.setId(id);
    }

    @Override
    public void actualizar(Usuario u) throws DAOException {
        exigirFilas(actualizarBD("UPDATE usuarios SET nombre = ?, rut = ?, correo = ?, rol = ? WHERE id = ?",
                u.getNombre(), u.getRut(), u.getCorreo(), u.getRol().getValorBD(), u.getId()));
    }

    public void cambiarContrasena(int idUsuario, String nuevaContrasena) throws DAOException {
        exigirFilas(actualizarBD("UPDATE usuarios SET " + colClave() + " = ? WHERE id = ?",
                almacenar(nuevaContrasena), idUsuario));
    }

    @Override
    public void eliminar(int id) throws DAOException {
        exigirFilas(actualizarBD("DELETE FROM usuarios WHERE id = ?", id));
    }

    @Override
    public Usuario buscarPorId(int id) throws DAOException {
        return consultarUno(select() + "WHERE id = ?", this::mapear, id);
    }

    @Override
    public List<Usuario> listar() throws DAOException {
        return consultar(select() + "ORDER BY nombre", this::mapear);
    }

    private String almacenar(String contrasenaPlana) {
        return Esquema.tamano("usuarios", "contrasena") >= LARGO_HASH
                ? PasswordUtil.sha256(contrasenaPlana)
                : contrasenaPlana;
    }
}
