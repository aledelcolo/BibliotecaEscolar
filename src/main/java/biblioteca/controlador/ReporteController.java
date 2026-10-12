package biblioteca.controlador;

import biblioteca.dao.DAOException;
import biblioteca.dao.EstudianteDAO;
import biblioteca.dao.PrestamoDAO;
import biblioteca.dao.ReporteDAO;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.PrestamoDetalle;
import biblioteca.modelo.TipoReporte;
import biblioteca.util.Fechas;
import biblioteca.vista.ReportePanel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteController {

    private final ReportePanel vista;
    private final ReporteDAO reporteDAO;
    private final PrestamoDAO prestamoDAO;
    private final EstudianteDAO estudianteDAO;

    public ReporteController(ReportePanel vista, ReporteDAO reporteDAO, PrestamoDAO prestamoDAO,
                             EstudianteDAO estudianteDAO) {
        this.vista = vista;
        this.reporteDAO = reporteDAO;
        this.prestamoDAO = prestamoDAO;
        this.estudianteDAO = estudianteDAO;
        vista.alGenerar(e -> generar());
    }

    public void cargar() {
        try {
            vista.setEstudiantes(estudianteDAO.listar());
        } catch (DAOException ex) {
            vista.mostrarError(ex.getMessage());
            return;
        }
        generar();
    }

    private void generar() {
        TipoReporte tipo = vista.getReporteSeleccionado();
        if (tipo == null) {
            return;
        }
        try {
            LocalDate hoy = LocalDate.now();
            switch (tipo) {
                case MAS_PRESTADOS:
                    vista.setTabla(tipo.toString() + " (top 10)",
                            new String[]{"Posición", "Título", "Autor", "Categoría", "Veces prestado"},
                            reporteDAO.librosMasPrestados(10));
                    break;
                case HISTORIAL_ESTUDIANTE:
                    Estudiante estudiante = vista.getEstudianteSeleccionado();
                    if (estudiante == null) {
                        vista.mostrarError("Selecciona un estudiante para ver su historial.");
                        return;
                    }
                    List<Object[]> historial = new ArrayList<>();
                    for (PrestamoDetalle p : prestamoDAO.listarPorEstudiante(estudiante.getId())) {
                        historial.add(new Object[]{p.getId(), p.getTituloLibro(), Fechas.formatear(p.getFechaPrestamo()),
                                Fechas.formatear(p.getFechaVencimiento()), p.getEstado(hoy)});
                    }
                    vista.setTabla("Historial de " + estudiante.getNombre() + " (" + estudiante.getCurso() + ")",
                            new String[]{"ID", "Libro", "Fecha préstamo", "Vence", "Estado"}, historial);
                    break;
                case EN_PRESTAMO:
                    List<Object[]> vigentes = new ArrayList<>();
                    for (PrestamoDetalle p : prestamoDAO.listarActivos()) {
                        vigentes.add(new Object[]{p.getId(), p.getTituloLibro(), p.getNombreEstudiante(),
                                p.getCursoEstudiante(), Fechas.formatear(p.getFechaPrestamo()),
                                Fechas.formatear(p.getFechaVencimiento()), p.getEstado(hoy)});
                    }
                    vista.setTabla(tipo.toString(),
                            new String[]{"ID", "Libro", "Estudiante", "Curso", "Fecha préstamo", "Vence", "Estado"},
                            vigentes);
                    break;
                default:
                    break;
            }
        } catch (DAOException ex) {
            vista.mostrarError(ex.getMessage());
        }
    }
}
