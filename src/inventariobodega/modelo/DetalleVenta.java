package modelo;

/**
 * Representa una línea de detalle dentro de una venta.
 * Solo datos: sin lógica de negocio.
 */
public class DetalleVenta {
    private int idDetalle;
    private Venta venta;
    private Producto producto;
    private int cantidad;
    private double subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(int idDetalle, Venta venta, Producto producto, int cantidad, double subtotal) {
        this.idDetalle = idDetalle;
        this.venta = venta;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public int getIdDetalle() {
        return idDetalle;
    }
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Venta getVenta() {
        return venta;
    }
    public void setVenta(Venta venta) {
        this.venta = venta;
    }

    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}