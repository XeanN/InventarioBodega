package repositorio;

import java.util.List;
import java.util.Optional;
import modelo.Producto;

public interface ProductoRepositorio {
    List<Producto> listar();
    Optional<Producto> buscarPorCodigo(String codigo);
    void insertar(Producto producto);
    void actualizar(Producto producto);
    void eliminar(String codigo);
}
