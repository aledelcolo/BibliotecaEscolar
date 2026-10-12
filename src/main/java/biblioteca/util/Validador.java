package biblioteca.util;

import java.util.regex.Pattern;

public final class Validador {

    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern RUT = Pattern.compile("^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$");

    private Validador() {
    }

    public static String requerido(String valor, String campo) throws ValidacionException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo \"" + campo + "\" es obligatorio.");
        }
        return valor.trim();
    }

    public static String correo(String valor, String campo) throws ValidacionException {
        String limpio = requerido(valor, campo);
        if (!CORREO.matcher(limpio).matches()) {
            throw new ValidacionException("El campo \"" + campo + "\" no tiene un formato de correo válido.");
        }
        return limpio;
    }

    public static String rut(String valor, String campo) throws ValidacionException {
        String limpio = requerido(valor, campo);
        if (!RUT.matcher(limpio).matches()) {
            throw new ValidacionException("El RUT debe tener el formato 12.345.678-5 (o 12345678-5).");
        }
        return limpio;
    }

    public static String normalizarRut(String rut) {
        return rut == null ? "" : rut.replace(".", "").replace("-", "").replace(" ", "").toUpperCase();
    }
}
