/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import sistema.models.ConfiguracionSistema;

/**
 *
 * @author andy
 */
public class SistemaDB {
        private static final String OBTENER_TODAS_CONFIGURACIONES_QUERY = 
        "SELECT id_configuracion, configuracion, valor, descripcion, " +
        "fecha_inicio, fecha_final FROM sistema ORDER BY configuracion";
    
    private static final String OBTENER_CONFIGURACION_POR_ID_QUERY = 
        "SELECT id_configuracion, configuracion, valor, descripcion, " +
        "fecha_inicio, fecha_final FROM sistema WHERE id_configuracion = ?";
    
    private static final String OBTENER_CONFIGURACION_POR_NOMBRE_QUERY = 
        "SELECT id_configuracion, configuracion, valor, descripcion, " +
        "fecha_inicio, fecha_final FROM sistema WHERE configuracion = ?";
    
    private static final String ACTUALIZAR_CONFIGURACION_QUERY = 
        "UPDATE sistema SET valor = ?, fecha_final = ? WHERE id_configuracion = ?";
    
    private static final String OBTENER_CONFIGURACIONES_ACTIVAS_QUERY = 
        "SELECT id_configuracion, configuracion, valor, descripcion, " +
        "fecha_inicio, fecha_final FROM sistema " +
        "WHERE fecha_final IS NULL OR fecha_final > CURDATE() " +
        "ORDER BY configuracion";
    
    /**
     * Obtiene todas las configuraciones del sistema
     * @return 
     */
    public List<ConfiguracionSistema> getAllConfiguraciones() {
        List<ConfiguracionSistema> configuraciones = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_CONFIGURACIONES_QUERY)) {
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                ConfiguracionSistema config = mapResultSetToConfiguracion(resultSet);
                configuraciones.add(config);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener configuraciones del sistema: " + e.getMessage());
        }
        
        return configuraciones;
    }
    
    /**
     * Obtiene una configuración por ID
     * @param idConfiguracion
     * @return 
     */
    public ConfiguracionSistema getConfiguracionById(int idConfiguracion) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_CONFIGURACION_POR_ID_QUERY)) {
            query.setInt(1, idConfiguracion);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToConfiguracion(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene una configuración por nombre
     * @param nombreConfiguracion
     * @return 
     */
    public ConfiguracionSistema getConfiguracionByNombre(String nombreConfiguracion) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_CONFIGURACION_POR_NOMBRE_QUERY)) {
            query.setString(1, nombreConfiguracion);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToConfiguracion(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Actualiza una configuración
     * @param idConfiguracion
     * @param nuevoValor
     * @param fechaFinal
     * @return 
     */
    public boolean updateConfiguracion(int idConfiguracion, String nuevoValor, Date fechaFinal) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_CONFIGURACION_QUERY)) {
            update.setString(1, nuevoValor);
            
            if (fechaFinal != null) {
                update.setDate(2, new java.sql.Date(fechaFinal.getTime()));
            } else {
                update.setNull(2, Types.DATE);
            }
            
            update.setInt(3, idConfiguracion);
            
            int affectedRows = update.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar configuración: " + e.getMessage(), e);
        }
    }
    
    public String getValorConfiguracion(String nombreConfiguracion) {
        ConfiguracionSistema config = getConfiguracionByNombre(nombreConfiguracion);
        return config.getValor();
    }
    
        private ConfiguracionSistema mapResultSetToConfiguracion(ResultSet resultSet) throws SQLException {
        return new ConfiguracionSistema(
            resultSet.getInt("id_configuracion"),
            resultSet.getString("configuracion"),
            resultSet.getString("valor"),
            resultSet.getString("descripcion"),
            resultSet.getDate("fecha_inicio"),
            resultSet.getDate("fecha_final")
        );
    }
}
