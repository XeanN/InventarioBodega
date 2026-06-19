package servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import util.ConexionBD;

public class DashboardService {

    public Map<String, Object> estadisticas() {
        Map<String, Object> stats = new LinkedHashMap<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            stats.put("totalProductos", count(cn, "SELECT COUNT(*) FROM producto"));
            stats.put("totalClientes", count(cn, "SELECT COUNT(*) FROM cliente"));
            stats.put("totalProveedores", count(cn, "SELECT COUNT(*) FROM proveedor"));
            stats.put("ventasHoy", count(cn, "SELECT COUNT(*) FROM venta WHERE fecha::date = CURRENT_DATE"));
            stats.put("ingresosHoy", sum(cn,
                    "SELECT COALESCE(SUM(dv.subtotal),0) FROM venta v LEFT JOIN detalle_venta dv ON dv.id_venta = v.id_venta WHERE v.fecha::date = CURRENT_DATE"));
            stats.put("comprasHoy", count(cn, "SELECT COUNT(*) FROM compra WHERE fecha::date = CURRENT_DATE"));
            stats.put("stockBajo", stockBajo(cn));
            stats.put("ventasRecientes", ventasRecientes(cn));
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener estadísticas", e);
        }
        return stats;
    }

    private int count(Connection cn, String sql) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private double sum(Connection cn, String sql) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        }
    }

    private List<Map<String, Object>> stockBajo(Connection cn) throws SQLException {
        String sql = "SELECT codigo, nombre, stock FROM producto WHERE stock < 20 ORDER BY stock ASC LIMIT 5";
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("codigo", rs.getString("codigo"));
                item.put("nombre", rs.getString("nombre"));
                item.put("stock", rs.getInt("stock"));
                lista.add(item);
            }
        }
        return lista;
    }

    private List<Map<String, Object>> ventasRecientes(Connection cn) throws SQLException {
        String sql = """
            SELECT v.id_venta, c.nombre AS cliente, v.estado,
                   COALESCE(SUM(dv.subtotal), 0) AS total, v.fecha
            FROM venta v
            JOIN cliente c ON c.id_cliente = v.id_cliente
            LEFT JOIN detalle_venta dv ON dv.id_venta = v.id_venta
            GROUP BY v.id_venta, c.nombre
            ORDER BY v.fecha DESC LIMIT 5
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("idVenta", rs.getInt("id_venta"));
                item.put("cliente", rs.getString("cliente"));
                item.put("estado", rs.getString("estado"));
                item.put("total", rs.getDouble("total"));
                item.put("fecha", rs.getTimestamp("fecha").toInstant().toString());
                lista.add(item);
            }
        }
        return lista;
    }
}
