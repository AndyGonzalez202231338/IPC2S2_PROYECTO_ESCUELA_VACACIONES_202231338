/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.services;


import db.ComisionDB;
import db.VideojuegoDB;
import exceptions.ComisionNoEncontradaException;

import java.sql.Date;

/**
 *
 * @author andy
 */
public class CalculadoraComisionService {
    
    private final ComisionDB comisionDB;
    private final VideojuegoDB videojuegoDB;
    
    public CalculadoraComisionService() {
        this.comisionDB = new ComisionDB();
        this.videojuegoDB = new VideojuegoDB();
    }
    
    /**
     * Calcular comisión para un videojuego en una fecha específica, segun su comisión asignada en el rango de fechas
     * @param idVideojuego
     * @param fechaCompra
     * @return
     * @throws ComisionNoEncontradaException 
     */
    public double calcularComision(int idVideojuego, java.util.Date fechaCompra) 
            throws ComisionNoEncontradaException {
        
        int idEmpresa = videojuegoDB.obtenerIdEmpresaDelVideojuego(idVideojuego);
        if (idEmpresa == -1) {
            throw new ComisionNoEncontradaException(
                "No se encontró la empresa para el videojuego ID: " + idVideojuego
            );
        }
        
        // Obtener porcentaje de comisión vigente
        Date fechaRango = new Date(fechaCompra.getTime());
        Double porcentajeComision = comisionDB.obtenerComisionVigente(idEmpresa, fechaRango);
        
        if (porcentajeComision == null) {
            throw new ComisionNoEncontradaException(
                "No hay comisión vigente para la empresa ID: " + idEmpresa + 
                " en la fecha: " + fechaRango
            );
        }
        
        return porcentajeComision;
    }
    
    /**
     * Exttaer el monto de comisión cobrada por compra de videojuego
     * @param idVideojuego
     * @param precio
     * @param fechaCompra
     * @return
     * @throws ComisionNoEncontradaException 
     */
    public double calcularMontoComision(int idVideojuego, double precio, java.util.Date fechaCompra) 
            throws ComisionNoEncontradaException {

        double porcentajeComision = calcularComision(idVideojuego, fechaCompra);        
        return porcentajeComision;
    }
    
    /**
     * Calcular comisión para una empresa en una fecha de comisiones que tiene la empresa
     * @param idEmpresa
     * @param fecha
     * @return
     * @throws ComisionNoEncontradaException 
     */
    public double calcularComisionParaEmpresa(int idEmpresa, java.util.Date fecha) 
            throws ComisionNoEncontradaException {
        
        Date fechaRango = new Date(fecha.getTime());
        Double porcentajeComision = comisionDB.obtenerComisionVigente(idEmpresa, fechaRango);
        
        if (porcentajeComision == null) {
            throw new ComisionNoEncontradaException(
                "No hay comisión vigente para la empresa ID: " + idEmpresa + 
                " en la fecha: " + fechaRango
            );
        }
        
        return porcentajeComision;
    }
    
    /**
     * Verificar si hay comisión vigente, existe comision con la fecha de compra
     * @param idEmpresa
     * @param fecha
     * @return 
     */
    public boolean existeComisionVigente(int idEmpresa, java.util.Date fecha) {
        Date fechaSQL = new Date(fecha.getTime());
        Double porcentaje = comisionDB.obtenerComisionVigente(idEmpresa, fechaSQL);
        return porcentaje != null;
    }
    
}