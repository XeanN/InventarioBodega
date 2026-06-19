package repositorio;

import java.util.List;
import java.util.Optional;
import modelo.Cliente;

public interface ClienteRepositorio {
    List<Cliente> listar();
    Optional<Cliente> buscarPorId(int id);
    void insertar(Cliente cliente);
    void actualizar(Cliente cliente);
    void eliminar(int id);
}
