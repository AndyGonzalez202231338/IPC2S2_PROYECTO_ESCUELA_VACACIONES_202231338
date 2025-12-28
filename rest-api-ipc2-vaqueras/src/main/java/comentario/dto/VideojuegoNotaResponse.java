/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comentario.dto;

/**
 *
 * @author andy
 */
public class VideojuegoNotaResponse {
    public int idVideojuego;
    public String nombre;
    public double notaFinal;
    public int totalCalificaciones;

    public VideojuegoNotaResponse(int idVideojuego, String nombre, double notaFinal, int totalCalificaciones) {
        this.idVideojuego = idVideojuego;
        this.nombre = nombre;
        this.notaFinal = notaFinal;
        this.totalCalificaciones = totalCalificaciones;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(int idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public int getTotalCalificaciones() {
        return totalCalificaciones;
    }

    public void setTotalCalificaciones(int totalCalificaciones) {
        this.totalCalificaciones = totalCalificaciones;
    }
    
    
}
