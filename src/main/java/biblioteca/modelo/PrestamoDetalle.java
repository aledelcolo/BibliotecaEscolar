package biblioteca.modelo;

public class PrestamoDetalle extends Prestamo {

    private String nombreEstudiante;
    private String cursoEstudiante;
    private String tituloLibro;

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getCursoEstudiante() {
        return cursoEstudiante;
    }

    public void setCursoEstudiante(String cursoEstudiante) {
        this.cursoEstudiante = cursoEstudiante;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    @Override
    public String getDescripcion() {
        return "\"" + tituloLibro + "\" prestado a " + nombreEstudiante;
    }
}
