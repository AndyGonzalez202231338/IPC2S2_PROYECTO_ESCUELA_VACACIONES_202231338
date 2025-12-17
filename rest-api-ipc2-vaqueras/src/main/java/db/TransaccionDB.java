/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import compra.models.Compra;
import conexion.DBConnectionSingleton;
import exceptions.SaldoInsuficienteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author andy
 */
public class TransaccionDB {
    
    private static final String INSERTAR_COMPRA_QUERY = 
        "INSERT INTO compra (id_usuario, id_videojuego, monto_pago, fecha_compra, comision_aplicada) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String ACTUALIZAR_SALDO_USUARIO_QUERY = 
        "UPDATE usuario SET saldo_cartera = ? WHERE id_usuario = ?";
    
    private static final String OBTENER_SALDO_USUARIO_QUERY = 
        "SELECT saldo_cartera FROM usuario WHERE id_usuario = ? FOR UPDATE";
    
    /**
     * Insertar compra usando conexión existente de la transaccion que valida saldo de usuario
     * @param connection
     * @param compra
     * @return
     * @throws SQLException 
     */
    public int insertarCompra(Connection connection, Compra compra) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(INSERTAR_COMPRA_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, compra.getId_usuario());
            stmt.setInt(2, compra.getId_videojuego());
            stmt.setDouble(3, compra.getMonto_pago());
            stmt.setDate(4, new Date(compra.getFecha_compra().getTime()));
            stmt.setDouble(5, compra.getComision_aplicada());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
            
            throw new SQLException("No se pudo insertar la compra, no se generó ID");
        }
    }
    
    /**
     * Actualizar saldo de usuario usando conexión existente de transaccion de compra
     * @param connection
     * @param idUsuario
     * @param nuevoSaldo
     * @return
     * @throws SQLException 
     */
    public boolean actualizarSaldoUsuario(Connection connection, int idUsuario, double nuevoSaldo) 
            throws SQLException {
        
        try (PreparedStatement stmt = connection.prepareStatement(ACTUALIZAR_SALDO_USUARIO_QUERY)) {
            stmt.setDouble(1, nuevoSaldo);
            stmt.setInt(2, idUsuario);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Obtener saldo de usuario con LOCK (FOR UPDATE) para transacciones
     * (FOR UPDATE): evita que se edite la columna antes de que termine la transacción actual
     * @param connection
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public double obtenerSaldoUsuarioConLock(Connection connection, int idUsuario) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(OBTENER_SALDO_USUARIO_QUERY)) {
            stmt.setInt(1, idUsuario);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo_cartera");
                }
                throw new SQLException("Usuario no encontrado con ID: " + idUsuario);
            }
        }
    }
    
    /**
     * Verificar si usuario puede comprar (tiene saldo suficiente)
     * @param connection
     * @param idUsuario
     * @param montoRequerido
     * @return
     * @throws SQLException 
     */
    public boolean verificarSaldoSuficiente(Connection connection, int idUsuario, double montoRequerido) 
            throws SQLException {
        
        double saldoActual = obtenerSaldoUsuarioConLock(connection, idUsuario);
        return saldoActual >= montoRequerido;
    }
    
    /**
     * Debitar saldo de usuario con transacción, para una compra
     * @param connection
     * @param idUsuario
     * @param monto
     * @return
     * @throws SQLException
     * @throws SaldoInsuficienteException 
     */
    public boolean debitarSaldo(Connection connection, int idUsuario, double monto) 
            throws SQLException, SaldoInsuficienteException {
        
        // Obtener y bloquear saldo actual
        double saldoActual = obtenerSaldoUsuarioConLock(connection, idUsuario);
        
        if (saldoActual < monto) {
            throw new SaldoInsuficienteException(
                String.format("Saldo insuficiente. Saldo actual: $%.2f, Monto a debitar: $%.2f", 
                             saldoActual, monto)
            );
        }
        
        double nuevoSaldo = saldoActual - monto;
        
        return actualizarSaldoUsuario(connection, idUsuario, nuevoSaldo);
    }
    
    /**
     * Acreditar saldo a usuario con transacción
     * @param connection
     * @param idUsuario
     * @param monto
     * @return
     * @throws SQLException 
     */
    public boolean acreditarSaldo(Connection connection, int idUsuario, double monto) 
            throws SQLException {
        
        // Obtener y bloquear saldo actual
        double saldoActual = obtenerSaldoUsuarioConLock(connection, idUsuario);
        
        double nuevoSaldo = saldoActual + monto;
        
        return actualizarSaldoUsuario(connection, idUsuario, nuevoSaldo);
    }
    
}
