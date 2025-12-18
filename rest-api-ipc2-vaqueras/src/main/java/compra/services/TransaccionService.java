/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.services;


import compra.models.Compra;
import videojuego.models.Videojuego;
import conexion.DBConnectionSingleton;
import db.CompraDB;
import db.TransaccionDB;
import db.UsersDB;
import db.VideojuegoDB;
import exceptions.ComisionNoEncontradaException;
import exceptions.CompraDataInvalidException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import java.util.Optional;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class TransaccionService {
    
    private final TransaccionDB transaccionDB;
    private final CompraDB compraDB;
    private final UsersDB usuarioDB;
    private final VideojuegoDB videojuegoDB;
    private final CalculadoraComisionService calculadoraComision;
    
    public TransaccionService() {
        this.transaccionDB = new TransaccionDB();
        this.compraDB = new CompraDB();
        this.usuarioDB = new UsersDB();
        this.videojuegoDB = new VideojuegoDB();
        this.calculadoraComision = new CalculadoraComisionService();
    }
    
    /**
     * Procesar compra completa con comisión automática
     * @param idUsuario
     * @param idVideojuego
     * @param fechaCompra
     * @return
     * @throws EntityNotFoundException
     * @throws SaldoInsuficienteException
     * @throws EntityAlreadyExistsException
     * @throws CompraDataInvalidException
     * @throws ComisionNoEncontradaException
     * @throws SQLException 
     */
    public Compra procesarCompraConTransaccion(int idUsuario, int idVideojuego, Date fechaCompra) throws EntityNotFoundException, 
            SaldoInsuficienteException, EntityAlreadyExistsException, CompraDataInvalidException, ComisionNoEncontradaException, SQLException {
        
        Connection connection = null;
        
        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false); // Iniciar transacción
            
            validarPreTransaccion(idUsuario, idVideojuego, fechaCompra);
            
            Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
            Usuario usuario = optionalUsuario.orElse(null);

            Videojuego videojuego = videojuegoDB.getVideojuegoById(idVideojuego, false);
            double precio = videojuego.getPrecio().doubleValue();

            // Calcular comisión 
            double montoComision = calculadoraComision.calcularMontoComision(
                idVideojuego, precio, fechaCompra
            );
            
            if (!transaccionDB.verificarSaldoSuficiente(connection, idUsuario, precio)) {
                throw new SaldoInsuficienteException(
                    String.format("Saldo insuficiente. Monto requerido: $%.2f", 
                                 videojuego.getPrecio())
                );
            }
            
            
            Compra compra = new Compra();
            compra.setId_usuario(idUsuario);
            compra.setId_videojuego(idVideojuego);
            compra.setMonto_pago(precio); 
            compra.setFecha_compra(fechaCompra);
            compra.setComision_aplicada(montoComision); // Comisión extraida de las comisiones de empresa
            
            int idCompra = transaccionDB.insertarCompra(connection, compra);
            compra.setId_compra(idCompra);
            
            // Actualizar saldo del usuario
            double nuevoSaldo = usuario.getSaldo_cartera() - precio;
            transaccionDB.actualizarSaldoUsuario(connection, idUsuario, nuevoSaldo);
            
            // Confirmar transacción
            connection.commit();
            compra.setUsuario(usuario);
            compra.setVideojuego(videojuego);
            
            return compra;
            
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
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
     * Validaciones antes de iniciar transacción
     * @param idUsuario
     * @param idVideojuego
     * @param fechaCompra
     * @throws EntityNotFoundException
     * @throws EntityAlreadyExistsException
     * @throws CompraDataInvalidException
     * @throws ComisionNoEncontradaException 
     */
    private void validarPreTransaccion(int idUsuario, int idVideojuego, Date fechaCompra) 
            throws EntityNotFoundException, EntityAlreadyExistsException, CompraDataInvalidException, 
                   ComisionNoEncontradaException {
        
        Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
        Usuario usuario = optionalUsuario.orElse(null);

        if (usuario == null) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        Videojuego videojuego = videojuegoDB.getVideojuegoById(idVideojuego, false);
        if (videojuego == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + idVideojuego);
        }
        
        if (compraDB.existeCompraUsuarioVideojuego(idUsuario, idVideojuego)) {
            throw new EntityAlreadyExistsException(
                "El usuario ya ha comprado este videojuego anteriormente"
            );
        }
        
        if (fechaCompra == null) {
            throw new CompraDataInvalidException("La fecha de compra es requerida");
        }
        

        
        // Validar que haya comisión vigente
        int idEmpresa = videojuegoDB.obtenerIdEmpresaDelVideojuego(idVideojuego);
        if (!calculadoraComision.existeComisionVigente(idEmpresa, fechaCompra)) {
            throw new ComisionNoEncontradaException(
                "No hay comisión configurada para esta empresa en la fecha seleccionada"
            );
        }
    }
    
    
    
}