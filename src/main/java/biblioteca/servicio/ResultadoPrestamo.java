package biblioteca.servicio;

import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestamo;
import biblioteca.modelo.PrestamoDetalle;

import java.util.ArrayList;
import java.util.List;

public class ResultadoPrestamo {

    private final Prestamo prestamo;
    private final Libro libro;
    private final long diasAtraso;
    private List<PrestamoDetalle> historial = new ArrayList<>();

    public ResultadoPrestamo(Prestamo prestamo, Libro libro, long diasAtraso) {
        this.prestamo = prestamo;
        this.libro = libro;
        this.diasAtraso = diasAtraso;
    }

    public Prestamo getPrestamo() {
        return prestamo;
    }

    public Libro getLibro() {
        return libro;
    }

    public long getDiasAtraso() {
        return diasAtraso;
    }

    public List<PrestamoDetalle> getHistorial() {
        return historial;
    }

    public void setHistorial(List<PrestamoDetalle> historial) {
        this.historial = historial;
    }
}
