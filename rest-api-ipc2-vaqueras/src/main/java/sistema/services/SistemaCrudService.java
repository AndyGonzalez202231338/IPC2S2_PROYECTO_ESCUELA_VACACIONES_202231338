/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.services;

import sistema.dtos.UpdateConfiguracionRequest;
import sistema.models.ConfiguracionSistema;
import db.SistemaDB;
import exceptions.EntityNotFoundException;
import exceptions.ConfiguracionDataInvalidException;
import java.util.Date;
import java.util.List;
/**
 *
 * @author andy
 */
public class SistemaCrudService {
    
    private final SistemaDB sistemaDB;
    
    public SistemaCrudService() {
        this.sistemaDB = new SistemaDB();
    }
    

    public List<ConfiguracionSistema> getAllConfiguraciones() {
        return sistemaDB.getAllConfiguraciones();
    }
    
    /**
     * Obtiene configuraciones activas
     * @return 
     */
    public List<ConfiguracionSistema> getConfiguracionesActivas() {
        //return sistemaDB.getConfiguracionesActivas();
        return null;
    }
    
    /**
     * Busca configuraciones
     * @param searchTerm
     * @return 
     */
    public List<ConfiguracionSistema> searchConfiguraciones(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllConfiguraciones();
        }
        //return sistemaDB.searchConfiguraciones(searchTerm.trim());
        return null;
    }
    

    public ConfiguracionSistema getConfiguracionById(int id) throws EntityNotFoundException {
        ConfiguracionSistema config = sistemaDB.getConfiguracionById(id);
        if (config == null) {
            throw new EntityNotFoundException("Configuración no encontrada con ID: " + id);
        }
        return config;
    }
    

    public ConfiguracionSistema getConfiguracionByNombre(String nombre) throws EntityNotFoundException {
        ConfiguracionSistema config = sistemaDB.getConfiguracionByNombre(nombre);
        if (config == null) {
            throw new EntityNotFoundException("Configuración no encontrada: " + nombre);
        }
        return config;
    }
    
    /**
     * Obtiene el valor de una configuración por nombre
     * @param nombre
     * @return
     * @throws EntityNotFoundException 
     */
    public String getValorConfiguracion(String nombre) throws EntityNotFoundException {
        ConfiguracionSistema config = getConfiguracionByNombre(nombre);
        return config.getValor();
    }
    
    /**
     * Valida los datos para actualizar una configuración
     * @param valor
     * @param fechaFinal
     * @throws ConfiguracionDataInvalidException 
     */
    private void validarDatosConfiguracion(String valor, Date fechaFinal) throws ConfiguracionDataInvalidException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ConfiguracionDataInvalidException("El valor de la configuración es requerido");
        }
        
        // Validaciones específicas según el tipo de configuración
        if (valor.trim().length() > 255) {
            throw new ConfiguracionDataInvalidException("El valor no puede exceder 255 caracteres");
        }
    }
    
    public ConfiguracionSistema updateConfiguracion(int id, UpdateConfiguracionRequest configRequest) 
            throws EntityNotFoundException, ConfiguracionDataInvalidException {
        
        System.out.println("Actualizando configuración ID: " + id + " con valor: " + configRequest.getValor());
        
        ConfiguracionSistema configExistente = getConfiguracionById(id);
        
        // Validar datos
        validarDatosConfiguracion(configRequest.getValor(), configRequest.getFecha_final());
        
        
        String tipoConfiguracion = configExistente.getConfiguracion();
        String nuevoValor = configRequest.getValor().trim();
        
        if (tipoConfiguracion.equals("COMISION_GLOBAL")) {
            // Validar que sea un número decimal válido
            try {
                double comision = Double.parseDouble(nuevoValor);
                if (comision < 0 || comision > 100) {
                    throw new ConfiguracionDataInvalidException(
                        "La comisión debe ser un porcentaje entre 0 y 100"
                    );
                }
            } catch (NumberFormatException e) {
                throw new ConfiguracionDataInvalidException(
                    "La comisión debe ser un número válido"
                );
            }
        } else if (tipoConfiguracion.equals("EDAD_ADOLESCENTES") || 
                   tipoConfiguracion.equals("MAX_MIEMBROS_GRUPO")) {
            // Validar que sea un número entero válido
            try {
                int numero = Integer.parseInt(nuevoValor);
                if (numero <= 0) {
                    throw new ConfiguracionDataInvalidException(
                        "El valor debe ser un número entero positivo"
                    );
                }
            } catch (NumberFormatException e) {
                throw new ConfiguracionDataInvalidException(
                    "El valor debe ser un número entero válido"
                );
            }
        }
        
        
        boolean actualizado = sistemaDB.updateConfiguracion(
            id, 
            nuevoValor, 
            configRequest.getFecha_final()
        );
        
        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar la configuración");
        }
        
        ConfiguracionSistema configActualizada = getConfiguracionById(id);
        
        System.out.println("Configuración actualizada exitosamente: " + 
                          configActualizada.getConfiguracion() + " = " + 
                          configActualizada.getValor());
        
        return configActualizada;
    }
    
    /**
     * Método de conveniencia para obtener valores específicos
     * @return 
     */
    public double getComisionGlobal() {
        try {
            String valor = sistemaDB.getValorConfiguracion("COMISION_GLOBAL");
            return valor != null ? Double.parseDouble(valor) : 15.0; // Valor por defecto
        } catch (NumberFormatException e) {
            return 15.0; 
        }
    }
    
    public int getEdadMinimaAdolescentes() {
        try {
            String valor = sistemaDB.getValorConfiguracion("EDAD_ADOLESCENTES");
            return valor != null ? Integer.parseInt(valor) : 16; // Valor por defecto
        } catch (NumberFormatException e) {
            return 16; 
        }
    }
    
    public int getMaxMiembrosGrupo() {
        try {
            String valor = sistemaDB.getValorConfiguracion("MAX_MIEMBROS_GRUPO");
            return valor != null ? Integer.parseInt(valor) : 6; // Valor por defecto
        } catch (NumberFormatException e) {
            return 6; 
        }
    }
}