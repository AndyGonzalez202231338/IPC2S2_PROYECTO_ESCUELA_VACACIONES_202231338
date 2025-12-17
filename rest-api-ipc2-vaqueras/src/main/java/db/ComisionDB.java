/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import conexion.DBConnectionSingleton;
import empresa.models.Comision;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author andy
 */
public class ComisionDB {
    
    private static final String OBTENER_COMISION_VIGENTE_QUERY = 
        "SELECT c.porcentaje " +
        "FROM comision c " +
        "WHERE c.id_empresa = ? " +
        "AND c.fecha_inicio <= ? " +
        "AND (c.fecha_final IS NULL OR c.fecha_final >= ?) " +
        "ORDER BY c.fecha_inicio DESC " +
        "LIMIT 1";
    
    private static final String OBTENER_COMISION_POR_ID_QUERY = 
        "SELECT id_comision, id_empresa, porcentaje, fecha_inicio, fecha_final " +
        "FROM comision WHERE id_comision = ?";
    
    private static final String CREAR_COMISION_QUERY = 
        "INSERT INTO comision (id_empresa, porcentaje, fecha_inicio, fecha_final) " +
        "VALUES (?, ?, ?, ?)";
    
    /**
     * Obtener porcentaje de comisión vigente para una empresa en una fecha específica
     * @param idEmpresa
     * @param fecha
     * @return 
     */
    public Double obtenerComisionVigente(int idEmpresa, Date fecha) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMISION_VIGENTE_QUERY)) {
            query.setInt(1, idEmpresa);
            query.setDate(2, fecha);
            query.setDate(3, fecha);
            
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getDouble("porcentaje");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null; // No hay comisión vigente
    }
    

    
}
