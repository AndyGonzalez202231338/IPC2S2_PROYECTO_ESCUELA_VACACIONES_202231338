package sistema.services;

import sistema.dtos.UpdateConfiguracionRequest;
import sistema.models.ConfiguracionSistema;
import db.SistemaDB;
import exceptions.EntityNotFoundException;
import exceptions.ConfiguracionDataInvalidException;
import java.util.Date;
import java.util.List;

public class SistemaCrudService {
    
    private final SistemaDB sistemaDB;
    
    public SistemaCrudService() {
        this.sistemaDB = new SistemaDB();
    }
    
    public List<ConfiguracionSistema> getAllConfiguraciones() {
        return sistemaDB.getAllConfiguraciones();
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
    
    private void validarDatosConfiguracion(String valor, Date fechaFinal) throws ConfiguracionDataInvalidException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ConfiguracionDataInvalidException("El valor de la configuración es requerido");
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
        
        // Validaciones específicas
        if (tipoConfiguracion.equals("COMISION_GLOBAL")) {
            try {
                double nuevaComision = Double.parseDouble(nuevoValor);
                if (nuevaComision < 0 || nuevaComision > 100) {
                    throw new ConfiguracionDataInvalidException(
                        "La comisión debe ser un porcentaje entre 0 y 100"
                    );
                }
                
                double valorAnterior = Double.parseDouble(configExistente.getValor());
                
                // Solo actualizar comisiones de empresas si el valor cambió
                if (Math.abs(nuevaComision - valorAnterior) > 0.001) {
                    System.out.println("CAMBIANDO COMISIÓN GLOBAL de " + valorAnterior + "% a " + nuevaComision + "%");
                    
                    // Usar el servicio independiente para actualizar
                    GlobalComisionUpdateService updateService = new GlobalComisionUpdateService();
                    updateService.actualizarComisionGlobal(nuevaComision, configRequest.getFecha_final());
                }
                
            } catch (NumberFormatException e) {
                throw new ConfiguracionDataInvalidException(
                    "La comisión debe ser un número válido"
                );
            }
        } else if (tipoConfiguracion.equals("EDAD_ADOLESCENTES") || 
                   tipoConfiguracion.equals("MAX_MIEMBROS_GRUPO")) {
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
        
        // Guardar el nuevo valor en sistema
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
    
    public double getComisionGlobal() {
        try {
            String valor = sistemaDB.getValorConfiguracion("COMISION_GLOBAL");
            return valor != null ? Double.parseDouble(valor) : 15.0;
        } catch (NumberFormatException e) {
            return 15.0; 
        }
    }
    
    public int getEdadMinimaAdolescentes() {
        try {
            String valor = sistemaDB.getValorConfiguracion("EDAD_ADOLESCENTES");
            return valor != null ? Integer.parseInt(valor) : 16;
        } catch (NumberFormatException e) {
            return 16; 
        }
    }
    
    public int getMaxMiembrosGrupo() {
        try {
            String valor = sistemaDB.getValorConfiguracion("MAX_MIEMBROS_GRUPO");
            return valor != null ? Integer.parseInt(valor) : 6;
        } catch (NumberFormatException e) {
            return 6; 
        }
    }
}