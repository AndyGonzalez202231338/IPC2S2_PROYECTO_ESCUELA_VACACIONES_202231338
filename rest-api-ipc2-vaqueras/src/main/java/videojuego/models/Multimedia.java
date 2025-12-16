/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.models;

/**
 *
 * @author andy
 */
public class Multimedia {
    private int id_multimedia;
    private int id_videojuego;
    private byte[] imagen;
    
    public Multimedia() {
    }

    public Multimedia(int id_multimedia, int id_videojuego, byte[] imagen) {
        this.id_multimedia = id_multimedia;
        this.id_videojuego = id_videojuego;
        this.imagen = imagen;
    }

    public Multimedia(int id_videojuego, byte[] imagen) {
        this.id_videojuego = id_videojuego;
        this.imagen = imagen;
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

    public byte[] getImagen() {
        return imagen;
    }

    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
