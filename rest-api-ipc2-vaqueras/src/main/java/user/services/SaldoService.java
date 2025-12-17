/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.services;

import db.TransaccionDB;
import db.UsersDB;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
import user.dtos.OperacionSaldoResponse;
import user.models.Usuario;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

/**
 *
 * @author andy
 */
public class SaldoService {
    
    private final TransaccionDB transaccionDB;
    private final UsersDB usuarioDB;
    
    public SaldoService() {
        this.transaccionDB = new TransaccionDB();
        this.usuarioDB = new UsersDB();
    }
    
    /**
     * Debitar saldo de usuario
     * @param idUsuario
     * @param monto
     * @return
     * @throws EntityNotFoundException
     * @throws SaldoInsuficienteException 
     */
    public OperacionSaldoResponse debitarSaldo(int idUsuario, double monto) 
            throws EntityNotFoundException, SaldoInsuficienteException {
        
        Connection connection = null;
        
        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
            if (!optionalUsuario.isPresent()) {
                throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
            }
            
            if (monto <= 0) {
                throw new IllegalArgumentException("El monto a debitar debe ser mayor a 0");
            }
            
            transaccionDB.debitarSaldo(connection, idUsuario, monto);
            
            // Confirmar transacción
            connection.commit();
            
            // Obtener nuevo saldo
            Usuario usuarioActualizado = usuarioDB.getById(idUsuario).get();
            
            return new OperacionSaldoResponse(idUsuario, usuarioActualizado.getSaldo_cartera());
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Error al debitar saldo: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Acreditar saldo a usuario
     * @param idUsuario
     * @param monto
     * @return
     * @throws EntityNotFoundException 
     */
    public OperacionSaldoResponse acreditarSaldo(int idUsuario, double monto) 
            throws EntityNotFoundException {
        
        Connection connection = null;
        
        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);
            
            Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
            if (!optionalUsuario.isPresent()) {
                throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
            }
            
            if (monto <= 0) {
                throw new IllegalArgumentException("El monto a acreditar debe ser mayor a 0");
            }
            
            
            transaccionDB.acreditarSaldo(connection, idUsuario, monto);
            
            // Confirmar transacción
            connection.commit();
            
            // Obtener nuevo saldo
            Usuario usuarioActualizado = usuarioDB.getById(idUsuario).get();
            
            return new OperacionSaldoResponse(idUsuario, usuarioActualizado.getSaldo_cartera());
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new RuntimeException("Error al acreditar saldo: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Obtener saldo de usuario
     * @param idUsuario
     * @return
     * @throws EntityNotFoundException 
     */
    public OperacionSaldoResponse obtenerSaldo(int idUsuario) throws EntityNotFoundException {
        Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
        
        if (!optionalUsuario.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        Usuario usuario = optionalUsuario.get();
        return new OperacionSaldoResponse(idUsuario, usuario.getSaldo_cartera());
    }
}