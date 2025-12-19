/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comentario.model;

import java.time.LocalDateTime;

/**
 *
 * @author andy
 */
public class RespuestaComentario {
    private int id_respuesta;
    private int id_comentario_padre;
    private int id_usuario;
    private String comentario;
    private LocalDateTime fecha_hora;

    public RespuestaComentario() {
    }

    public RespuestaComentario(int id_comentario_padre, int id_usuario, String comentario, LocalDateTime fecha_hora) {
        this.id_comentario_padre = id_comentario_padre;
        this.id_usuario = id_usuario;
        this.comentario = comentario;
        this.fecha_hora = fecha_hora;
    }

    public RespuestaComentario(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }

    public int getId_respuesta() {
        return id_respuesta;
    }

    public void setId_respuesta(int id_respuesta) {
        this.id_respuesta = id_respuesta;
    }

    public int getId_comentario_padre() {
        return id_comentario_padre;
    }

    public void setId_comentario_padre(int id_comentario_padre) {
        this.id_comentario_padre = id_comentario_padre;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
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
