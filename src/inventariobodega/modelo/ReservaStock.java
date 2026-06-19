package modelo;

/**
 * Representa una reserva temporal de stock.
 * Es la pieza clave que evita que dos cajas vendan
 * el mismo producto al mismo tiempo: el stock solo se
 * descuenta de verdad cuando la reserva pasa a CONFIRMADA.
 *
 * Estados posibles: ACTIVA, CONFIRMADA, LIBERADA.
 */
public class ReservaStock {
    private int idReserva;
    private Producto producto;
    private Venta venta;
    private int cantidadReservada;
    private String estado;

    public ReservaStock() {
    }

    public ReservaStock(int idReserva, Producto producto, Venta venta, int cantidadReservada, String estado) {
        this.idReserva = idReserva;
        this.producto = producto;
        this.venta = venta;
        this.cantidadReservada = cantidadReservada;
        this.estado = estado;
    }

    public int getIdReserva() {
        return idReserva;
    }
    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Venta getVenta() {
        return venta;
    }
    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public int getCantidadReservada() {
        return cantidadReservada;
    }
    public void setCantidadReservada(int cantidadReservada) {
        this.cantidadReservada = cantidadReservada;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva #" + idReserva + " - " + producto.getCodigo() + " x" + cantidadReservada + " [" + estado + "]";
    }
}