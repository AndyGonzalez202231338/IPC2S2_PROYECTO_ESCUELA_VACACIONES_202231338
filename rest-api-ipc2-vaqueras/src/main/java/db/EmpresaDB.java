package db;

import conexion.DBConnectionSingleton;
import empresa.models.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDB {
    
    private static final String CREAR_EMPRESA_QUERY = 
        "INSERT INTO empresa (nombre, descripcion) VALUES (?, ?)";
    
    private static final String OBTENER_EMPRESA_POR_ID_QUERY = 
        "SELECT id_empresa, nombre, descripcion FROM empresa WHERE id_empresa = ?";
    
    private static final String OBTENER_TODAS_EMPRESAS_QUERY = 
        "SELECT id_empresa, nombre, descripcion FROM empresa ORDER BY nombre";
    
    private static final String EXISTE_EMPRESA_POR_NOMBRE_QUERY = 
        "SELECT COUNT(*) as count FROM empresa WHERE nombre = ?";
    
    private Empresa mapResultSetToEmpresa(ResultSet resultSet) throws SQLException {
        return new Empresa(
            resultSet.getInt("id_empresa"),
            resultSet.getString("nombre"),
            resultSet.getString("descripcion")
        );
    }
    
    public Empresa createEmpresa(Empresa empresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement insert = connection.prepareStatement(CREAR_EMPRESA_QUERY, 
                Statement.RETURN_GENERATED_KEYS)) {
            
            insert.setString(1, empresa.getNombre());
            insert.setString(2, empresa.getDescripcion());
            
            int affectedRows = insert.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empresa.setId_empresa(generatedKeys.getInt(1));
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear empresa: " + e.getMessage(), e);
        }
        
        return empresa;
    }
    
    public Empresa getEmpresaById(int idEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_EMPRESA_POR_ID_QUERY)) {
            query.setInt(1, idEmpresa);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToEmpresa(resultSet);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
        /**
     * Obtiene todas las empresas de la base de datos
     * @return Lista de todas las empresas
     */
    public List<Empresa> getAllEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_EMPRESAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();
            
            while (resultSet.next()) {
                Empresa empresa = mapResultSetToEmpresa(resultSet);
                empresas.add(empresa);
            }
            
            System.out.println("Empresas obtenidas de la BD: " + empresas.size());
            
        } catch (SQLException e) {
            System.out.println("Error al obtener todas las empresas: " + e.getMessage());
            e.printStackTrace();
        }
        
        return empresas;
    }
    
        /**
     * Verifica si ya existe una empresa con el mismo nombre
     * @param nombreEmpresa Nombre a verificar
     * @return true si ya existe, false si no existe
     */
    public boolean existeEmpresaPorNombre(String nombreEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement query = connection.prepareStatement(EXISTE_EMPRESA_POR_NOMBRE_QUERY)) {
            query.setString(1, nombreEmpresa);
            ResultSet resultSet = query.executeQuery();
            
            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar nombre de empresa: " + e.getMessage());
        }
        
        return false;
    }
    
}