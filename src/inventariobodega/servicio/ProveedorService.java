package servicio;

import java.util.List;
import modelo.Proveedor;
import repositorio.ProveedorRepositorio;
import repositorio.ProveedorRepositorioJDBC;

public class ProveedorService {
    private final ProveedorRepositorio repo = new ProveedorRepositorioJDBC();

    public List<Proveedor> listar() {
        return repo.listar();
    }

    public Proveedor crear(Proveedor proveedor) {
        repo.insertar(proveedor);
        return proveedor;
    }

    public Proveedor actualizar(Proveedor proveedor) {
        repo.actualizar(proveedor);
        return proveedor;
    }

    public void eliminar(int id) {
        repo.eliminar(id);
    }
}
