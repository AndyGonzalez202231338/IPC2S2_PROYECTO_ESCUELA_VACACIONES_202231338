package instalacion.models;

public class InstalacionJuego {
    private int id_instalacion;
    private int id_biblioteca;
    private int id_videojuego;
    private String estado;
    private String tipo_adquisicion;
    private int id_usuario;

    public InstalacionJuego() {
    }

    public InstalacionJuego(int id_instalacion, int id_biblioteca, int id_videojuego, String estado, String tipo_adquisicion) {
        this.id_instalacion = id_instalacion;
        this.id_biblioteca = id_biblioteca;
        this.id_videojuego = id_videojuego;
        this.estado = estado;
        this.tipo_adquisicion = tipo_adquisicion;
    }


    public int getId_instalacion() {
        return id_instalacion;
    }

    public void setId_instalacion(int id_instalacion) {
        this.id_instalacion = id_instalacion;
    }

    public int getId_biblioteca() {
        return id_biblioteca;
    }

    public void setId_biblioteca(int id_biblioteca) {
        this.id_biblioteca = id_biblioteca;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTipo_adquisicion() {
        return tipo_adquisicion;
    }

    public void setTipo_adquisicion(String tipo_adquisicion) {
        this.tipo_adquisicion = tipo_adquisicion;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    
}