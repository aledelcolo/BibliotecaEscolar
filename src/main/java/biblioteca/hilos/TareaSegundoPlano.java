package biblioteca.hilos;

import javax.swing.SwingUtilities;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public abstract class TareaSegundoPlano<R> implements Runnable {

    private static final int RETARDO_DEMO_MS = Integer.getInteger("biblioteca.retardo", 1200);

    private final Consumer<R> alExito;
    private final Consumer<Exception> alError;

    protected TareaSegundoPlano(Consumer<R> alExito, Consumer<Exception> alError) {
        this.alExito = alExito;
        this.alError = alError;
    }

    protected abstract R ejecutar() throws Exception;

    @Override
    public final void run() {
        try {
            final R resultado = ejecutar();
            SwingUtilities.invokeLater(() -> alExito.accept(resultado));
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> alError.accept(ex));
        }
    }

    public Thread iniciar(String nombreHilo) {
        Thread hilo = new Thread(this, nombreHilo);
        hilo.start();
        return hilo;
    }

    public static <R> Thread lanzar(String nombreHilo, Callable<R> trabajo,
                                    Consumer<R> alExito, Consumer<Exception> alError) {
        return new TareaSegundoPlano<R>(alExito, alError) {
            @Override
            protected R ejecutar() throws Exception {
                return trabajo.call();
            }
        }.iniciar(nombreHilo);
    }

    protected static void simularLatencia() throws InterruptedException {
        if (RETARDO_DEMO_MS > 0) {
            Thread.sleep(RETARDO_DEMO_MS);
        }
    }
}
