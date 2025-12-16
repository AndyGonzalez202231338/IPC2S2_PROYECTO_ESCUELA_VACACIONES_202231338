/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.dtos;

/**
 *
 * @author andy
 */
public class NewMultimediaRequest {
    private int id_videojuego;
    private String imagenBase64;

    public NewMultimediaRequest() {
    }

    public NewMultimediaRequest(int id_videojuego, String imagenBase64) {
        this.id_videojuego = id_videojuego;
        this.imagenBase64 = imagenBase64;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public String getImagenBase64() {
        return imagenBase64;
    }

    public void setImagenBase64(String imagenBase64) {
        this.imagenBase64 = imagenBase64;
    }
    
    
}
