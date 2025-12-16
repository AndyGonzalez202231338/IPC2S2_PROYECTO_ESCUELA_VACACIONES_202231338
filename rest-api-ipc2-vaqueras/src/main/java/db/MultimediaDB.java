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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import videojuego.models.Multimedia;


/**
 *
 * @author andy
 */
public class MultimediaDB {
    
    private static final String CREAR_MULTIMEDIA_QUERY = 
        "INSERT INTO multimedia (id_videojuego, imagen) VALUES (?, ?)";
    
    private static final String OBTENER_MULTIMEDIA_POR_ID_QUERY = 
        "SELECT id_multimedia, id_videojuego, imagen FROM multimedia WHERE id_multimedia = ?";
    
    private static final String OBTENER_MULTIMEDIA_POR_VIDEOJUEGO_QUERY = 
        "SELECT id_multimedia, id_videojuego, imagen FROM multimedia WHERE id_videojuego = ?";
    
    private static final String OBTENER_TODAS_MULTIMEDIAS_QUERY = 
        "SELECT id_multimedia, id_videojuego, imagen FROM multimedia ORDER BY id_multimedia";
    
    private static final String ACTUALIZAR_MULTIMEDIA_QUERY = 
        "UPDATE multimedia SET id_videojuego = ?, imagen = ? WHERE id_multimedia = ?";
    
    private static final String ELIMINAR_MULTIMEDIA_QUERY = 
        "DELETE FROM multimedia WHERE id_multimedia = ?";
    
    private static final String ELIMINAR_MULTIMEDIA_POR_VIDEOJUEGO_QUERY = 
        "DELETE FROM multimedia WHERE id_videojuego = ?";
    
    private static final String CONTAR_MULTIMEDIA_POR_VIDEOJUEGO_QUERY = 
        "SELECT COUNT(*) as count FROM multimedia WHERE id_videojuego = ?";
    
    /**
     * Crear multimedia para un juego
     * @param multimedia
     * @return 
     */
    public Multimedia createMultimedia(Multimedia multimedia) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement insert = connection.prepareStatement(CREAR_MULTIMEDIA_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            insert.setInt(1, multimedia.getId_videojuego());
            
            // Convertir byte[] a Blob
            if (multimedia.getImagen() != null) {
                insert.setBytes(2, multimedia.getImagen());
            } else {
                insert.setBytes(2, new byte[0]);
            }
            
            int affectedRows = insert.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        multimedia.setId_multimedia(generatedKeys.getInt(1));
                    }
                }
            }
            
            return multimedia;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear multimedia: " + e.getMessage(), e);
        }
    }
    
    /**
     * Obtener una imagen de un videojuego por id
     * @param idMultimedia
     * @return 
     */
    public Multimedia getMultimediaById(int idMultimedia) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_MULTIMEDIA_POR_ID_QUERY)) {
            query.setInt(1, idMultimedia);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToMultimedia(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtener todas las imagenes de un videojuego
     * @param idVideojuego
     * @return 
     */
    public List<Multimedia> getMultimediaByVideojuego(int idVideojuego) {
        List<Multimedia> multimedias = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_MULTIMEDIA_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Multimedia multimedia = mapResultSetToMultimedia(resultSet);
                multimedias.add(multimedia);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return multimedias;
    }
    
    /**
     * Obtener todas las multimedias de todos los juegos
     * @return 
     */
    public List<Multimedia> getAllMultimedia() {
        List<Multimedia> multimedias = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_MULTIMEDIAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Multimedia multimedia = mapResultSetToMultimedia(resultSet);
                multimedias.add(multimedia);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return multimedias;
    }
    
    /**
     * Actualizar multimedia, cambi de imagenes
     * @param multimedia
     * @return 
     */
    public boolean updateMultimedia(Multimedia multimedia) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_MULTIMEDIA_QUERY)) {
            update.setInt(1, multimedia.getId_videojuego());
            update.setBytes(2, multimedia.getImagen());
            update.setInt(3, multimedia.getId_multimedia());
            
            int affectedRows = update.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar multimedia: " + e.getMessage(), e);
        }
    }
    
    /**
     * Eliminar iamgen por id
     * @param idMultimedia
     * @return 
     */
    public boolean deleteMultimedia(int idMultimedia) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_MULTIMEDIA_QUERY)) {
            delete.setInt(1, idMultimedia);
            
            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar multimedia: " + e.getMessage(), e);
        }
    }
    
    /**
     * Eliminar todas las multimedias de un videojuego
     * @param idVideojuego
     * @return 
     */
    public boolean deleteMultimediaByVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_MULTIMEDIA_POR_VIDEOJUEGO_QUERY)) {
            delete.setInt(1, idVideojuego);
            
            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar multimedia por videojuego: " + e.getMessage(), e);
        }
    }
    
    /**
     * Contar la cangidad de imagenes por videojuego
     * @param idVideojuego
     * @return 
     */
    public int countMultimediaByVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(CONTAR_MULTIMEDIA_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Mapear ResultSet a Multimedia
     */
    private Multimedia mapResultSetToMultimedia(ResultSet resultSet) throws SQLException {
        Multimedia multimedia = new Multimedia();
        multimedia.setId_multimedia(resultSet.getInt("id_multimedia"));
        multimedia.setId_videojuego(resultSet.getInt("id_videojuego"));
        
        // Obtener imagen como byte[]
        byte[] imagenBytes = resultSet.getBytes("imagen");
        multimedia.setImagen(imagenBytes);
        
        return multimedia;
    }
}