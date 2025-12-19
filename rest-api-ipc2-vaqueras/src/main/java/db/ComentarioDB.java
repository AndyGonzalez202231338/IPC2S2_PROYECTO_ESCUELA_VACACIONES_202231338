package db;

import comentario.model.Comentario;
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

public class ComentarioDB {
    
    private static final String CREAR_COMENTARIO_QUERY = 
        "INSERT INTO comentario (id_usuario, id_biblioteca, comentario, fecha_hora) VALUES (?, ?, ?, ?)";
    
    private static final String OBTENER_COMENTARIO_POR_ID_QUERY = 
        "SELECT id_comentario, id_usuario, id_biblioteca, comentario, fecha_hora FROM comentario WHERE id_comentario = ?";
    
    private static final String OBTENER_COMENTARIOS_POR_USUARIO_QUERY = 
        "SELECT id_comentario, id_usuario, id_biblioteca, comentario, fecha_hora FROM comentario WHERE id_usuario = ? ORDER BY fecha_hora DESC";
    
    private static final String OBTENER_COMENTARIOS_POR_BIBLIOTECA_QUERY = 
        "SELECT id_comentario, id_usuario, id_biblioteca, comentario, fecha_hora FROM comentario WHERE id_biblioteca = ? ORDER BY fecha_hora DESC";
    
    private static final String OBTENER_COMENTARIOS_POR_VIDEOJUEGO_QUERY = 
        "SELECT c.id_comentario, c.id_usuario, c.id_biblioteca, c.comentario, c.fecha_hora " +
        "FROM comentario c " +
        "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca " +
        "WHERE bu.id_videojuego = ? ORDER BY c.fecha_hora DESC";
    
    private static final String OBTENER_TOTAL_COMENTARIOS_POR_VIDEOJUEGO_QUERY = 
        "SELECT COUNT(*) as total " +
        "FROM comentario c " +
        "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca " +
        "WHERE bu.id_videojuego = ?";
    
    private static final String OBTENER_COMENTARIOS_POR_USUARIO_Y_VIDEOJUEGO_QUERY = 
        "SELECT c.id_comentario, c.id_usuario, c.id_biblioteca, c.comentario, c.fecha_hora " +
        "FROM comentario c " +
        "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca " +
        "WHERE c.id_usuario = ? AND bu.id_videojuego = ? ORDER BY c.fecha_hora DESC";
    
    /**
     * Crear un nuevo comentario
     * @param comentario
     * @return 
     */
    public Comentario createComentario(Comentario comentario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement insert = connection.prepareStatement(CREAR_COMENTARIO_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            insert.setInt(1, comentario.getId_usuario());
            insert.setInt(2, comentario.getId_biblioteca());
            insert.setString(3, comentario.getComentario());
            insert.setTimestamp(4, Timestamp.valueOf(comentario.getFecha_hora()));
            
            int affectedRows = insert.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comentario.setId_comentario(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear comentario: " + e.getMessage(), e);
        }
        
        return comentario;
    }
    
    /**
     * Obtiene un comentario por ID
     * @param idComentario
     * @return 
     */
    public Comentario getComentarioById(int idComentario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMENTARIO_POR_ID_QUERY)) {
            query.setInt(1, idComentario);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToComentario(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todos los comentarios de un usuario
     * @param idUsuario
     * @return 
     */
    public List<Comentario> getComentariosPorUsuario(int idUsuario) {
        List<Comentario> comentarios = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMENTARIOS_POR_USUARIO_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Comentario comentario = mapResultSetToComentario(resultSet);
                comentarios.add(comentario);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener comentarios por usuario: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    /**
     * Obtiene todos los comentarios de una biblioteca específica
     * @param idBiblioteca
     * @return 
     */
    public List<Comentario> getComentariosPorBiblioteca(int idBiblioteca) {
        List<Comentario> comentarios = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMENTARIOS_POR_BIBLIOTECA_QUERY)) {
            query.setInt(1, idBiblioteca);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Comentario comentario = mapResultSetToComentario(resultSet);
                comentarios.add(comentario);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener comentarios por biblioteca: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    /**
     * Obtiene todos los comentarios de un videojuego específico
     * @param idVideojuego
     * @return 
     */
    public List<Comentario> getComentariosPorVideojuego(int idVideojuego) {
        List<Comentario> comentarios = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMENTARIOS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Comentario comentario = mapResultSetToComentario(resultSet);
                comentarios.add(comentario);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener comentarios por videojuego: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    /**
     * Obtiene los comentarios de un usuario específico para un videojuego específico
     * @param idUsuario
     * @param idVideojuego
     * @return 
     */
    public List<Comentario> getComentariosPorUsuarioYVideojuego(int idUsuario, int idVideojuego) {
        List<Comentario> comentarios = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMENTARIOS_POR_USUARIO_Y_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idUsuario);
            query.setInt(2, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Comentario comentario = mapResultSetToComentario(resultSet);
                comentarios.add(comentario);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener comentarios por usuario y videojuego: " + e.getMessage());
        }
        
        return comentarios;
    }
    
    /**
     * Obtiene el total de comentarios de un videojuego
     * @param idVideojuego
     * @return 
     */
    public int getTotalComentariosPorVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TOTAL_COMENTARIOS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener total de comentarios por videojuego: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Mapea un ResultSet a un objeto Comentario
     * @param resultSet
     * @return
     * @throws SQLException 
     */
    private Comentario mapResultSetToComentario(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
        
        return new Comentario(
            resultSet.getInt("id_usuario"),
            resultSet.getInt("id_biblioteca"),
            resultSet.getString("comentario"),
            fechaHora
        );
    }
}