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
public class RespuestaComentarioResponse {
    private int id_respuesta;
    private int id_comentario_padre;
    private int id_usuario;
    private String comentario;
    private LocalDateTime fecha_Hora;

    public RespuestaComentarioResponse() {
    }

    public RespuestaComentarioResponse(int id_respuesta, int id_comentario_padre, int id_usuario, String comentario, LocalDateTime fecha_Hora) {
        this.id_respuesta = id_respuesta;
        this.id_comentario_padre = id_comentario_padre;
        this.id_usuario = id_usuario;
        this.comentario = comentario;
        this.fecha_Hora = fecha_Hora;
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

    public LocalDateTime getFecha_Hora() {
        return fecha_Hora;
    }

    public void setFecha_Hora(LocalDateTime fecha_Hora) {
        this.fecha_Hora = fecha_Hora;
    }
    
    
}
