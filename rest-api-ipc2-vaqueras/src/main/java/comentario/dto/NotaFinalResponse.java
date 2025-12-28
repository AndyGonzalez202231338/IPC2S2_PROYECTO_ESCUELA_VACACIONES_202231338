/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comentario.dto;

/**
 *
 * @author andy
 */
public class NotaFinalResponse {
    public int idVideojuego;
    public double notaFinal;
    public int totalCalificaciones;

    public NotaFinalResponse(int idVideojuego, double notaFinal, int total) {
        this.idVideojuego = idVideojuego;
        this.notaFinal = notaFinal;
        this.totalCalificaciones = total;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(int idVideojuego) {
        this.idVideojuego = idVideojuego;
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
