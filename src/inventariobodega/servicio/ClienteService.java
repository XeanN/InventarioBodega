package servicio;

import java.util.List;
import modelo.Cliente;
import repositorio.ClienteRepositorio;
import repositorio.ClienteRepositorioJDBC;

public class ClienteService {
    private final ClienteRepositorio repo = new ClienteRepositorioJDBC();

    public List<Cliente> listar() {
        return repo.listar();
    }

    public Cliente crear(Cliente cliente) {
        repo.insertar(cliente);
        return cliente;
    }

    public Cliente actualizar(Cliente cliente) {
        repo.actualizar(cliente);
        return cliente;
    }

    public void eliminar(int id) {
        repo.eliminar(id);
    }
}
