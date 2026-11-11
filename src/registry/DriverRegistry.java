package registry;

import java.util.HashMap;
import java.util.Map;

/**
 * Catálogo extensible de controladores JDBC.
 */
public class DriverRegistry {
    
    private final Map<String, String> driverMap = new HashMap<>();

    public DriverRegistry() {
        driverMap.put("jdbc:sqlite:", "org.sqlite.JDBC");
        driverMap.put("jdbc:mysql:", "com.mysql.cj.jdbc.Driver");
        driverMap.put("jdbc:postgresql:", "org.postgresql.Driver");
        driverMap.put("jdbc:h2:", "org.h2.Driver");
        driverMap.put("jdbc:sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public void registrarDriver(String prefijoUrl, String claseDriver) {
        driverMap.put(prefijoUrl.toLowerCase(), claseDriver);
    }

    public String resolverClaseDriver(String url) {
        if (url == null) {
            return "org.sqlite.JDBC";
        }
        String urlMin = url.toLowerCase();

        // Lambda en tubería Stream para buscar la coincidencia
        return driverMap.entrySet().stream()
                .filter(entry -> urlMin.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("org.sqlite.JDBC");
    }
}