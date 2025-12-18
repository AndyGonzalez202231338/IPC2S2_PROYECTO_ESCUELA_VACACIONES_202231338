/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.models;

import compra.models.Compra;
import java.sql.Date;
import user.models.Usuario;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class Biblioteca {
    private int id_biblioteca;
    private int id_usuario;
    private int id_videojuego;
    private int id_compra;
    private String tipo_adquisicion;
    
    
    private Usuario usuario;
    private Videojuego videojuego;
    private Compra compra;

    public Biblioteca() {
    }

    public Biblioteca(int id_usuario, int id_videojuego, int id_compra, String tipo_adquisicion) {
        this.id_usuario = id_usuario;
        this.id_videojuego = id_videojuego;
        this.id_compra = id_compra;
        this.tipo_adquisicion = tipo_adquisicion;
        
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

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public int getId_compra() {
        return id_compra;
    }

    public void setId_compra(int id_compra) {
        this.id_compra = id_compra;
    }

    public String getTipo_adquisicion() {
        return tipo_adquisicion;
    }

    public void setTipo_adquisicion(String tipo_adquisicion) {
        this.tipo_adquisicion = tipo_adquisicion;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Videojuego getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(Videojuego videojuego) {
        this.videojuego = videojuego;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }
    
}
