/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comentario.dto;

import java.time.LocalDateTime;

/**
 *
 * @author andy
 */
public class ComentarioResponse {
    private int id_comentario;
    private int id_usuario;
    private int id_biblioteca;
    private String comentario;
    private LocalDateTime fecha_hora;

    public ComentarioResponse(int id_comentario, int id_usuario, int id_biblioteca, String comentario, LocalDateTime fecha_hora) {
        this.id_comentario = id_comentario;
        this.id_usuario = id_usuario;
        this.id_biblioteca = id_biblioteca;
        this.comentario = comentario;
        this.fecha_hora = fecha_hora;
    }

    public int getId_comentario() {
        return id_comentario;
    }

    public void setId_comentario(int id_comentario) {
        this.id_comentario = id_comentario;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_biblioteca() {
        return id_biblioteca;
    }

    public void setId_biblioteca(int id_biblioteca) {
        this.id_biblioteca = id_biblioteca;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
    
    
    
}
