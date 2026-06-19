package repositorio;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CompraRepositorio {
    List<Map<String, Object>> listarResumen();
    Optional<Map<String, Object>> buscarDetalle(int idCompra);
    int crearCompra(int idProveedor, List<Map<String, Object>> detalles);
}
