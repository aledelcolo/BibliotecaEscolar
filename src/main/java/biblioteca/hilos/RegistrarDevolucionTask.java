package biblioteca.hilos;

import biblioteca.dao.PrestamoDAO;
import biblioteca.servicio.PrestamoService;
import biblioteca.servicio.ResultadoPrestamo;

import java.util.function.Consumer;

public class RegistrarDevolucionTask extends TareaSegundoPlano<ResultadoPrestamo> {

    private final PrestamoService servicio;
    private final PrestamoDAO prestamoDAO;
    private final int idPrestamo;

    public RegistrarDevolucionTask(PrestamoService servicio, PrestamoDAO prestamoDAO, int idPrestamo,
                                   Consumer<ResultadoPrestamo> alExito, Consumer<Exception> alError) {
        super(alExito, alError);
        this.servicio = servicio;
        this.prestamoDAO = prestamoDAO;
        this.idPrestamo = idPrestamo;
    }

    @Override
    protected ResultadoPrestamo ejecutar() throws Exception {
        simularLatencia();
        ResultadoPrestamo resultado = servicio.registrarDevolucion(idPrestamo);
        resultado.setHistorial(prestamoDAO.listarPorEstudiante(resultado.getPrestamo().getIdEstudiante()));
        return resultado;
    }
}
