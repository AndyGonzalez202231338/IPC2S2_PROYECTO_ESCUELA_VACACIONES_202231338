package banner.models;

public class Banner {
    private int id_banner;
    private String titulo;
    private String descripcion;
    private int id_videojuego;
    private boolean activo;

    public Banner() {
    }

    public Banner(int id_banner, String titulo, String descripcion, 
                  int id_videojuego, boolean activo) {
        this.id_banner = id_banner;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.id_videojuego = id_videojuego;
        this.activo = activo;
    }

    public int getId_banner() {
        return id_banner;
    }

    public void setId_banner(int id_banner) {
        this.id_banner = id_banner;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}