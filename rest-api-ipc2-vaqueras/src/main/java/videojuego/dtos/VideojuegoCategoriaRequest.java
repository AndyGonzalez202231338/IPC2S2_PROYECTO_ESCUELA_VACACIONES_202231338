/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.dtos;

import java.util.List;

/**
 *
 * @author andy
 */
public class VideojuegoCategoriaRequest {
    private int id_videojuego;
    private List<Integer> categorias_ids;
    private String estado;
    
    public VideojuegoCategoriaRequest() {}

    public VideojuegoCategoriaRequest(int id_videojuego, List<Integer> categorias_ids, String estado) {
        this.id_videojuego = id_videojuego;
        this.categorias_ids = categorias_ids;
        this.estado = estado;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public List<Integer> getCategorias_ids() {
        return categorias_ids;
    }

    public void setCategorias_ids(List<Integer> categorias_ids) {
        this.categorias_ids = categorias_ids;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
