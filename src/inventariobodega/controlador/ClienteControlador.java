package controlador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import modelo.Cliente;
import servicio.ClienteService;
import util.JsonUtil;

public class ClienteControlador {
    private final ClienteService service = new ClienteService();

    public String listarJson() {
        List<Map<String, Object>> data = service.listar().stream().map(this::toMap).collect(Collectors.toList());
        return JsonUtil.array(data);
    }

    public String crearJson(String body) {
        Cliente c = new Cliente(0, JsonUtil.getString(body, "nombre"), JsonUtil.getString(body, "documento"));
        return JsonUtil.obj(toMap(service.crear(c)));
    }

    public String actualizarJson(int id, String body) {
        Cliente c = new Cliente(id, JsonUtil.getString(body, "nombre"), JsonUtil.getString(body, "documento"));
        return JsonUtil.obj(toMap(service.actualizar(c)));
    }

    public void eliminar(int id) {
        service.eliminar(id);
    }

    private Map<String, Object> toMap(Cliente c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idCliente", c.getIdCliente());
        map.put("nombre", c.getNombre());
        map.put("documento", c.getDocumento());
        return map;
    }
}
