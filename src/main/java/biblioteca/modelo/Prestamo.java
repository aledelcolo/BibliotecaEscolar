package biblioteca.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo extends EntidadBase {

    public static final int DIAS_PRESTAMO = 7;

    private int idEstudiante;
    private int idLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo() {
    }

    public Prestamo(int idEstudiante, int idLibro, LocalDate fechaPrestamo) {
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaPrestamo.plusDays(DIAS_PRESTAMO);
        this.devuelto = false;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

    public LocalDate getFechaVencimiento() {
        if (fechaDevolucion != null) {
            return fechaDevolucion;
        }
        return fechaPrestamo == null ? null : fechaPrestamo.plusDays(DIAS_PRESTAMO);
    }

    public long getDiasAtraso(LocalDate hoy) {
        LocalDate vencimiento = getFechaVencimiento();
        if (devuelto || vencimiento == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(vencimiento, hoy));
    }

    public boolean estaAtrasado(LocalDate hoy) {
        return getDiasAtraso(hoy) > 0;
    }

    public String getEstado(LocalDate hoy) {
        if (devuelto) {
            return "DEVUELTO";
        }
        long atraso = getDiasAtraso(hoy);
        if (atraso > 0) {
            return "ATRASADO (" + atraso + (atraso == 1 ? " día)" : " días)");
        }
        LocalDate vencimiento = getFechaVencimiento();
        if (vencimiento != null && vencimiento.equals(hoy)) {
            return "VENCE HOY";
        }
        return "EN PLAZO";
    }

    @Override
    public String getDescripcion() {
        return "Préstamo #" + id;
    }
}
