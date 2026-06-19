package util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Lee la configuración de base de datos desde config.properties
 * ubicado en la raíz del classpath (junto al código fuente).
 */
public final class ConfigBD {
    private static final Properties props = new Properties();

    static {
        cargar();
    }

    private ConfigBD() {
    }

    private static void cargar() {
        try (InputStream in = ConfigBD.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                props.load(in);
                return;
            }
        } catch (IOException ignored) {
        }

        Path local = Path.of("src/inventariobodega/config.properties");
        if (Files.exists(local)) {
            try (InputStream in = Files.newInputStream(local)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("No se pudo leer config.properties: " + e.getMessage());
            }
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }
}
