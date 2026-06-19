package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton: una sola conexión activa a PostgreSQL/Supabase.
 * La configuración la pone el backend en config.properties.
 */
public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    private ConexionBD() {
        String url = ConfigBD.get("db.url", "");
        String usuario = ConfigBD.get("db.usuario", "");
        String password = ConfigBD.get("db.password", "");

        if (url.isBlank() || url.contains("<")) {
            throw new RuntimeException(
                    "Configura config.properties con los datos de PostgreSQL/Supabase (copia config.properties.example)"
            );
        }

        try {
            conexion = DriverManager.getConnection(url, usuario, password);
            conexion.setAutoCommit(true);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos: " + e.getMessage(), e);
        }
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public static void reiniciar() {
        if (instancia != null) {
            try {
                instancia.conexion.close();
            } catch (SQLException ignored) {
            }
            instancia = null;
        }
    }

    public Connection getConexion() {
        return conexion;
    }
}
