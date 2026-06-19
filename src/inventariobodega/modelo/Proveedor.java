package modelo;
/**
 * Representa un proveedor que entrega mercadería.
 * Solo datos: sin lógica de negocio.
 */
public class Proveedor {

    private int idProveedor;
    private String nombre;
    private String contacto;

    public Proveedor(){
    }

    public Proveedor(int idProveedor, String nombre, String contacto){
        this.idProveedor = idProveedor;
        this.nombre = nombre;
        this.contacto = contacto;
    }

    public int getIdProveedor(){
        return idProveedor;
    }
    public void setIdProveedor(int idProveedor){
        this.idProveedor = idProveedor;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getContacto(){
        return contacto;
    }
    public void setContacto(String contacto){
        this.contacto = contacto;
    }

    @Override
    public String toString(){
        return nombre + " (" + contacto + ")";
    }

}
