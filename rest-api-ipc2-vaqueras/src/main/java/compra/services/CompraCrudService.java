/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.services;


import compra.dtos.NewCompraRequest;
import compra.models.Compra;
import db.CompraDB;
import exceptions.CompraDataInvalidException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
import exceptions.ComisionNoEncontradaException;
import exceptions.EdadNoValidaException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

import java.util.Map;

/**
 *
 * @author andy
 */
public class CompraCrudService {
    
    private final CompraDB compraDB;
    private final TransaccionService transaccionService;
    private final CalculadoraComisionService calculadoraComision;
    
    public CompraCrudService() {
        this.compraDB = new CompraDB();
        this.transaccionService = new TransaccionService();
        this.calculadoraComision = new CalculadoraComisionService();
    }
    
    /**
     * Registrar compra en la base de datos
     * @param request
     * @return
     * @throws CompraDataInvalidException
     * @throws EntityNotFoundException
     * @throws EntityAlreadyExistsException
     * @throws SaldoInsuficienteException
     * @throws ComisionNoEncontradaException 
     */
    public Compra createCompra(NewCompraRequest request) throws CompraDataInvalidException, EntityNotFoundException, 
        EntityAlreadyExistsException, SaldoInsuficienteException, ComisionNoEncontradaException, EdadNoValidaException {
        try {
            if (request.getFecha_compra() == null) {
                throw new CompraDataInvalidException("La fecha de compra es requerida");
            }
            
            return transaccionService.procesarCompraConTransaccion(
                request.getId_usuario(),
                request.getId_videojuego(),
                request.getFecha_compra()
            );
            
        } catch (SQLException e) {
            throw new RuntimeException("Error en transacción de compra: " + e.getMessage(), e);
        }
    }    
    
    /**
     * Buscar una compra por id
     * @param id
     * @return
     * @throws EntityNotFoundException 
     */
    public Compra getCompraById(int id) throws EntityNotFoundException {
        Compra compra = compraDB.getCompraById(id);
        if (compra == null) {
            throw new EntityNotFoundException("Compra no encontrada con ID: " + id);
        }
        return compra;
    }
    /**
     * Compras realizadas por un usuario
     * @param idUsuario
     * @return 
     */
    public List<Compra> getComprasByUsuario(int idUsuario) {
        return compraDB.getComprasByUsuario(idUsuario);
    }
    
    public List<Compra> getComprasByVideojuego(int idVideojuego) {
        return compraDB.getComprasByVideojuego(idVideojuego);
    }
    /**
     * Todas las compras registradas en base de datos
     * @return 
     */
    public List<Compra> getAllCompras() {
        return compraDB.getAllCompras();
    }
    

    
    public boolean deleteCompra(int id) throws EntityNotFoundException {
        // Primero obtener la compra para verificar que existe
        Compra compra = getCompraById(id);
        
        // Luego eliminarla
        boolean eliminado = compraDB.deleteCompra(id);
        
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar la compra");
        }
        
        return eliminado;
    }
    
    public double getTotalGastadoPorUsuario(int idUsuario) {
        return compraDB.getTotalGastadoPorUsuario(idUsuario);
    }
    
    public double getIngresosPorVideojuego(int idVideojuego) {
        return compraDB.getIngresosPorVideojuego(idVideojuego);
    }
    
    public Map<String, Object> getEstadisticasCompras() {
        return compraDB.getEstadisticasCompras();
    }
    
    public boolean existeCompraUsuarioVideojuego(int idUsuario, int idVideojuego) {
        return compraDB.existeCompraUsuarioVideojuego(idUsuario, idVideojuego);
    }
    
}