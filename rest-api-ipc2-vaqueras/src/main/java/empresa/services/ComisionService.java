package empresa.services;

import db.EmpresaDB;
import empresa.models.Comision;
import exceptions.EntityNotFoundException;
import java.sql.Date;
import java.util.List;

public class ComisionService {
    private final EmpresaDB empresaDB;
    
    public ComisionService() {
        this.empresaDB = new EmpresaDB();
    }
    
    public Comision getComisionActualEmpresa(int idEmpresa) throws EntityNotFoundException {
        Comision comision = empresaDB.getComisionActualEmpresa(idEmpresa);
        if (comision == null) {
            throw new EntityNotFoundException("No se encontró comisión activa para la empresa ID: " + idEmpresa);
        }
        return comision;
    }
    
    public List<Comision> getTodasComisionesEmpresa(int idEmpresa) {
        return empresaDB.getTodasComisionesEmpresa(idEmpresa);
    }
    
    /**
     * Crea una nueva comisión específica para una empresa
     * Validar que no sea mayor al porcentaje global actual
     */
    public Comision crearComisionEspecifica(int idEmpresa, double porcentaje, Date fechaInicio, Date fechaFinal, 
                                            double porcentajeGlobalActual) throws Exception {
        
        // Validar que el porcentaje no sea mayor al global
        if (porcentaje > porcentajeGlobalActual) {
            throw new Exception(
                String.format(
                    "El porcentaje de comisión (%.2f%%) no puede ser mayor al porcentaje global (%.2f%%)",
                    porcentaje, porcentajeGlobalActual
                )
            );
        }
        
        // Obtener la comisión activa actual (si existe)
        Comision comisionActual = null;
        try {
            comisionActual = getComisionActualEmpresa(idEmpresa);
        } catch (EntityNotFoundException e) {
            // No hay comisión activa, es la primera
            System.out.println("No hay comisión activa para la empresa " + idEmpresa + ", creando primera comisión");
        }
        
        //Si hay comisión activa, finalizarla UN DÍA ANTES de la nueva fecha_inicio
        if (comisionActual != null && comisionActual.isActiva()) {
            System.out.println("Comisión activa encontrada: ID " + comisionActual.getId_comision() + 
                             ", porcentaje: " + comisionActual.getPorcentaje() + "%");
            
            // Calcular fecha_final para la comisión anterior (un día antes de la nueva)
            long unDiaEnMillis = 24 * 60 * 60 * 1000; // 24 horas en milisegundos
            long fechaInicioMillis = fechaInicio.getTime();
            long fechaFinalAnteriorMillis = fechaInicioMillis - unDiaEnMillis;
            Date fechaFinalAnterior = new Date(fechaFinalAnteriorMillis);
            
            System.out.println("Finalizando comisión anterior el: " + fechaFinalAnterior);
            
            // Finalizar la comisión anterior
            boolean finalizada = empresaDB.finalizarComision(comisionActual.getId_comision(), fechaFinalAnterior);
            
            if (finalizada) {
                System.out.println(" Comisión anterior finalizada exitosamente");
            } else {
                System.out.println(" Error al finalizar comisión anterior");
            }
        }
        
        // 3. Crear nueva comisión con tipo "especifica"
        System.out.println("Creando nueva comisión específica: " + porcentaje + "% a partir de: " + fechaInicio);
        return empresaDB.crearComisionConFecha(
            idEmpresa, 
            porcentaje, 
            fechaInicio, 
            fechaFinal, 
            "especifica"
        );
    }
}