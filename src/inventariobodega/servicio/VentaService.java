package servicio;

import java.util.List;
import java.util.Map;
import repositorio.VentaRepositorio;
import repositorio.VentaRepositorioJDBC;

public class VentaService {
    private final VentaRepositorio repo = new VentaRepositorioJDBC();

    public List<Map<String, Object>> listar() {
        return repo.listarResumen();
    }

    public Map<String, Object> buscar(int id) {
        return repo.buscarDetalle(id).orElse(null);
    }

    public int crear(int idCliente, int idCaja, List<Map<String, Object>> detalles) {
        return repo.crearVenta(idCliente, idCaja, detalles);
    }
}
