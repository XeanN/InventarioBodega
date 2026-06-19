package controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import servicio.VentaService;
import util.JsonUtil;

public class VentaControlador {
    private final VentaService service = new VentaService();

    public String listarJson() {
        return JsonUtil.array(service.listar());
    }

    public String buscarJson(int id) {
        Map<String, Object> venta = service.buscar(id);
        return venta == null ? null : JsonUtil.obj(venta);
    }

    public String crearJson(String body) {
        int idCliente = JsonUtil.getInt(body, "idCliente");
        int idCaja = JsonUtil.getInt(body, "idCaja");
        List<Map<String, Object>> detalles = parseDetallesVenta(body);
        int idVenta = service.crear(idCliente, idCaja, detalles);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("idVenta", idVenta);
        result.put("estado", "PAGADO");
        return JsonUtil.obj(result);
    }

    private List<Map<String, Object>> parseDetallesVenta(String body) {
        List<Map<String, Object>> detalles = new ArrayList<>();
        int index = 0;
        while (true) {
            String codigo = extractArrayValue(body, "codigoProducto", index);
            if (codigo == null) {
                break;
            }
            Integer cantidad = extractArrayInt(body, "cantidad", index);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("codigoProducto", codigo);
            item.put("cantidad", cantidad == null ? 1 : cantidad);
            detalles.add(item);
            index++;
        }
        return detalles;
    }

    private String extractArrayValue(String json, String key, int index) {
        String marker = "\"" + key + "\"";
        int pos = 0;
        for (int i = 0; i <= index; i++) {
            pos = json.indexOf(marker, pos);
            if (pos < 0) {
                return null;
            }
            pos += marker.length();
        }
        int startQuote = json.indexOf('"', json.indexOf(':', pos) + 1);
        int endQuote = json.indexOf('"', startQuote + 1);
        return json.substring(startQuote + 1, endQuote);
    }

    private Integer extractArrayInt(String json, String key, int index) {
        String marker = "\"" + key + "\"";
        int pos = 0;
        for (int i = 0; i <= index; i++) {
            pos = json.indexOf(marker, pos);
            if (pos < 0) {
                return null;
            }
            pos += marker.length();
        }
        int colon = json.indexOf(':', pos) + 1;
        int end = colon;
        while (end < json.length() && ",}]\n\r ".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        return Integer.parseInt(json.substring(colon, end).trim());
    }
}
