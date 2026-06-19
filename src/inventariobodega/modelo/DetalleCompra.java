package modelo;

/**
 * Representa una línea de detalle dentro de una compra.
 * Solo datos: sin lógica de negocio.
 */
public class DetalleCompra {
    private int idDetalle;
    private Compra compra;
    private Producto producto;
    private int cantidad;
    private double costoUnitario;

    public DetalleCompra() {
    }

    public DetalleCompra(int idDetalle, Compra compra, Producto producto, int cantidad, double costoUnitario) {
        this.idDetalle = idDetalle;
        this.compra = compra;
        this.producto = producto;
        this.cantidad = cantidad;
        this.costoUnitario = costoUnitario;
    }

    public int getIdDetalle() {
        return idDetalle;
    }
    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public Compra getCompra() {
        return compra;
    }
    public void setCompra(Compra compra) {
        this.compra = compra;
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

    public double getCostoUnitario() {
        return costoUnitario;
    }
    public void setCostoUnitario(double costoUnitario) {
        this.costoUnitario = costoUnitario;
    }
}