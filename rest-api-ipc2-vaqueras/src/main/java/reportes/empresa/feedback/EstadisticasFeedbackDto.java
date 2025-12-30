/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.feedback;

/**
 *
 * @author andy
 */
public class EstadisticasFeedbackDto {
    private int totalJuegosCalificados;
    private int totalPeoresCalificaciones;
    private double promedioGeneral;
    private String mejorJuego = "N/A";
    private double mejorRating = 0.0;
    private String peorJuego = "N/A";
    private double peorRating = 0.0;

    public int getTotalJuegosCalificados() {
        return totalJuegosCalificados;
    }

    public void setTotalJuegosCalificados(int totalJuegosCalificados) {
        this.totalJuegosCalificados = totalJuegosCalificados;
    }

    public int getTotalPeoresCalificaciones() {
        return totalPeoresCalificaciones;
    }

    public void setTotalPeoresCalificaciones(int totalPeoresCalificaciones) {
        this.totalPeoresCalificaciones = totalPeoresCalificaciones;
    }

    public double getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(double promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public String getMejorJuego() {
        return mejorJuego;
    }

    public void setMejorJuego(String mejorJuego) {
        this.mejorJuego = mejorJuego;
    }

    public double getMejorRating() {
        return mejorRating;
    }

    public void setMejorRating(double mejorRating) {
        this.mejorRating = mejorRating;
    }

    public String getPeorJuego() {
        return peorJuego;
    }

    public void setPeorJuego(String peorJuego) {
        this.peorJuego = peorJuego;
    }

    public double getPeorRating() {
        return peorRating;
    }

    public void setPeorRating(double peorRating) {
        this.peorRating = peorRating;
    }
}
