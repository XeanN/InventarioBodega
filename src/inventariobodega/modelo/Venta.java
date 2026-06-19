package modelo;
import java.util.Date;
/**
 * Representa una venta realizada en una caja.
 * Solo datos: sin lógica de negocio (la lógica vive en VentaService).
 */
public class Venta {
    
    private int idVenta;
    private Cliente cliente;
    private Caja caja;
    private EstadoPedido estado;
    private Date fecha;

    public Venta(){
    }

    public Venta(int idVenta, Cliente cliente, Caja caja, EstadoPedido estado, Date fecha){
        this.idVenta = idVenta;
        this.cliente = cliente;
        this.caja = caja;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getIdVenta(){
        return idVenta;
    }
    public void setIdVenta(int idVenta){
        this.idVenta = idVenta;
    }

    public Cliente getCliente(){
        return cliente;
    }
    public void setCliente(Cliente cliente){
        this.cliente = cliente;
    }

    public Caja getCaja(){
        return caja;
    }
    public void setCaja(Caja caja){
        this.caja = caja;
    }

    public EstadoPedido getEstado(){
        return estado;
    }
    public void setEstado(EstadoPedido estado){
        this.estado = estado;
    }

    public Date getFecha(){
        return fecha;
    }
    public void setFecha(Date fecha){
        this.fecha = fecha;
    }
    
    @Override
    public String toString() {
        return "Venta #" + idVenta + " - " + estado;
    }

}
