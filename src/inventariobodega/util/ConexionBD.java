package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton: una sola conexión activa a la base de datos
 * (PostgreSQL en Supabase) compartida por todos los repositorios.
 */
public class ConexionBD {
    private static ConexionBD instancia;
    private Connection conexion;

    private static final String URL = "jdbc:postgresql://<host>:5432/<basededatos>";
    private static final String USUARIO = "<usuario>";
    private static final String PASSWORD = "<password>";

    private ConexionBD() {
        try {
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }
    }

    public static ConexionBD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBD();
        }
        return instancia;
    }

    public Connection getConexion() {
        return conexion;
    }
}