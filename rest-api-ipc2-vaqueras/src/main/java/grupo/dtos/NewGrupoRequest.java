/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grupo.dtos;

/**
 *
 * @author andy
 */
public class NewGrupoRequest {
    private int id_grupo;      
    private int id_creador;    
    private String nombre;     
    
    
    public NewGrupoRequest() {}
    
    
    public NewGrupoRequest(int id_creador, String nombre) {
        this.id_creador = id_creador;
        this.nombre = nombre;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public int getId_creador() {
        return id_creador;
    }

    public void setId_creador(int id_creador) {
        this.id_creador = id_creador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
