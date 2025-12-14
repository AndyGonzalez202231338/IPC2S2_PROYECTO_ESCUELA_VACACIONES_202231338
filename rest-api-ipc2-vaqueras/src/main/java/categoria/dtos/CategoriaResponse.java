/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package categoria.dtos;

import categoria.models.Categoria;

/**
 *
 * @author andy
 */
public class CategoriaResponse {
    private int id_categoria;
    private String nombre;
    private String descripcion;

    public CategoriaResponse(Categoria categoria) {
        this.id_categoria = categoria.getId_categoria();
        this.nombre = categoria.getNombre();
        this.descripcion = categoria.getDescripcion();
    }

    public int getId_categoria() {
        return id_categoria;
    }

    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    
}
