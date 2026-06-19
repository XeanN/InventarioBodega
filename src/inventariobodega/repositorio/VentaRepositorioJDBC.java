package repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import util.ConexionBD;

public class VentaRepositorioJDBC implements VentaRepositorio {

    @Override
    public List<Map<String, Object>> listarResumen() {
        String sql = """
            SELECT v.id_venta, v.estado, v.fecha, c.id_cliente, c.nombre AS cliente_nombre,
                   c.documento AS cliente_documento, ca.id_caja, ca.nombre AS caja_nombre,
                   COALESCE(SUM(dv.subtotal), 0) AS total
            FROM venta v
            JOIN cliente c ON c.id_cliente = v.id_cliente
            JOIN caja ca ON ca.id_caja = v.id_caja
            LEFT JOIN detalle_venta dv ON dv.id_venta = v.id_venta
            GROUP BY v.id_venta, c.id_cliente, ca.id_caja
            ORDER BY v.fecha DESC
            """;
        List<Map<String, Object>> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(resumen(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ventas", e);
        }
        return lista;
    }

    @Override
    public Optional<Map<String, Object>> buscarDetalle(int idVenta) {
        String sqlVenta = """
            SELECT v.id_venta, v.estado, v.fecha, c.id_cliente, c.nombre AS cliente_nombre,
                   c.documento AS cliente_documento, ca.id_caja, ca.nombre AS caja_nombre
            FROM venta v
            JOIN cliente c ON c.id_cliente = v.id_cliente
            JOIN caja ca ON ca.id_caja = v.id_caja
            WHERE v.id_venta = ?
            """;
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sqlVenta)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Map<String, Object> venta = resumenBase(rs);
                List<Map<String, Object>> detalles = listarDetalles(cn, idVenta);
                double total = detalles.stream().mapToDouble(d -> ((Number) d.get("subtotal")).doubleValue()).sum();
                venta.put("detalles", detalles);
                venta.put("total", total);
                return Optional.of(venta);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar venta", e);
        }
    }

    @Override
    public int crearVenta(int idCliente, int idCaja, List<Map<String, Object>> detalles) {
        try (Connection cn = ConexionBD.getInstancia().getConexion()) {
            cn.setAutoCommit(false);
            try {
                String sqlVenta = "INSERT INTO venta (id_cliente, id_caja, estado) VALUES (?, ?, 'PAGADO') RETURNING id_venta";
                int idVenta;
                try (PreparedStatement ps = cn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, idCliente);
                    ps.setInt(2, idCaja);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException("No se generó id de venta");
                        }
                        idVenta = rs.getInt(1);
                    }
                }

                String sqlDetalle = "INSERT INTO detalle_venta (id_venta, codigo_producto, cantidad, subtotal) VALUES (?, ?, ?, ?)";
                String sqlStock = "UPDATE producto SET stock = stock - ? WHERE codigo = ?";
                String sqlPrecio = "SELECT precio_venta, stock, nombre FROM producto WHERE codigo = ? FOR UPDATE";

                for (Map<String, Object> item : detalles) {
                    String codigo = (String) item.get("codigoProducto");
                    int cantidad = ((Number) item.get("cantidad")).intValue();
                    double precio;
                    int stock;
                    String nombre;
                    try (PreparedStatement ps = cn.prepareStatement(sqlPrecio)) {
                        ps.setString(1, codigo);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                throw new IllegalArgumentException("Producto " + codigo + " no encontrado");
                            }
                            precio = rs.getDouble("precio_venta");
                            stock = rs.getInt("stock");
                            nombre = rs.getString("nombre");
                        }
                    }
                    if (cantidad <= 0) {
                        throw new IllegalArgumentException("Cantidad inválida");
                    }
                    if (stock < cantidad) {
                        throw new IllegalArgumentException("Stock insuficiente para " + nombre + ". Disponible: " + stock);
                    }
                    double subtotal = precio * cantidad;
                    try (PreparedStatement ps = cn.prepareStatement(sqlDetalle)) {
                        ps.setInt(1, idVenta);
                        ps.setString(2, codigo);
                        ps.setInt(3, cantidad);
                        ps.setDouble(4, subtotal);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = cn.prepareStatement(sqlStock)) {
                        ps.setInt(1, cantidad);
                        ps.setString(2, codigo);
                        ps.executeUpdate();
                    }
                }
                cn.commit();
                return idVenta;
            } catch (Exception e) {
                cn.rollback();
                throw e;
            } finally {
                cn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al crear venta: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> listarDetalles(Connection cn, int idVenta) throws SQLException {
        String sql = """
            SELECT dv.id_detalle, dv.codigo_producto, p.nombre AS producto_nombre, dv.cantidad, dv.subtotal
            FROM detalle_venta dv
            JOIN producto p ON p.codigo = dv.codigo_producto
            WHERE dv.id_venta = ?
            """;
        List<Map<String, Object>> detalles = new ArrayList<>();
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> det = new LinkedHashMap<>();
                    det.put("idDetalle", rs.getInt("id_detalle"));
                    det.put("codigoProducto", rs.getString("codigo_producto"));
                    det.put("productoNombre", rs.getString("producto_nombre"));
                    det.put("cantidad", rs.getInt("cantidad"));
                    det.put("subtotal", rs.getDouble("subtotal"));
                    detalles.add(det);
                }
            }
        }
        return detalles;
    }

    private Map<String, Object> resumen(ResultSet rs) throws SQLException {
        Map<String, Object> map = resumenBase(rs);
        map.put("total", rs.getDouble("total"));
        return map;
    }

    private Map<String, Object> resumenBase(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("idVenta", rs.getInt("id_venta"));
        map.put("estado", rs.getString("estado"));
        map.put("fecha", rs.getTimestamp("fecha").toInstant().toString());
        map.put("idCliente", rs.getInt("id_cliente"));
        map.put("clienteNombre", rs.getString("cliente_nombre"));
        map.put("clienteDocumento", rs.getString("cliente_documento"));
        map.put("idCaja", rs.getInt("id_caja"));
        map.put("cajaNombre", rs.getString("caja_nombre"));
        return map;
    }
}
