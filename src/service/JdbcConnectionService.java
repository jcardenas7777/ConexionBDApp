package service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import model.ConnectionConfig;
import model.ConnectionResult;
import registry.DriverRegistry;

/**
 * Lógica JDBC aislada de cualquier librería gráfica.
 */
public class JdbcConnectionService implements ConnectionService {

    private final DriverRegistry driverRegistry;

    // Inyección de dependencias por constructor
    public JdbcConnectionService(DriverRegistry driverRegistry) {
        this.driverRegistry = driverRegistry;
    }

    @Override
    public ConnectionResult probarConexion(ConnectionConfig config) {
        if (!config.esValida()) {
            return new ConnectionResult(false, "Fallo de validación.", "Debe ingresar una URL de conexión válida.");
        }

        String claseDriver = driverRegistry.resolverClaseDriver(config.url());

        try {
            Class.forName(claseDriver);

            // Cierre automático de la conexión al terminar el bloque
            try (Connection conexion = DriverManager.getConnection(config.url(), config.usuario(), config.password())) {
                DatabaseMetaData meta = conexion.getMetaData();
                String detalle = String.format("Motor : %s %s\n  Driver: %s %s",
                        meta.getDatabaseProductName(), meta.getDatabaseProductVersion(),
                        meta.getDriverName(), meta.getDriverVersion());

                return new ConnectionResult(true, "Conexión exitosa.", detalle);
            }

        } catch (ClassNotFoundException e) {
            return new ConnectionResult(false, "Fallo del driver: no se encontró la clase del driver JDBC.",
                    "Detalle técnico: " + e.getMessage() + "\n  Agregue el .jar del driver al classpath.");
        } catch (SQLException e) {
            return new ConnectionResult(false, "Fallo de conexión.", "Detalle técnico: " + e.getMessage());
        } catch (Throwable e) {
            return new ConnectionResult(false, "Error inesperado al conectar.", "Detalle técnico: " + e.toString());
        }
    }
}