package servicio;

import java.util.List;
import modelo.Caja;
import repositorio.CajaRepositorio;
import repositorio.CajaRepositorioJDBC;

public class CajaService {
    private final CajaRepositorio repo = new CajaRepositorioJDBC();

    public List<Caja> listar() {
        return repo.listar();
    }

    public Caja cambiarEstado(int idCaja, String estado) {
        repo.actualizarEstado(idCaja, estado);
        return repo.listar().stream().filter(c -> c.getIdCaja() == idCaja).findFirst().orElse(null);
    }
}
