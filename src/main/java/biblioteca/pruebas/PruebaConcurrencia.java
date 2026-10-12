package biblioteca.pruebas;

import biblioteca.dao.CategoriaDAO;
import biblioteca.dao.DatabaseConnection;
import biblioteca.dao.EstudianteDAO;
import biblioteca.dao.LibroDAO;
import biblioteca.dao.PrestamoDAO;
import biblioteca.modelo.Categoria;
import biblioteca.modelo.Estudiante;
import biblioteca.modelo.Libro;
import biblioteca.servicio.PrestamoService;
import biblioteca.servicio.ReglaNegocioException;
import biblioteca.servicio.ResultadoPrestamo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class PruebaConcurrencia {

    private static final int HILOS = 10;
    private static final int STOCK_INICIAL = 3;

    public static void main(String[] args) throws Exception {
        LibroDAO libroDAO = new LibroDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        List<Estudiante> estudiantes = new EstudianteDAO().listar();
        List<Categoria> categorias = new CategoriaDAO().listar();
        if (estudiantes.isEmpty() || categorias.isEmpty()) {
            System.out.println("Faltan datos: ejecuta primero los scripts SQL de creacion y poblado.");
            return;
        }


        PrestamoService servicio = new PrestamoService(prestamoDAO, libroDAO);


        Libro libro = new Libro();
        libro.setTitulo("[PRUEBA] Libro de concurrencia");
        libro.setAutor("Prueba automatica");
        libro.setIsbn("TEST-" + (System.currentTimeMillis() % 1_000_000_000L));
        libro.setEditorial("-");
        libro.setStock(STOCK_INICIAL);
        libro.setIdCategoria(categorias.get(0).getId());
        libroDAO.insertar(libro);

        System.out.println("Libro de prueba creado con stock = " + STOCK_INICIAL);
        System.out.println("Lanzando " + HILOS + " hilos que piden el mismo libro al mismo tiempo...\n");

        AtomicInteger exitosos = new AtomicInteger();
        AtomicInteger rechazados = new AtomicInteger();
        AtomicInteger errores = new AtomicInteger();
        List<ResultadoPrestamo> creados = Collections.synchronizedList(new ArrayList<ResultadoPrestamo>());
        CountDownLatch salida = new CountDownLatch(1);
        List<Thread> hilos = new ArrayList<>();

        try {
            for (int i = 0; i < HILOS; i++) {
                final Estudiante estudiante = estudiantes.get(i % estudiantes.size());
                Thread hilo = new Thread(() -> {
                    String nombre = Thread.currentThread().getName();
                    try {
                        salida.await(); // todos los hilos parten juntos
                        ResultadoPrestamo r = servicio.registrarPrestamo(estudiante.getId(), libro.getId());
                        creados.add(r);
                        exitosos.incrementAndGet();
                        System.out.println("[" + nombre + "] PRESTADO   a " + estudiante.getNombre()
                                + "  -> stock restante: " + r.getLibro().getStock());
                    } catch (ReglaNegocioException e) {
                        rechazados.incrementAndGet();
                        System.out.println("[" + nombre + "] RECHAZADO  (" + e.getMessage() + ")");
                    } catch (Exception e) {
                        errores.incrementAndGet();
                        System.out.println("[" + nombre + "] ERROR      " + e.getMessage());
                    }
                }, "Hilo-" + (i + 1));
                hilos.add(hilo);
                hilo.start();
            }
            salida.countDown();
            for (Thread hilo : hilos) {
                hilo.join();
            }

            int stockFinal = libroDAO.buscarPorId(libro.getId()).getStock();
            System.out.println("\n--------------- RESUMEN ---------------");
            System.out.println("Prestamos exitosos : " + exitosos.get() + " (esperado " + STOCK_INICIAL + ")");
            System.out.println("Rechazados         : " + rechazados.get() + " (esperado " + (HILOS - STOCK_INICIAL) + ")");
            System.out.println("Errores            : " + errores.get() + " (esperado 0)");
            System.out.println("Stock final        : " + stockFinal + " (esperado 0, nunca negativo)");
            boolean correcto = exitosos.get() == STOCK_INICIAL
                    && rechazados.get() == HILOS - STOCK_INICIAL
                    && errores.get() == 0
                    && stockFinal == 0;
            System.out.println(correcto
                    ? "RESULTADO: CORRECTO - el control de stock evito las condiciones de carrera."
                    : "RESULTADO: REVISAR - los numeros no coinciden con lo esperado.");
        } finally {

            for (ResultadoPrestamo r : creados) {
                prestamoDAO.eliminar(r.getPrestamo().getId());
            }
            libroDAO.eliminar(libro.getId());
            DatabaseConnection.getInstance().cerrar();
            System.out.println("(Datos de prueba eliminados)");
        }
    }
}
