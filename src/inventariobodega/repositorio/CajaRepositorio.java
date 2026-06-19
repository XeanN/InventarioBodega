package repositorio;

import java.util.List;
import modelo.Caja;

public interface CajaRepositorio {
    List<Caja> listar();
    void actualizarEstado(int idCaja, String estado);
}
