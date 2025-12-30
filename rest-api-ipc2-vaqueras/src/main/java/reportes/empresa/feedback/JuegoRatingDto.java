/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.feedback;

/**
 *
 * @author andy
 */
public class JuegoRatingDto {
    private int idVideojuego;
    private String tituloJuego;
    private int totalCalificaciones;
    private double promedioRating;
    private int peorCalificacion;
    private int mejorCalificacion;

    public JuegoRatingDto(int idVideojuego, String tituloJuego, int totalCalificaciones, double promedioRating, int peorCalificacion, int mejorCalificacion) {
        this.idVideojuego = idVideojuego;
        this.tituloJuego = tituloJuego;
        this.totalCalificaciones = totalCalificaciones;
        this.promedioRating = promedioRating;
        this.peorCalificacion = peorCalificacion;
        this.mejorCalificacion = mejorCalificacion;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(int idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public int getTotalCalificaciones() {
        return totalCalificaciones;
    }

    public void setTotalCalificaciones(int totalCalificaciones) {
        this.totalCalificaciones = totalCalificaciones;
    }

    public double getPromedioRating() {
        return promedioRating;
    }

    public void setPromedioRating(double promedioRating) {
        this.promedioRating = promedioRating;
    }

    public int getPeorCalificacion() {
        return peorCalificacion;
    }

    public void setPeorCalificacion(int peorCalificacion) {
        this.peorCalificacion = peorCalificacion;
    }

    public int getMejorCalificacion() {
        return mejorCalificacion;
    }

    public void setMejorCalificacion(int mejorCalificacion) {
        this.mejorCalificacion = mejorCalificacion;
    }
}