package modelo;
/**
 * Representa un punto de venta (caja).
 * Solo datos: sin lógica de negocio.
 */

public class Caja {

    private int idCaja;
    private String nombre;
    private String estado;

    public Caja(){
    }

    public Caja(int idCaja, String nombre, String estado){
        this.idCaja = idCaja;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getIdCaja(){
        return idCaja;
    }
    public void setIdCaja(int idCaja){
        this.idCaja = idCaja;
    }

    public String getNombre(){
        return nombre;
    }
    public void setNombre(String nombre){
        this.nombre = nombre;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    @Override
    public String toString(){
        return nombre + " [" + estado + "]";
    }

}
