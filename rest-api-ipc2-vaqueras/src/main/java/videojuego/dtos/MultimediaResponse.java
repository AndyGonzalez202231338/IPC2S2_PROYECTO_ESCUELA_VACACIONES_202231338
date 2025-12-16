/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.dtos;

import videojuego.models.Multimedia;

/**
 *
 * @author andy
 */
public class MultimediaResponse {

    private int id_multimedia;
    private int id_videojuego;
    private String imagenBase64;

    public MultimediaResponse(Multimedia multimedia) {
        this.id_multimedia = multimedia.getId_multimedia();
        this.id_videojuego = multimedia.getId_videojuego();
        // Convertir imagen a Base64 para la respuesta
        if (multimedia.getImagen() != null) {
            this.imagenBase64 = java.util.Base64.getEncoder().encodeToString(multimedia.getImagen());
        }
    }

    public int getId_multimedia() {
        return id_multimedia;
    }

    public void setId_multimedia(int id_multimedia) {
        this.id_multimedia = id_multimedia;
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
