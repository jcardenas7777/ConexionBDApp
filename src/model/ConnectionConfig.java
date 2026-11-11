package model;

/**
 * Contenedor inmutable para los datos del formulario.
 */
public record ConnectionConfig(String url, String usuario, String password) {
    
    public boolean esValida() {
        return url != null && !url.isBlank();
    }
}
