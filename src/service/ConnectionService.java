package service;

import model.ConnectionConfig;
import model.ConnectionResult;

/**
 * Contrato funcional del servicio.
 */
@FunctionalInterface
public interface ConnectionService {
    ConnectionResult probarConexion(ConnectionConfig config);
}