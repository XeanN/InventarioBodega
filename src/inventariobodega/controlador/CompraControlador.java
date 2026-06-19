package controlador;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import servicio.CompraService;
import util.JsonUtil;

public class CompraControlador {
    private final CompraService service = new CompraService();

    public String listarJson() {
        return JsonUtil.array(service.listar());
    }

    public String buscarJson(int id) {
        Map<String, Object> compra = service.buscar(id);
        return compra == null ? null : JsonUtil.obj(compra);
    }

    public String crearJson(String body) {
        int idProveedor = JsonUtil.getInt(body, "idProveedor");
        List<Map<String, Object>> detalles = parseDetallesCompra(body);
        int idCompra = service.crear(idProveedor, detalles);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("idCompra", idCompra);
        return JsonUtil.obj(result);
    }

    private List<Map<String, Object>> parseDetallesCompra(String body) {
        List<Map<String, Object>> detalles = new ArrayList<>();
        int index = 0;
        while (true) {
            String codigo = extractArrayValue(body, "codigoProducto", index);
            if (codigo == null) {
                break;
            }
            Integer cantidad = extractArrayInt(body, "cantidad", index);
            Double costo = extractArrayDouble(body, "costoUnitario", index);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("codigoProducto", codigo);
            item.put("cantidad", cantidad == null ? 1 : cantidad);
            item.put("costoUnitario", costo == null ? 0.0 : costo);
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

    private Double extractArrayDouble(String json, String key, int index) {
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
        return Double.parseDouble(json.substring(colon, end).trim());
    }
}
