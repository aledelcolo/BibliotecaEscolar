package biblioteca.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Esquema {

    private static final Map<String, String> NOMBRES = new HashMap<>();
    private static final Map<String, Integer> TAMANOS = new HashMap<>();

    private Esquema() {
    }

    public static synchronized String col(String tabla, String logico) {
        String clave = tabla + "." + logico;
        if (!NOMBRES.containsKey(clave)) {
            resolver(tabla, logico);
        }
        String real = NOMBRES.get(clave);
        return real != null ? real : "`" + logico + "`";
    }

    public static synchronized int tamano(String tabla, String logico) {
        String clave = tabla + "." + logico;
        if (!TAMANOS.containsKey(clave)) {
            resolver(tabla, logico);
        }
        Integer tamano = TAMANOS.get(clave);
        return tamano != null ? tamano : -1;
    }

    private static void resolver(String tabla, String logico) {
        try {
            Connection con = DatabaseConnection.getInstance().getConnection();
            DatabaseMetaData meta = con.getMetaData();
            String objetivo = normalizar(logico);
            try (ResultSet rs = meta.getColumns(con.getCatalog(), null, tabla, "%")) {
                while (rs.next()) {
                    String nombre = rs.getString("COLUMN_NAME");
                    if (normalizar(nombre).equals(objetivo)) {
                        String clave = tabla + "." + logico;
                        NOMBRES.put(clave, "`" + nombre + "`");
                        TAMANOS.put(clave, rs.getInt("COLUMN_SIZE"));
                        return;
                    }
                }
            }
        } catch (SQLException e) {

        }
    }

    static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
