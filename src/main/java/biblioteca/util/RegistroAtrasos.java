package biblioteca.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class RegistroAtrasos {

    private static final Path ARCHIVO = Paths.get("logs", "atrasos.log");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private RegistroAtrasos() {
    }

    public static synchronized void registrar(String detalle, long diasAtraso) {
        String linea = LocalDateTime.now().format(FORMATO) + " | ATRASO de " + diasAtraso + " dia(s) | "
                + detalle + System.lineSeparator();
        try {
            Files.createDirectories(ARCHIVO.getParent());
            Files.write(ARCHIVO, linea.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("No se pudo escribir la constancia de atraso: " + e.getMessage());
        }
    }
}
