package controlador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import modelo.Caja;
import servicio.CajaService;
import util.JsonUtil;

public class CajaControlador {
    private final CajaService service = new CajaService();

    public String listarJson() {
        List<Map<String, Object>> data = service.listar().stream().map(this::toMap).collect(Collectors.toList());
        return JsonUtil.array(data);
    }

    public String cambiarEstadoJson(int id, String body) {
        String estado = JsonUtil.getString(body, "estado");
        Caja caja = service.cambiarEstado(id, estado);
        return caja == null ? null : JsonUtil.obj(toMap(caja));
    }

    private Map<String, Object> toMap(Caja c) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idCaja", c.getIdCaja());
        map.put("nombre", c.getNombre());
        map.put("estado", c.getEstado());
        return map;
    }
}
