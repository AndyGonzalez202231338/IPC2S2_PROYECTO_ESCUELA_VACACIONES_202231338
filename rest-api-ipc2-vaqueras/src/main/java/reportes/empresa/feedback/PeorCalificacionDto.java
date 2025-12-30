/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.feedback;

import java.time.LocalDateTime;

/**
 *
 * @author andy
 */
public class PeorCalificacionDto {
    private int posicion;
    private String tituloJuego;
    private String nombreUsuario;
    private int calificacion;
    private LocalDateTime fechaHora;

    public PeorCalificacionDto(int posicion, String tituloJuego, String nombreUsuario, int calificacion, LocalDateTime fechaHora) {
        this.posicion = posicion;
        this.tituloJuego = tituloJuego;
        this.nombreUsuario = nombreUsuario;
        this.calificacion = calificacion;
        this.fechaHora = fechaHora;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }
}