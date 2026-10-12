package biblioteca.hilos;

import biblioteca.dao.PrestamoDAO;
import biblioteca.servicio.PrestamoService;
import biblioteca.servicio.ResultadoPrestamo;

import java.util.function.Consumer;

public class RegistrarPrestamoTask extends TareaSegundoPlano<ResultadoPrestamo> {

    private final PrestamoService servicio;
    private final PrestamoDAO prestamoDAO;
    private final int idEstudiante;
    private final int idLibro;

    public RegistrarPrestamoTask(PrestamoService servicio, PrestamoDAO prestamoDAO, int idEstudiante, int idLibro,
                                 Consumer<ResultadoPrestamo> alExito, Consumer<Exception> alError) {
        super(alExito, alError);
        this.servicio = servicio;
        this.prestamoDAO = prestamoDAO;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
    }

    @Override
    protected ResultadoPrestamo ejecutar() throws Exception {
        simularLatencia();
        ResultadoPrestamo resultado = servicio.registrarPrestamo(idEstudiante, idLibro);
        resultado.setHistorial(prestamoDAO.listarPorEstudiante(idEstudiante));
        return resultado;
    }
}
