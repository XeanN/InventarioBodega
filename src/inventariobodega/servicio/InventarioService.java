package servicio;

import java.util.List;
import modelo.Producto;
import repositorio.ProductoRepositorio;
import repositorio.ProductoRepositorioJDBC;

public class InventarioService {
    private final ProductoRepositorio repo = new ProductoRepositorioJDBC();

    public List<Producto> listar() {
        return repo.listar();
    }

    public Producto buscar(String codigo) {
        return repo.buscarPorCodigo(codigo).orElse(null);
    }

    public Producto crear(Producto producto) {
        repo.insertar(producto);
        return producto;
    }

    public Producto actualizar(Producto producto) {
        repo.actualizar(producto);
        return producto;
    }

    public void eliminar(String codigo) {
        repo.eliminar(codigo);
    }
}
