package controlador;

import servicio.DashboardService;
import util.JsonUtil;

public class DashboardControlador {
    private final DashboardService service = new DashboardService();

    public String statsJson() {
        return JsonUtil.obj(service.estadisticas());
    }
}
