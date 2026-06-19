package repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import util.ConexionBD;

public class CompraRepositorioJDBC implements CompraRepositorio {

    @Override
    public List<Map<String, Object>> listarResumen() {
        String sql = """
            SELECT co.id_compra, co.fecha, p.id_proveedor, p.nombre AS proveedor_nombre,
                   COALESCE(SUM(dc.cantidad * dc.costo_unitario), 0) AS total
            FROM compra co
            JOIN proveedor p ON p.id_proveedor = co.id_proveedor
            LEFT JOIN detalle_compra dc ON dc.id_compra = co.id_compra
            GROUP BY co.id_compra, p.id_proveedor
            ORDER BY co.fecha DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("idCompra", rs.getInt("id_compra"));
                map.put("fecha", rs.getTimestamp("fecha").toInstant().toString());
                map.put("idProveedor", rs.getInt("id_proveedor"));
                map.put("proveedorNombre", rs.getString("proveedor_nombre"));
                map.put("total", rs.getDouble("total"));
                lista.add(map);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar compras", e);
        }
        return lista;
    }

    @Override
    public Optional<Map<String, Object>> buscarDetalle(int idCompra) {
        String sql = """
            SELECT co.id_compra, co.fecha, p.id_proveedor, p.nombre AS proveedor_nombre
            FROM compra co
            JOIN proveedor p ON p.id_proveedor = co.id_proveedor
            WHERE co.id_compra = ?
            """;
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Map<String, Object> compra = new LinkedHashMap<>();
                compra.put("idCompra", rs.getInt("id_compra"));
                compra.put("fecha", rs.getTimestamp("fecha").toInstant().toString());
                compra.put("idProveedor", rs.getInt("id_proveedor"));
                compra.put("proveedorNombre", rs.getString("proveedor_nombre"));
                List<Map<String, Object>> detalles = listarDetalles(cn, idCompra);
                double total = detalles.stream().mapToDouble(d -> ((Number) d.get("subtotal")).doubleValue()).sum();
                compra.put("detalles", detalles);
                compra.put("total", total);
                return Optional.of(compra);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar compra", e);
        }
    }

    @Override
    public int crearCompra(int idProveedor, List<Map<String, Object>> detalles) {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                int idCompra;
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO compra (id_proveedor) VALUES (?) RETURNING id_compra",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idProveedor);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se generó id de compra");
                        }
                        idCompra = rs.getInt(1);
                    }
                }

                String sqlDetalle = "INSERT INTO detalle_compra (id_compra, codigo_producto, cantidad, costo_unitario) VALUES (?, ?, ?, ?)";
                String sqlStock = "UPDATE producto SET stock = stock + ? WHERE codigo = ?";

                for (Map<String, Object> item : detalles) {
                    String codigo = (String) item.get("codigoProducto");
                    int cantidad = ((Number) item.get("cantidad")).intValue();
                    double costo = ((Number) item.get("costoUnitario")).doubleValue();
                    if (cantidad <= 0 || costo < 0) {
                        throw new IllegalArgumentException("Detalle inválido");
                    }
                    try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                        ps.setInt(1, idCompra);
                        ps.setString(2, codigo);
                        ps.setInt(3, cantidad);
                        ps.setDouble(4, costo);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
                        ps.setInt(1, cantidad);
                        ps.setString(2, codigo);
                        ps.executeUpdate();
                    }
                }
                cn.commit();
                return idCompra;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear compra: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> listarDetalles(Connection cn, int idCompra) throws SQLException {
        String sql = """
            SELECT dc.id_detalle, dc.codigo_producto, pr.nombre AS producto_nombre,
                   dc.cantidad, dc.costo_unitario
            FROM detalle_compra dc
            JOIN producto pr ON pr.codigo = dc.codigo_producto
            WHERE dc.id_compra = ?
            """;
        List<Map<String, Object>> detalles = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> det = new LinkedHashMap<>();
                    int cantidad = rs.getInt("cantidad");
                    double costo = rs.getDouble("costo_unitario");
                    det.put("idDetalle", rs.getInt("id_detalle"));
                    det.put("codigoProducto", rs.getString("codigo_producto"));
                    det.put("productoNombre", rs.getString("producto_nombre"));
                    det.put("cantidad", cantidad);
                    det.put("costoUnitario", costo);
                    det.put("subtotal", cantidad * costo);
                    detalles.add(det);
                }
            }
        }
        return detalles;
    }
}
