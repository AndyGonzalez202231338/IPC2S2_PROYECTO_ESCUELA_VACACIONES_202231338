/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import categoria.models.Categoria;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andy
 */
public class CategoriaDB {
        private static final String CREAR_CATEGORIA_QUERY = 
        "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)";
    
    private static final String OBTENER_CATEGORIA_POR_ID_QUERY = 
        "SELECT id_categoria, nombre, descripcion FROM categoria WHERE id_categoria = ?";
    
    private static final String OBTENER_TODAS_CATEGORIAS_QUERY = 
        "SELECT id_categoria, nombre, descripcion FROM categoria ORDER BY nombre";
    
    private static final String ACTUALIZAR_CATEGORIA_QUERY = 
        "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?";
    
    private static final String ELIMINAR_CATEGORIA_QUERY = 
        "DELETE FROM categoria WHERE id_categoria = ?";
    
    private static final String EXISTE_CATEGORIA_POR_NOMBRE_QUERY = 
        "SELECT COUNT(*) as count FROM categoria WHERE nombre = ?";
    
    private static final String EXISTE_CATEGORIA_POR_NOMBRE_EXCLUYENDO_ID_QUERY = 
        "SELECT COUNT(*) as count FROM categoria WHERE nombre = ? AND id_categoria != ?";
    
    /**
     * Crear una nueva categoria para videojuegos
     * @param categoria
     * @return 
     */
    public Categoria createCategoria(Categoria categoria) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement insert = connection.prepareStatement(CREAR_CATEGORIA_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            insert.setString(1, categoria.getNombre());
            insert.setString(2, categoria.getDescripcion());
            
            int affectedRows = insert.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        categoria.setId_categoria(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear categoría: " + e.getMessage(), e);
        }
        
        return categoria;
    }
    
    /**
     * Obtiene una categoría por ID
     * @param idCategoria
     * @return 
     */
    public Categoria getCategoriaById(int idCategoria) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_CATEGORIA_POR_ID_QUERY)) {
            query.setInt(1, idCategoria);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToCategoria(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Obtiene todas las categorías creadas en la base de datos
     * @return 
     */
    public List<Categoria> getAllCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_CATEGORIAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Categoria categoria = mapResultSetToCategoria(resultSet);
                categorias.add(categoria);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener todas las categorías: " + e.getMessage());
        }
        
        return categorias;
    }
    
    /**
     * Actualiza una categoría
     * @param categoria
     * @return 
     */
    public boolean updateCategoria(Categoria categoria) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_CATEGORIA_QUERY)) {
            update.setString(1, categoria.getNombre());
            update.setString(2, categoria.getDescripcion());
            update.setInt(3, categoria.getId_categoria());
            
            int affectedRows = update.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar categoría: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteCategoria(int idCategoria) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_CATEGORIA_QUERY)) {
            delete.setInt(1, idCategoria);
            
            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar categoría: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si ya existe una categoría con el mismo nombre
     * @param nombreCategoria
     * @return 
     */
    public boolean existeCategoriaPorNombre(String nombreCategoria) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(EXISTE_CATEGORIA_POR_NOMBRE_QUERY)) {
            query.setString(1, nombreCategoria);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar nombre de categoría: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Verifica si ya existe una categoría con el mismo nombre, excluyendo una categoría específica
     * @param nombreCategoria
     * @param idCategoriaExcluir
     * @return 
     */
    public boolean existeCategoriaPorNombreExcluyendoId(String nombreCategoria, int idCategoriaExcluir) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(EXISTE_CATEGORIA_POR_NOMBRE_EXCLUYENDO_ID_QUERY)) {
            query.setString(1, nombreCategoria);
            query.setInt(2, idCategoriaExcluir);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar nombre de categoría (excluyendo ID): " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mapea un ResultSet a un objeto Categoria
     */
    private Categoria mapResultSetToCategoria(ResultSet resultSet) throws SQLException {
        return new Categoria(
            resultSet.getInt("id_categoria"),
            resultSet.getString("nombre"),
            resultSet.getString("descripcion")
        );
    }
}
