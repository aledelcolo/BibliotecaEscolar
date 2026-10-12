package biblioteca.controlador;

import biblioteca.dao.DAOException;
import biblioteca.dao.ErroresSQL;
import biblioteca.dao.EstudianteDAO;
import biblioteca.dao.LibroDAO;
import biblioteca.dao.PrestamoDAO;
import biblioteca.hilos.RegistrarDevolucionTask;
import biblioteca.hilos.RegistrarPrestamoTask;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.modelo.PrestamoDetalle;
import biblioteca.modelo.Usuario;
import biblioteca.servicio.PrestamoService;
import biblioteca.servicio.ResultadoPrestamo;
import biblioteca.util.Fechas;
import biblioteca.vista.PrestamoPanel;

import java.util.Collections;
import java.util.List;

public class PrestamoController {

    private final PrestamoPanel vista;
    private final PrestamoService servicio;
    private final EstudianteDAO estudianteDAO;
    private final LibroDAO libroDAO;
    private final PrestamoDAO prestamoDAO;
    private final Usuario usuario;
    private Estudiante estudianteSesion;

    public PrestamoController(PrestamoPanel vista, PrestamoService servicio, EstudianteDAO estudianteDAO,
                              LibroDAO libroDAO, PrestamoDAO prestamoDAO, Usuario usuario) {
        this.vista = vista;
        this.servicio = servicio;
        this.estudianteDAO = estudianteDAO;
        this.libroDAO = libroDAO;
        this.prestamoDAO = prestamoDAO;
        this.usuario = usuario;
        vista.alPrestar(e -> prestar());
        vista.alDevolver(e -> devolver());
        vista.alActualizar(e -> cargar());
        vista.alCambiarEstudiante(e -> cargarHistorial());
    }

    public void cargar() {
        try {
            vista.desbloquear();
            List<Estudiante> estudiantes;
            if (usuario.esBibliotecario()) {
                estudiantes = estudianteDAO.listar();
            } else {
                estudianteSesion = estudianteDAO.buscarPorRutOCorreo(usuario.getRut(), usuario.getCorreo());
                estudiantes = estudianteSesion == null
                        ? Collections.<Estudiante>emptyList()
                        : Collections.singletonList(estudianteSesion);
                vista.setModoEstudiante(true);
            }
            vista.setEstudiantes(estudiantes);
            recargarLibros();
            recargarActivos();
            cargarHistorial();
            if (!usuario.esBibliotecario() && estudianteSesion == null) {
                vista.bloquear("Tu usuario no está vinculado a ningún estudiante (debe coincidir el RUT o el correo). "
                        + "Pide a la biblioteca que te registre como estudiante.");
            }
        } catch (DAOException ex) {
            vista.mostrarMensaje(ex.getMessage(), true);
        }
    }

    private void recargarLibros() throws DAOException {
        vista.setLibros(libroDAO.listar());
    }

    private void recargarActivos() throws DAOException {
        List<PrestamoDetalle> lista;
        if (usuario.esBibliotecario()) {
            lista = prestamoDAO.listarActivos();
        } else if (estudianteSesion != null) {
            lista = prestamoDAO.listarActivosPorEstudiante(estudianteSesion.getId());
        } else {
            lista = Collections.<PrestamoDetalle>emptyList();
        }
        vista.setPrestamosActivos(lista);
    }

    private void cargarHistorial() {
        Estudiante estudiante = vista.getEstudianteSeleccionado();
        try {
            if (estudiante == null) {
                vista.setHistorial(Collections.<PrestamoDetalle>emptyList(), null);
            } else {
                vista.setHistorial(prestamoDAO.listarPorEstudiante(estudiante.getId()), estudiante.getNombre());
            }
        } catch (DAOException ex) {
            vista.mostrarMensaje(ex.getMessage(), true);
        }
    }

    private void refrescarListas() {
        try {
            recargarLibros();
            recargarActivos();
        } catch (DAOException ex) {
            vista.mostrarMensaje(ex.getMessage(), true);
        }
    }




    private void prestar() {
        Estudiante estudiante = vista.getEstudianteSeleccionado();
        Libro libro = vista.getLibroSeleccionado();
        if (estudiante == null) {
            vista.mostrarMensaje("Selecciona un estudiante.", true);
            return;
        }
        if (libro == null) {
            vista.mostrarMensaje("Selecciona un libro.", true);
            return;
        }
        vista.setOcupado(true, "Procesando el préstamo en segundo plano...");
        new RegistrarPrestamoTask(servicio, prestamoDAO, estudiante.getId(), libro.getId(),
                resultado -> prestamoRegistrado(resultado, estudiante),
                this::operacionFallida).iniciar("Hilo-Prestamo");
    }

    private void prestamoRegistrado(ResultadoPrestamo resultado, Estudiante estudiante) {
        vista.setOcupado(false, null);
        vista.mostrarMensaje("Préstamo registrado: \"" + resultado.getLibro().getTitulo() + "\" para "
                + estudiante.getNombre() + ". Vence el "
                + Fechas.formatear(resultado.getPrestamo().getFechaVencimiento())
                + ". Stock restante: " + resultado.getLibro().getStock() + ".", false);
        vista.setHistorial(resultado.getHistorial(), estudiante.getNombre());
        refrescarListas();
    }




    private void devolver() {
        PrestamoDetalle prestamo = vista.getPrestamoActivoSeleccionado();
        if (prestamo == null) {
            vista.mostrarMensaje("Selecciona en la tabla el préstamo que se devuelve.", true);
            return;
        }
        vista.setOcupado(true, "Registrando la devolución en segundo plano...");
        new RegistrarDevolucionTask(servicio, prestamoDAO, prestamo.getId(),
                resultado -> devolucionRegistrada(resultado, prestamo),
                this::operacionFallida).iniciar("Hilo-Devolucion");
    }

    private void devolucionRegistrada(ResultadoPrestamo resultado, PrestamoDetalle prestamo) {
        vista.setOcupado(false, null);
        long atraso = resultado.getDiasAtraso();
        if (atraso > 0) {
            vista.mostrarMensaje("Devolución registrada CON ATRASO de " + atraso + " día(s).", true);
            vista.avisarAtraso("El libro \"" + prestamo.getTituloLibro() + "\" se devolvió con " + atraso
                    + (atraso == 1 ? " día" : " días") + " de atraso (vencía el "
                    + Fechas.formatear(prestamo.getFechaVencimiento()) + ").\n"
                    + "Se dejó constancia del atraso en logs/atrasos.log.");
        } else {
            String stock = resultado.getLibro() != null ? " Stock actualizado: " + resultado.getLibro().getStock() + "." : "";
            vista.mostrarMensaje("Devolución registrada: \"" + prestamo.getTituloLibro() + "\"." + stock, false);
        }
        Estudiante seleccionado = vista.getEstudianteSeleccionado();
        if (seleccionado != null && seleccionado.getId() == prestamo.getIdEstudiante()) {
            vista.setHistorial(resultado.getHistorial(), seleccionado.getNombre());
        }
        refrescarListas();
    }

    private void operacionFallida(Exception ex) {
        vista.setOcupado(false, null);
        vista.mostrarMensaje(ErroresSQL.traducir(ex), true);
        refrescarListas();
    }
}
