package biblioteca.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(texto.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible en esta JVM", e);
        }
    }

    public static boolean verificar(String ingresada, String almacenada) {
        if (ingresada == null || almacenada == null) {
            return false;
        }
        String guardada = almacenada.trim();
        if (MessageDigest.isEqual(guardada.getBytes(StandardCharsets.UTF_8), ingresada.getBytes(StandardCharsets.UTF_8))) {
            return true;
        }
        String hash = sha256(ingresada);
        return MessageDigest.isEqual(guardada.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.UTF_8),
                hash.getBytes(StandardCharsets.UTF_8));
    }
}
