package repositorio;

import java.util.List;
import java.util.Optional;
import modelo.Proveedor;

public interface ProveedorRepositorio {
    List<Proveedor> listar();
    Optional<Proveedor> buscarPorId(int id);
    void insertar(Proveedor proveedor);
    void actualizar(Proveedor proveedor);
    void eliminar(int id);
}
