package patrones.observer;
 
import modelo.Producto;
 
/**
 * Quien implemente esta interfaz se entera automáticamente
 * cuando una venta se confirma. InventarioService la implementa
 * para descontar el stock real en ese momento exacto.
 */
public interface InventarioObserver {
    void onVentaConfirmada(Producto producto, int cantidad);
}
 






