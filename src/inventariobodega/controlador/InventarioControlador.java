package controlador;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import modelo.Producto;
import servicio.InventarioService;
import util.JsonUtil;

public class InventarioControlador {
    private final InventarioService service = new InventarioService();

    public String listarJson() {
        List<Map<String, Object>> data = service.listar().stream().map(this::toMap).collect(Collectors.toList());
        return JsonUtil.array(data);
    }

    public String buscarJson(String codigo) {
        Producto p = service.buscar(codigo);
        if (p == null) {
            return null;
        }
        return JsonUtil.obj(toMap(p));
    }

    public String crearJson(String body) {
        Producto p = new Producto(
                JsonUtil.getString(body, "codigo"),
                JsonUtil.getString(body, "nombre"),
                JsonUtil.getDouble(body, "precioVenta"),
                JsonUtil.getInt(body, "stock")
        );
        return JsonUtil.obj(toMap(service.crear(p)));
    }

    public String actualizarJson(String codigo, String body) {
        Producto actual = service.buscar(codigo);
        if (actual == null) {
            return null;
        }
        String nombre = JsonUtil.getString(body, "nombre");
        Double precio = JsonUtil.getDouble(body, "precioVenta");
        Integer stock = JsonUtil.getInt(body, "stock");
        if (nombre != null) actual.setNombre(nombre);
        if (precio != null) actual.setPrecioVenta(precio);
        if (stock != null) actual.setStock(stock);
        return JsonUtil.obj(toMap(service.actualizar(actual)));
    }

    public void eliminar(String codigo) {
        service.eliminar(codigo);
    }

    private Map<String, Object> toMap(Producto p) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("codigo", p.getCodigo());
        map.put("nombre", p.getNombre());
        map.put("precioVenta", p.getPrecioVenta());
        map.put("stock", p.getStock());
        return map;
    }
}
