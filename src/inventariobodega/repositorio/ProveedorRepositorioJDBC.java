package repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelo.Proveedor;
import util.ConexionBD;

public class ProveedorRepositorioJDBC implements ProveedorRepositorio {

    @Override
    public List<Proveedor> listar() {
        String sql = "SELECT id_proveedor, nombre, contacto FROM proveedor ORDER BY nombre";
        List<Proveedor> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar proveedores", e);
        }
        return lista;
    }

    @Override
    public Optional<Proveedor> buscarPorId(int id) {
        String sql = "SELECT id_proveedor, nombre, contacto FROM proveedor WHERE id_proveedor = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedor", e);
        }
        return Optional.empty();
    }

    @Override
    public void insertar(Proveedor proveedor) {
        String sql = "INSERT INTO proveedor (nombre, contacto) VALUES (?, ?) RETURNING id_proveedor";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    proveedor.setIdProveedor(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar proveedor", e);
        }
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedor SET nombre = ?, contacto = ? WHERE id_proveedor = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, proveedor.getNombre());
            ps.setString(2, proveedor.getContacto());
            ps.setInt(3, proveedor.getIdProveedor());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar proveedor", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM proveedor WHERE id_proveedor = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar proveedor: " + e.getMessage(), e);
        }
    }

    private Proveedor map(ResultSet rs) throws SQLException {
        return new Proveedor(rs.getInt("id_proveedor"), rs.getString("nombre"), rs.getString("contacto"));
    }
}
