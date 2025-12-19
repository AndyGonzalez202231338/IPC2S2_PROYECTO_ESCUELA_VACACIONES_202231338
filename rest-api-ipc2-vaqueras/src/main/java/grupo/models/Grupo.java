/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grupo.models;

import java.sql.Date;
import java.util.List;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class Grupo {
    private int id_grupo;
    private int id_creador;
    private String nombre;
    private int cantidad_participantes;
    private Date fecha_creacion;
    
    private Usuario creador;
    private List<Usuario> participantes;
    
    

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

    public int getCantidad_participantes() {
        return cantidad_participantes;
    }

    public void setCantidad_participantes(int cantidad_participantes) {
        this.cantidad_participantes = cantidad_participantes;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public List<Usuario> getParticipantes() {
        return participantes;
    }

    public void setParticipantes(List<Usuario> participantes) {
        this.participantes = participantes;
    }
    
    
}
