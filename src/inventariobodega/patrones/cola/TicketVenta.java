package patrones.cola;

import modelo.Producto;

/**
 * Representa el "turno" de una caja pidiendo reservar stock
 * de un producto. Es el objeto que viaja dentro de ColaTransacciones.
 */
public class TicketVenta {
    private int idCaja;
    private Producto producto;
    private int cantidadSolicitada;

    public TicketVenta(int idCaja, Producto producto, int cantidadSolicitada) {
        this.idCaja = idCaja;
        this.producto = producto;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public int getIdCaja() {
        return idCaja;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    @Override
    public String toString() {
        return "Ticket[caja=" + idCaja + ", producto=" + producto.getCodigo() + ", cantidad=" + cantidadSolicitada + "]";
    }
}