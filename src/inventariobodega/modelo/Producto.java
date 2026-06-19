package modelo;
/**
 * Representa un producto del inventario.
 * Solo datos: sin lógica de negocio.
 */
public class Producto {
    private String codigo;
    private String nombre;
    private double precioVenta;
    private int stock;

    public Producto(){
    }

    public Producto(String codigo, String nombre, double precioVenta, int stock){
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.stock = stock;
    }

    public String getCodigo(){
        return codigo;
    }
    public void setCodigo(String codigo){
        this.codigo = codigo;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public double getPrecioVenta(){
        return precioVenta;
    }
    public void setPrecioVenta(double precioVenta){
        this.precioVenta = precioVenta;
    }

    public int getStock(){
        return stock;
    }
    public void setStock(int stock){
        this.stock = stock;
    }

    @Override
    public String toString(){
        return codigo + " - " + nombre + " (stock:" + stock + ")";
    }
}