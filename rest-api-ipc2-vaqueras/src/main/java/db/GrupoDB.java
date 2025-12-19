/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import conexion.DBConnectionSingleton;
import grupo.dtos.GrupoResponse;
import grupo.dtos.NewGrupoRequest;
import grupo.models.Grupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class GrupoDB {
    
    private static final String INSERT_GRUPO_QUERY = 
        "INSERT INTO grupo (id_creador, nombre, cantidad_participantes) VALUES (?, ?, 0)";
    
    private static final String OBTENER_GRUPO_POR_ID_QUERY = 
        "SELECT * FROM grupo WHERE id_grupo = ?";
    
    private static final String DELETE_GRUPO_QUERY = 
        "DELETE FROM grupo WHERE id_grupo = ?";
    
    private static final String UPDATE_GRUPO_QUERY = 
        "UPDATE grupo SET nombre = ?, cantidad_participantes = ? WHERE id_grupo = ?";
    
    //querys para grupo_usuario
    
    private static final String INSERT_PARTICIPANTE_QUERY = 
    "INSERT INTO grupo_usuario (id_grupo, id_usuario) VALUES (?, ?)";

    
    private static final String DELETE_PARTICIPANTE_QUERY = 
        "DELETE FROM grupo_usuario WHERE id_grupo = ? AND id_usuario = ?";
    
    private static final String OBTENER_PARTICIPANTES_QUERY = 
        "SELECT u.* FROM usuario u " +
        "JOIN grupo_usuario gu ON u.id_usuario = gu.id_usuario " +
        "WHERE gu.id_grupo = ?";
    
    private static final String OBTENER_GRUPOS_POR_USUARIO_QUERY = 
        "SELECT g.* FROM grupo g " +
        "JOIN grupo_usuario gu ON g.id_grupo = gu.id_grupo " +
        "WHERE gu.id_usuario = ?";
    
    private static final String COUNT_PARTICIPANTES_QUERY = 
        "SELECT COUNT(*) FROM grupo_usuario WHERE id_grupo = ?";
    
    private static final String VERIFICAR_USUARIO_EN_GRUPO_QUERY = 
        "SELECT COUNT(*) FROM grupo_usuario WHERE id_grupo = ? AND id_usuario = ?";
    
    private static final String COUNT_GRUPOS_CREADOS_QUERY = 
        "SELECT COUNT(*) FROM grupo WHERE id_creador = ?";
    
    private static final String OBTENER_PARTICIPANTES_GRUPO_DETALLADO_QUERY = 
        "SELECT u.id_usuario, u.nombre, u.correo, u.pais, u.telefono, " +
               "u.fecha_nacimiento, u.saldo_cartera " +
        "FROM usuario u " +
        "JOIN grupo_usuario gu ON u.id_usuario = gu.id_usuario " +
        "WHERE gu.id_grupo = ? " +
        "ORDER BY u.nombre";
    

    public NewGrupoRequest insertarGrupo(NewGrupoRequest grupoRequest) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(
                INSERT_GRUPO_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, grupoRequest.getId_creador());
            ps.setString(2, grupoRequest.getNombre());
            // cantidad_participantes se establece como 0 en la query
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("No se pudo crear el grupo, ninguna fila afectada");
            }
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int idGenerado = generatedKeys.getInt(1);
                    
                    NewGrupoRequest respuesta = new NewGrupoRequest();
                    respuesta.setId_grupo(idGenerado);
                    respuesta.setId_creador(grupoRequest.getId_creador());
                    respuesta.setNombre(grupoRequest.getNombre());
                    
                    return respuesta;
                } else {
                    throw new SQLException("No se pudo obtener el ID generado del grupo");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Obtiene un grupo por ID
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public Grupo obtenerGrupoPorId(int idGrupo) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(OBTENER_GRUPO_POR_ID_QUERY)) {
            ps.setInt(1, idGrupo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGrupo(rs);
                }
                return null;
            }
        }
    }
    
    /**
     * Elimina un grupo
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public boolean eliminarGrupo(int idGrupo) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(DELETE_GRUPO_QUERY)) {
            ps.setInt(1, idGrupo);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Actualiza un grupo
     * @param grupo
     * @return
     * @throws SQLException 
     */
    public boolean actualizarGrupo(Grupo grupo) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(UPDATE_GRUPO_QUERY)) {
            ps.setString(1, grupo.getNombre());
            
            if (grupo.getCantidad_participantes() != 0) {
                ps.setInt(2, grupo.getCantidad_participantes());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            
            ps.setInt(3, grupo.getId_grupo());
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Agrega un participante a un grupo
     * @param idGrupo
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public boolean agregarParticipante(int idGrupo, int idUsuario) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(INSERT_PARTICIPANTE_QUERY)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                actualizarContadorParticipantes(idGrupo);
            }
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Elimina un participante de un grupo
     * @param idGrupo
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public boolean eliminarParticipante(int idGrupo, int idUsuario) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(DELETE_PARTICIPANTE_QUERY)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            
            int affectedRows = ps.executeUpdate();
            
            // Actualizar contador de participantes si se eliminó exitosamente
            if (affectedRows > 0) {
                actualizarContadorParticipantes(idGrupo);
            }
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Obtiene los participantes de un grupo
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public List<Usuario> obtenerParticipantes(int idGrupo) throws SQLException {
        List<Usuario> participantes = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(OBTENER_PARTICIPANTES_QUERY)) {
            ps.setInt(1, idGrupo);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    
                    participantes.add(mapResultSetToUsuario(rs));
                }
            }
        }
        
        return participantes;
    }
    
    /**
     * Obtiene los grupos a los que pertenece un usuario
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public List<Grupo> obtenerGruposPorUsuario(int idUsuario) throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(OBTENER_GRUPOS_POR_USUARIO_QUERY)) {
            ps.setInt(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grupos.add(mapResultSetToGrupo(rs));
                }
            }
        }
        
        return grupos;
    }
    
    /**
     * Cuenta los participantes de un grupo
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public int contarParticipantes(int idGrupo) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(COUNT_PARTICIPANTES_QUERY)) {
            ps.setInt(1, idGrupo);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    /**
     * Verifica si un usuario está en un grupo
     * @param idGrupo
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public boolean usuarioEnGrupo(int idGrupo, int idUsuario) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(VERIFICAR_USUARIO_EN_GRUPO_QUERY)) {
            ps.setInt(1, idGrupo);
            ps.setInt(2, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    /**
     * Cuenta cuántos grupos ha creado un usuario
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public int contarGruposCreadosPorUsuario(int idUsuario) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(COUNT_GRUPOS_CREADOS_QUERY)) {
            ps.setInt(1, idUsuario);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }
    
    /**
     * Actualiza el contador de participantes en la tabla grupo
     * @param idGrupo
     * @throws SQLException 
     */
    private void actualizarContadorParticipantes(int idGrupo) throws SQLException {
        int totalParticipantes = contarParticipantes(idGrupo);
        
        String updateQuery = "UPDATE grupo SET cantidad_participantes = ? WHERE id_grupo = ?";
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(updateQuery)) {
            ps.setInt(1, totalParticipantes);
            ps.setInt(2, idGrupo);
            ps.executeUpdate();
        }
    }
    
    /**
     * Obtiene los participantes de un grupo con información detallada
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public List<Usuario> obtenerParticipantesDetallado(int idGrupo) throws SQLException {
        List<Usuario> participantes = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        
        try (PreparedStatement ps = connection.prepareStatement(OBTENER_PARTICIPANTES_GRUPO_DETALLADO_QUERY)) {
            ps.setInt(1, idGrupo);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    participantes.add(mapResultSetToUsuarioDetallado(rs));
                }
            }
        }
        
        return participantes;
    }
    
        /**
     * Mapea ResultSet a Usuario con más campos
     */
    private Usuario mapResultSetToUsuarioDetallado(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setCorreo(rs.getString("correo"));
        usuario.setPais(rs.getString("pais"));
        usuario.setTelefono(rs.getString("telefono"));
        usuario.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
        usuario.setSaldo_cartera(rs.getDouble("saldo_cartera"));
        return usuario;
    }
    
    /**
     * Mapea ResultSet a Grupo
     */
    private Grupo mapResultSetToGrupo(ResultSet rs) throws SQLException {
        Grupo grupo = new Grupo();
        grupo.setId_grupo(rs.getInt("id_grupo"));
        grupo.setId_creador(rs.getInt("id_creador"));
        grupo.setNombre(rs.getString("nombre"));
        grupo.setCantidad_participantes(rs.getInt("cantidad_participantes"));
        if (rs.wasNull()) {
            grupo.setCantidad_participantes(0);
        }
        return grupo;
    }
    
    /**
     * Mapea ResultSet a Usuario (simplificado)
     */
    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setCorreo(rs.getString("correo"));
        return usuario;
    }
}