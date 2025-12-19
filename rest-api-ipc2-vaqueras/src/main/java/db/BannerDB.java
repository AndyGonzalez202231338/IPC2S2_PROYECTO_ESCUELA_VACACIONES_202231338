package db;

import banner.models.Banner;
import conexion.DBConnectionSingleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BannerDB {
    
    private static final String CREAR_BANNER_QUERY = 
        "INSERT INTO banner (titulo, descripcion, id_videojuego, activo) VALUES (?, ?, ?, ?)";
    
    private static final String OBTENER_BANNERS_ACTIVOS_QUERY = 
        "SELECT * FROM banner WHERE activo = true";
    
    private static final String OBTENER_TODOS_BANNERS_QUERY = 
        "SELECT * FROM banner";
    
    private static final String ACTUALIZAR_BANNER_QUERY = 
        "UPDATE banner SET titulo = ?, descripcion = ?, id_videojuego = ?, activo = ? WHERE id_banner = ?";
    
    private static final String ELIMINAR_BANNER_QUERY = 
        "DELETE FROM banner WHERE id_banner = ?";
    
    private static final String OBTENER_BANNER_POR_ID_QUERY = 
        "SELECT * FROM banner WHERE id_banner = ?";
    
    
    public Banner crearBanner(Banner banner) {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CREAR_BANNER_QUERY, 
                     Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, banner.getTitulo());
            pstmt.setString(2, banner.getDescripcion());
            pstmt.setInt(3, banner.getId_videojuego());
            pstmt.setBoolean(4, banner.isActivo());
            
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        banner.setId_banner(generatedKeys.getInt(1));
                        return banner;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public List<Banner> getBannersActivos() {
        List<Banner> banners = new ArrayList<>();
        
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_BANNERS_ACTIVOS_QUERY)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                banners.add(mapResultSetToBanner(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }
    

    public List<Banner> getAllBanners() {
        List<Banner> banners = new ArrayList<>();
        
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_TODOS_BANNERS_QUERY)) {
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                banners.add(mapResultSetToBanner(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return banners;
    }
    

    public Banner getBannerById(int id) {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(OBTENER_BANNER_POR_ID_QUERY)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToBanner(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarBanner(Banner banner) {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ACTUALIZAR_BANNER_QUERY)) {
            
            pstmt.setString(1, banner.getTitulo());
            pstmt.setString(2, banner.getDescripcion());
            pstmt.setInt(3, banner.getId_videojuego());
            pstmt.setBoolean(4, banner.isActivo());
            pstmt.setInt(5, banner.getId_banner());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
  
    public boolean eliminarBanner(int id) {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ELIMINAR_BANNER_QUERY)) {
            
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Mapear ResultSet a Banner 
     */
    private Banner mapResultSetToBanner(ResultSet rs) throws SQLException {
        Banner banner = new Banner();
        banner.setId_banner(rs.getInt("id_banner"));
        banner.setTitulo(rs.getString("titulo"));
        banner.setDescripcion(rs.getString("descripcion"));
        banner.setId_videojuego(rs.getInt("id_videojuego"));
        banner.setActivo(rs.getBoolean("activo"));
        return banner;
    }
}