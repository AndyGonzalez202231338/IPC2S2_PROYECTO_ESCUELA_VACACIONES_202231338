/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author andy
 */
public class PopularidadDB {
    
    /**
     * Consulta optimizada para obtener estadísticas de calificaciones por videojuego
     * @param idVideojuego
     * @return
     * @throws SQLException 
     */
    public ResultSet getEstadisticasCalificacionesVideojuego(int idVideojuego) throws SQLException {
        String query = 
            "SELECT " +
            "    bu.id_videojuego, " +
            "    COUNT(c.id_calificacion) as total_calificaciones, " +
            "    AVG(c.calificacion) as promedio_calificacion, " +
            "    MIN(c.calificacion) as minima_calificacion, " +
            "    MAX(c.calificacion) as maxima_calificacion, " +
            "    STDDEV(c.calificacion) as desviacion_estandar, " +
            "    COUNT(CASE WHEN c.fecha_hora >= DATE_SUB(NOW(), INTERVAL 7 DAY) THEN 1 END) as calificaciones_recientes " +
            "FROM biblioteca_usuario bu " +
            "LEFT JOIN calificacion c ON bu.id_biblioteca = c.id_biblioteca " +
            "WHERE bu.id_videojuego = ? " +
            "GROUP BY bu.id_videojuego";
        
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, idVideojuego);
        
        return pstmt.executeQuery();
    }
    
    /**
     * Obtiene los videojuegos más populares usando algoritmo de ranking
     * @param limite
     * @return
     * @throws SQLException 
     */
    public ResultSet getTopVideojuegosPopulares(int limite) throws SQLException {
        String query = 
            "SELECT " +
            "    bu.id_videojuego, " +
            "    COUNT(c.id_calificacion) as total_calificaciones, " +
            "    AVG(c.calificacion) as promedio_calificacion, " +
            "    " +
            "    -- Fórmula de popularidad: ponderación entre promedio y cantidad " +
            "    (AVG(c.calificacion) * 0.7 + " +
            "     (CASE WHEN COUNT(c.id_calificacion) > 0 THEN " +
            "        (1 - EXP(-COUNT(c.id_calificacion) / 10.0)) * 0.3 " +
            "     ELSE 0 END)) as score_popularidad " +
            "FROM biblioteca_usuario bu " +
            "LEFT JOIN calificacion c ON bu.id_biblioteca = c.id_biblioteca " +
            "GROUP BY bu.id_videojuego " +
            "HAVING COUNT(c.id_calificacion) > 0 " +
            "ORDER BY score_popularidad DESC " +
            "LIMIT ?";
        
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        PreparedStatement pstmt = connection.prepareStatement(query);
        pstmt.setInt(1, limite);
        
        return pstmt.executeQuery();
    }
}
