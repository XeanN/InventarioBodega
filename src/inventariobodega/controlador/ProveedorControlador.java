package controlador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import modelo.Proveedor;
import servicio.ProveedorService;
import util.JsonUtil;

public class ProveedorControlador {
    private final ProveedorService service = new ProveedorService();

    public String listarJson() {
        List<Map<String, Object>> data = service.listar().stream().map(this::toMap).collect(Collectors.toList());
        return JsonUtil.array(data);
    }

    public String crearJson(String body) {
        Proveedor p = new Proveedor(0, JsonUtil.getString(body, "nombre"), JsonUtil.getString(body, "contacto"));
        return JsonUtil.obj(toMap(service.crear(p)));
    }

    public String actualizarJson(int id, String body) {
        Proveedor p = new Proveedor(id, JsonUtil.getString(body, "nombre"), JsonUtil.getString(body, "contacto"));
        return JsonUtil.obj(toMap(service.actualizar(p)));
    }

    public void eliminar(int id) {
        service.eliminar(id);
    }

    private Map<String, Object> toMap(Proveedor p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idProveedor", p.getIdProveedor());
        map.put("nombre", p.getNombre());
        map.put("contacto", p.getContacto());
        return map;
    }
}
