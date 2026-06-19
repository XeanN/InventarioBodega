package modelo;

import java.util.Date;

/**
 * Representa una compra a un proveedor (ingreso de mercadería).
 * Solo datos: sin lógica de negocio.
 */
public class Compra {
    private int idCompra;
    private Proveedor proveedor;
    private Date fecha;

    public Compra() {
    }

    public Compra(int idCompra, Proveedor proveedor, Date fecha) {
        this.idCompra = idCompra;
        this.proveedor = proveedor;
        this.fecha = fecha;
    }

    public int getIdCompra() {
        return idCompra;
    }
    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }
    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Compra #" + idCompra + " - " + proveedor;
    }
}