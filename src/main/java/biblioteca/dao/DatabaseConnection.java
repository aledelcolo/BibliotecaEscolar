package biblioteca.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection {

    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String URL_POR_DEFECTO = "jdbc:mysql://localhost:3306/biblioteca_escolar"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago"
            + "&useUnicode=true&characterEncoding=UTF-8&connectTimeout=5000";

    private static DatabaseConnection instance;

    private final String url;
    private final String usuario;
    private final String clave;

    private Connection connection;

    private DatabaseConnection() {
        Properties cfg = cargarConfiguracion();
        this.url = valor("DB_URL", cfg, "db.url", URL_POR_DEFECTO);
        this.usuario = valor("DB_USER", cfg, "db.user", "root");
        this.clave = valor("DB_PASSWORD", cfg, "db.password", "Colocolo91.");
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = abrirConexion();
        }
        return connection;
    }

    public synchronized void cerrar() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignorada) {

            } finally {
                connection = null;
            }
        }
    }

    public String getDestino() {
        int corte = url.indexOf('?');
        return corte < 0 ? url : url.substring(0, corte);
    }

    private Connection abrirConexion() throws SQLException {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver MySQL Connector/J. Agrega la dependencia "
                    + "'mysql-connector-j' en pom.xml (o el .jar en las librerías del proyecto).", "DRIVER");
        }
        return DriverManager.getConnection(url, usuario, clave);
    }

    private static Properties cargarConfiguracion() {
        Properties cfg = new Properties();
        Path externo = Paths.get("db.properties");
        try {
            if (Files.exists(externo)) {
                try (InputStream in = Files.newInputStream(externo);
                     InputStreamReader lector = new InputStreamReader(in, StandardCharsets.UTF_8)) {
                    cfg.load(lector);
                }
            } else {
                try (InputStream in = DatabaseConnection.class.getResourceAsStream("/db.properties")) {
                    if (in != null) {
                        cfg.load(new InputStreamReader(in, StandardCharsets.UTF_8));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo leer db.properties; se usarán valores por defecto: " + e.getMessage());
        }
        return cfg;
    }

    private static String valor(String variableEntorno, Properties cfg, String clave, String porDefecto) {
        String entorno = System.getenv(variableEntorno);
        if (entorno != null && !entorno.isEmpty()) {
            return entorno;
        }
        return cfg.getProperty(clave, porDefecto).trim();
    }
}
