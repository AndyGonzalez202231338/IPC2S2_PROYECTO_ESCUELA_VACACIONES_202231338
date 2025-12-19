package db;

import comentario.model.RespuestaComentario;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RespuestaComentarioDB {
    
    private static final String CREAR_RESPUESTA_COMENTARIO_QUERY = 
        "INSERT INTO respuesta_comentario (id_comentario_padre, id_usuario, comentario, fecha_hora) VALUES (?, ?, ?, ?)";
    
    private static final String OBTENER_RESPUESTA_POR_ID_QUERY = 
        "SELECT id_respuesta, id_comentario_padre, id_usuario, comentario, fecha_hora FROM respuesta_comentario WHERE id_respuesta = ?";
    
    private static final String OBTENER_RESPUESTAS_POR_COMENTARIO_PADRE_QUERY = 
        "SELECT id_respuesta, id_comentario_padre, id_usuario, comentario, fecha_hora FROM respuesta_comentario WHERE id_comentario_padre = ? ORDER BY fecha_hora ASC";
    
    private static final String OBTENER_RESPUESTAS_POR_USUARIO_QUERY = 
        "SELECT id_respuesta, id_comentario_padre, id_usuario, comentario, fecha_hora FROM respuesta_comentario WHERE id_usuario = ? ORDER BY fecha_hora DESC";
    
    private static final String OBTENER_RESPUESTAS_POR_VIDEOJUEGO_QUERY = 
        "SELECT rc.id_respuesta, rc.id_comentario_padre, rc.id_usuario, rc.comentario, rc.fecha_hora " +
        "FROM respuesta_comentario rc " +
        "JOIN comentario c ON rc.id_comentario_padre = c.id_comentario " +
        "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca " +
        "WHERE bu.id_videojuego = ? ORDER BY rc.fecha_hora DESC";
    
    private static final String OBTENER_TOTAL_RESPUESTAS_POR_COMENTARIO_QUERY = 
        "SELECT COUNT(*) as total FROM respuesta_comentario WHERE id_comentario_padre = ?";
    
    private static final String OBTENER_TOTAL_RESPUESTAS_POR_VIDEOJUEGO_QUERY = 
        "SELECT COUNT(*) as total " +
        "FROM respuesta_comentario rc " +
        "JOIN comentario c ON rc.id_comentario_padre = c.id_comentario " +
        "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca " +
        "WHERE bu.id_videojuego = ?";
    
    private static final String VERIFICAR_EXISTENCIA_COMENTARIO_PADRE_QUERY = 
        "SELECT COUNT(*) as count FROM comentario WHERE id_comentario = ?";
    
    /**
     * Crear una nueva respuesta a comentario
     * @param respuesta
     * @return 
     */
    public RespuestaComentario createRespuestaComentario(RespuestaComentario respuesta) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement insert = connection.prepareStatement(CREAR_RESPUESTA_COMENTARIO_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            insert.setInt(1, respuesta.getId_comentario_padre());
            insert.setInt(2, respuesta.getId_usuario());
            insert.setString(3, respuesta.getComentario());
            insert.setTimestamp(4, Timestamp.valueOf(respuesta.getFecha_hora()));
            
            int affectedRows = insert.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        respuesta.setId_respuesta(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear respuesta a comentario: " + e.getMessage(), e);
        }
        
        return respuesta;
    }
    
    /**
     * Obtiene una respuesta por ID
     * @param idRespuesta
     * @return 
     */
    public RespuestaComentario getRespuestaById(int idRespuesta) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_RESPUESTA_POR_ID_QUERY)) {
            query.setInt(1, idRespuesta);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToRespuestaComentario(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las respuestas de un comentario padre
     * @param idComentarioPadre
     * @return 
     */
    public List<RespuestaComentario> getRespuestasPorComentarioPadre(int idComentarioPadre) {
        List<RespuestaComentario> respuestas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_RESPUESTAS_POR_COMENTARIO_PADRE_QUERY)) {
            query.setInt(1, idComentarioPadre);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                RespuestaComentario respuesta = mapResultSetToRespuestaComentario(resultSet);
                respuestas.add(respuesta);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener respuestas por comentario padre: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    /**
     * Obtiene todas las respuestas de un usuario
     * @param idUsuario
     * @return 
     */
    public List<RespuestaComentario> getRespuestasPorUsuario(int idUsuario) {
        List<RespuestaComentario> respuestas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_RESPUESTAS_POR_USUARIO_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                RespuestaComentario respuesta = mapResultSetToRespuestaComentario(resultSet);
                respuestas.add(respuesta);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener respuestas por usuario: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    public List<RespuestaComentario> getRespuestasPorVideojuego(int idVideojuego) {
        List<RespuestaComentario> respuestas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_RESPUESTAS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                RespuestaComentario respuesta = mapResultSetToRespuestaComentario(resultSet);
                respuestas.add(respuesta);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener respuestas por videojuego: " + e.getMessage());
        }
        
        return respuestas;
    }
    
    /**
     * Obtiene el total de respuestas de un comentario
     * @param idComentarioPadre
     * @return 
     */
    public int getTotalRespuestasPorComentario(int idComentarioPadre) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TOTAL_RESPUESTAS_POR_COMENTARIO_QUERY)) {
            query.setInt(1, idComentarioPadre);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener total de respuestas por comentario: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Obtiene el total de respuestas de un videojuego
     * @param idVideojuego
     * @return 
     */
    public int getTotalRespuestasPorVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TOTAL_RESPUESTAS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener total de respuestas por videojuego: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Verifica si el comentario padre existe
     */
    public boolean existeComentarioPadre(int idComentarioPadre) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(VERIFICAR_EXISTENCIA_COMENTARIO_PADRE_QUERY)) {
            query.setInt(1, idComentarioPadre);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar existencia de comentario padre: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto RespuestaComentario
     */
    private RespuestaComentario mapResultSetToRespuestaComentario(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
        
        return new RespuestaComentario(
            resultSet.getInt("id_comentario_padre"),
            resultSet.getInt("id_usuario"),
            resultSet.getString("comentario"),
            fechaHora
        );
    }
}