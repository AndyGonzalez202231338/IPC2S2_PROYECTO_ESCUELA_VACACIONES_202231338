/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.models;

import user.models.Usuario;

/**
 *
 * @author andy
 */
public class Empresa {
    private int id_empresa;
    private String nombre;
    private String descripcion;

    public Empresa(int id_empresa, String nombre, String descripcion) {
        this.id_empresa = id_empresa;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
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
        
    
    public boolean isValid() {
        return nombre != null && !nombre.trim().isEmpty() &&
               descripcion != null && !descripcion.trim().isEmpty();
    }
}
