package biblioteca.dao;

import java.sql.SQLException;

public final class ErroresSQL {

    private ErroresSQL() {
    }

    public static String traducir(Throwable error) {
        SQLException sql = buscarSQLException(error);
        if (sql == null) {
            return error.getMessage() != null ? error.getMessage() : error.toString();
        }
        String estado = sql.getSQLState() == null ? "" : sql.getSQLState();
        if ("DRIVER".equals(estado)) {
            return sql.getMessage();
        }
        switch (sql.getErrorCode()) {
            case 1062:
                return "Ya existe un registro con ese valor único (por ejemplo, un RUT, correo o ISBN repetido).";
            case 1451:
                return "No se puede eliminar: el registro está relacionado con otros datos (por ejemplo, préstamos).";
            case 1452:
                return "Referencia inválida: el registro relacionado no existe.";
            case 1045:
                return "Acceso denegado a MySQL: revisa el usuario y la contraseña en db.properties.";
            case 1049:
                return "La base de datos no existe: ejecuta los scripts SQL y revisa db.url en db.properties.";
            case 1146:
                return "Falta una tabla en la base de datos: ejecuta los scripts SQL de creación y poblado.";
            case 1054:
                return "Una columna no coincide con la base de datos: " + sql.getMessage();
            case 1406:
                return "Un dato es demasiado largo para la columna de la base de datos.";
            default:
                break;
        }
        if (estado.startsWith("08")) {
            return "No se pudo conectar con MySQL: verifica que el servidor esté encendido y que db.url sea correcto.";
        }
        return "Error de base de datos: " + sql.getMessage();
    }

    private static SQLException buscarSQLException(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException) {
                return (SQLException) t;
            }
            t = t.getCause();
        }
        return null;
    }
}
