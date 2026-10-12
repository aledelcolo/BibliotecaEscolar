package biblioteca.modelo;

import java.util.Locale;

public enum Rol {

    BIBLIOTECARIO("bibliotecario", "Bibliotecario"),
    ESTUDIANTE("estudiante", "Estudiante");

    private final String valorBD;
    private final String etiqueta;

    Rol(String valorBD, String etiqueta) {
        this.valorBD = valorBD;
        this.etiqueta = etiqueta;
    }

    public String getValorBD() {
        return valorBD;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean puedeGestionar() {
        return this == BIBLIOTECARIO;
    }

    public static Rol desdeTexto(String texto) {
        if (texto != null) {
            String t = texto.trim().toLowerCase(Locale.ROOT);
            if (t.startsWith("biblio") || t.startsWith("admin")) {
                return BIBLIOTECARIO;
            }
        }
        return ESTUDIANTE;
    }
}
