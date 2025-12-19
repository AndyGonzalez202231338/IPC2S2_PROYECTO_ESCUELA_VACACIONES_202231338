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
public class CalificacionResponse {
    private int id_calificacion;
    private int id_usuario;
    private int id_biblioteca;
    private int calificacion;
    private LocalDateTime fecha_hora;

    public CalificacionResponse() {
    }

    public CalificacionResponse(int id_calificacion, int id_usuario, int id_biblioteca, int calificacion, LocalDateTime fecha_hora) {
        this.id_calificacion = id_calificacion;
        this.id_usuario = id_usuario;
        this.id_biblioteca = id_biblioteca;
        this.calificacion = calificacion;
        this.fecha_hora = fecha_hora;
    }

    public int getId_calificacion() {
        return id_calificacion;
    }

    public void setId_calificacion(int id_calificacion) {
        this.id_calificacion = id_calificacion;
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

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDateTime getFecha_hora() {
        return fecha_hora;
    }

    public void setFecha_hora(LocalDateTime fecha_hora) {
        this.fecha_hora = fecha_hora;
    }
    
    
}
