package util;

/**
 * Validaciones genéricas reutilizables en toda la aplicación.
 */
public class Validaciones {

    public static boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    public static boolean esCantidadValida(int cantidad) {
        return cantidad > 0;
    }

    public static boolean esStockSuficiente(int stockActual, int cantidadSolicitada) {
        return stockActual >= cantidadSolicitada;
    }

    public static boolean esPrecioValido(double precio) {
        return precio > 0;
    }
}