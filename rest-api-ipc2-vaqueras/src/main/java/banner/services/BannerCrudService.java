package banner.services;

import banner.models.Banner;
import db.BannerDB;
import java.util.List;

public class BannerCrudService {
    
    private final BannerDB bannerDB;
    
    public BannerCrudService() {
        this.bannerDB = new BannerDB();
    }
    
    public Banner crearBanner(Banner banner) {
        if (banner.getTitulo() == null || banner.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título es requerido");
        }
        if (banner.getId_videojuego() <= 0) {
            throw new IllegalArgumentException("ID de videojuego inválido");
        }
        
        return bannerDB.crearBanner(banner);
    }
    
    public List<Banner> getBannersActivos() {
        return bannerDB.getBannersActivos();
    }
    
    public List<Banner> getAllBanners() {
        return bannerDB.getAllBanners();
    }
    
    public Banner getBannerById(int id) {
        return bannerDB.getBannerById(id);
    }
    
    public boolean actualizarBanner(Banner banner) {
        return bannerDB.actualizarBanner(banner);
    }
    
    public boolean eliminarBanner(int id) {
        return bannerDB.eliminarBanner(id);
    }
}