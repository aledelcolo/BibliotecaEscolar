package biblioteca.modelo;

public enum TipoReporte {

    MAS_PRESTADOS("Libros más prestados"),
    HISTORIAL_ESTUDIANTE("Historial por estudiante"),
    EN_PRESTAMO("Libros actualmente en préstamo");

    private final String etiqueta;

    TipoReporte(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
