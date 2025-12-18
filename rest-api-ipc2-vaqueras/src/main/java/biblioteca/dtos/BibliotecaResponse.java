package biblioteca.dtos;


import biblioteca.models.Biblioteca;
import java.util.Date;

public class BibliotecaResponse {
    private int id_biblioteca;
    private int id_usuario;
    private String tipo_adquisicion;

    
    // Información básica de la compra
    private CompraMiniResponse compra;
    
    // Información del videojuego
    private VideojuegoMiniResponse videojuego;
    
    public BibliotecaResponse() {}
    
    public BibliotecaResponse(Biblioteca biblioteca) {
        this.id_biblioteca = biblioteca.getId_biblioteca();
        this.id_usuario = biblioteca.getId_usuario();
        this.tipo_adquisicion = biblioteca.getTipo_adquisicion();
        
        
        // Información básica de la compra sin repetir
        if (biblioteca.getCompra() != null) {
            this.compra = new CompraMiniResponse(biblioteca.getCompra());
        }
        
        // Información del videojuego sin repetir
        if (biblioteca.getVideojuego() != null) {
            this.videojuego = new VideojuegoMiniResponse(biblioteca.getVideojuego());
        }
    }
    
    public int getId_biblioteca() {
        return id_biblioteca;
    }
    
    public void setId_biblioteca(int id_biblioteca) {
        this.id_biblioteca = id_biblioteca;
    }
    
    public int getId_usuario() {
        return id_usuario;
    }
    
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
    
    public String getTipo_adquisicion() {
        return tipo_adquisicion;
    }
    
    public void setTipo_adquisicion(String tipo_adquisicion) {
        this.tipo_adquisicion = tipo_adquisicion;
    }
    
    public CompraMiniResponse getCompra() {
        return compra;
    }
    
    public void setCompra(CompraMiniResponse compra) {
        this.compra = compra;
    }
    
    public VideojuegoMiniResponse getVideojuego() {
        return videojuego;
    }
    
    public void setVideojuego(VideojuegoMiniResponse videojuego) {
        this.videojuego = videojuego;
    }
}