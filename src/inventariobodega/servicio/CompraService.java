package servicio;

import java.util.List;
import java.util.Map;
import repositorio.CompraRepositorio;
import repositorio.CompraRepositorioJDBC;

public class CompraService {
    private final CompraRepositorio repo = new CompraRepositorioJDBC();

    public List<Map<String, Object>> listar() {
        return repo.listarResumen();
    }

    public Map<String, Object> buscar(int id) {
        return repo.buscarDetalle(id).orElse(null);
    }

    public int crear(int idProveedor, List<Map<String, Object>> detalles) {
        return repo.crearCompra(idProveedor, detalles);
    }
}
