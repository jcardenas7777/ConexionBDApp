package model;

/**
 * Contenedor inmutable para la respuesta tras intentar la conexión.
 */
public record ConnectionResult(boolean exitoso, String mensaje, String detalleTecnico) {
}