package repositorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Caja;
import util.ConexionBD;

public class CajaRepositorioJDBC implements CajaRepositorio {

    @Override
    public List<Caja> listar() {
        String sql = "SELECT id_caja, nombre, estado FROM caja ORDER BY id_caja";
        List<Caja> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Caja(rs.getInt("id_caja"), rs.getString("nombre"), rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar cajas", e);
        }
        return lista;
    }

    @Override
    public void actualizarEstado(int idCaja, String estado) {
        String sql = "UPDATE caja SET estado = ? WHERE id_caja = ?";
        try (Connection cn = ConexionBD.getInstancia().getConexion();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idCaja);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar caja", e);
        }
    }
}
