package repositorio;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VentaRepositorio {
    List<Map<String, Object>> listarResumen();
    Optional<Map<String, Object>> buscarDetalle(int idVenta);
    int crearVenta(int idCliente, int idCaja, List<Map<String, Object>> detalles);
}
