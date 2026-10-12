package biblioteca.dao;

import java.util.ArrayList;
import java.util.List;

public class ReporteDAO extends BaseDAO {

    public List<Object[]> librosMasPrestados(int limite) throws DAOException {
        String titulo = Esquema.col("libros", "titulo");
        String sql = "SELECT l." + titulo + " AS titulo, l.autor AS autor, COALESCE(c.nombre, '-') AS categoria, "
                + "COUNT(p.id) AS total "
                + "FROM prestamos p "
                + "JOIN libros l ON l.id = p.id_libro "
                + "LEFT JOIN categorias c ON c.id = l.id_categoria "
                + "GROUP BY l.id, l." + titulo + ", l.autor, c.nombre "
                + "ORDER BY total DESC, titulo ASC LIMIT ?";
        List<Object[]> filas = consultar(sql, rs -> new Object[]{
                rs.getString("titulo"), rs.getString("autor"), rs.getString("categoria"), rs.getInt("total")
        }, limite);

        List<Object[]> numeradas = new ArrayList<>();
        int posicion = 1;
        for (Object[] f : filas) {
            numeradas.add(new Object[]{posicion++, f[0], f[1], f[2], f[3]});
        }
        return numeradas;
    }
}
