/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.dtos;

/**
 *
 * @author andy
 */
public class NewBibliotecaRequest {
    private int id_usuario;
    private int id_videojuego;
    private int id_compra;
    private String tipo_adquisicion;

    public NewBibliotecaRequest() {
    }

    public NewBibliotecaRequest(int id_usuario, int id_videojuego, int id_compra, String tipo_adquisicion) {
        this.id_usuario = id_usuario;
        this.id_videojuego = id_videojuego;
        this.id_compra = id_compra;
        this.tipo_adquisicion = tipo_adquisicion;
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
    
}
