package modelo;
/**
 * Representa un cliente que realiza una venta.
 * Solo datos: sin lógica de negocio.
 */

public class Cliente {

    private int idCliente;
    private String nombre;
    private String documento;

    public Cliente(){
    }

    public Cliente(int idCliente, String nombre, String documento){
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.documento = documento;
    }

    public int getIdCliente(){
        return idCliente;
    }
    public void setIdCliente(int idCliente){
        this.idCliente = idCliente;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getDocumento(){
        return documento;
    }
    public void setDocumento(String documento){
        this.documento = documento;
    }

    @Override
    public String toString(){
        return nombre + " (" + documento + ")";
    }

}