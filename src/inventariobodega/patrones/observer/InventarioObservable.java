package patrones.observer;

import java.util.ArrayList;
import java.util.List;
import modelo.Producto;

/**
 * Venta (a través de VentaService) extiende o usa esta clase
 * para avisar a todos los observadores registrados (ej. InventarioService)
 * cuando una venta se confirma. Así el descuento de stock ocurre
 * solo, sin que VentaService tenga que llamarlo a mano.
 */
public class InventarioObservable {
    private List<InventarioObserver> observadores = new ArrayList<>();

    public void agregarObserver(InventarioObserver observer) {
        observadores.add(observer);
    }

    public void quitarObserver(InventarioObserver observer) {
        observadores.remove(observer);
    }

    protected void notificarVentaConfirmada(Producto producto, int cantidad) {
        for (InventarioObserver observer : observadores) {
            observer.onVentaConfirmada(producto, cantidad);
        }
    }
}