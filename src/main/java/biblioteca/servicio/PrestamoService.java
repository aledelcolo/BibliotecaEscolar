package biblioteca.servicio;

import biblioteca.dao.DAOException;
import biblioteca.dao.LibroDAO;
import biblioteca.dao.PrestamoDAO;
import biblioteca.modelo.Libro;
import biblioteca.modelo.Prestable;
import biblioteca.modelo.Prestamo;
import biblioteca.util.RegistroAtrasos;

import java.time.LocalDate;

public class PrestamoService {

    private final PrestamoDAO prestamoDAO;
    private final LibroDAO libroDAO;

    public PrestamoService(PrestamoDAO prestamoDAO, LibroDAO libroDAO) {
        this.prestamoDAO = prestamoDAO;
        this.libroDAO = libroDAO;
    }

    public synchronized ResultadoPrestamo registrarPrestamo(int idEstudiante, int idLibro)
            throws DAOException, ReglaNegocioException {
        Libro libro = libroDAO.buscarPorId(idLibro);
        if (libro == null) {
            throw new ReglaNegocioException("El libro seleccionado ya no existe.");
        }
        Prestable item = libro;
        if (!item.estaDisponible()) {
            throw new ReglaNegocioException("No hay stock disponible de \"" + libro.getTitulo() + "\".");
        }


        if (!libroDAO.descontarStock(idLibro)) {
            throw new ReglaNegocioException("No hay stock disponible de \"" + libro.getTitulo() + "\".");
        }
        item.reducirStock();


        Prestamo prestamo = new Prestamo(idEstudiante, idLibro, LocalDate.now());
        try {
            prestamoDAO.insertar(prestamo);
        } catch (DAOException e) {
            try {
                libroDAO.reponerStock(idLibro);
                item.aumentarStock();
            } catch (DAOException reversa) {
                e.addSuppressed(reversa);
            }
            throw e;
        }
        return new ResultadoPrestamo(prestamo, libro, 0);
    }

    public synchronized ResultadoPrestamo registrarDevolucion(int idPrestamo)
            throws DAOException, ReglaNegocioException {
        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);
        if (prestamo == null) {
            throw new ReglaNegocioException("El préstamo seleccionado ya no existe.");
        }
        if (prestamo.isDevuelto()) {
            throw new ReglaNegocioException("Este préstamo ya fue devuelto.");
        }

        long diasAtraso = prestamo.getDiasAtraso(LocalDate.now());

        if (!prestamoDAO.marcarDevuelto(idPrestamo)) {
            throw new ReglaNegocioException("Este préstamo ya fue devuelto.");
        }
        try {
            libroDAO.reponerStock(prestamo.getIdLibro());
        } catch (DAOException e) {
            prestamo.setDevuelto(false);
            try {
                prestamoDAO.actualizar(prestamo);
            } catch (DAOException reversa) {
                e.addSuppressed(reversa);
            }
            throw e;
        }
        prestamo.setDevuelto(true);

        if (diasAtraso > 0) {
            RegistroAtrasos.registrar(prestamo.getDescripcion(), diasAtraso);
        }
        return new ResultadoPrestamo(prestamo, libroDAO.buscarPorId(prestamo.getIdLibro()), diasAtraso);
    }
}
