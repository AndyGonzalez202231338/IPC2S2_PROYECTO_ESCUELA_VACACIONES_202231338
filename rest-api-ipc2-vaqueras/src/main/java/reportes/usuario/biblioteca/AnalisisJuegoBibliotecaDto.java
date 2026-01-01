/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.biblioteca;

import java.time.LocalDate;

/**
 *
 * @author andy
 */
public class AnalisisJuegoBibliotecaDto {
    
    private String tituloJuego;
    private double calificacionComunidad;
    private double calificacionPersonal;
    private double diferencia;
    
    public AnalisisJuegoBibliotecaDto(String tituloJuego, double calificacionComunidad, 
                                      double calificacionPersonal) {
        this.tituloJuego = tituloJuego;
        this.calificacionComunidad = calificacionComunidad;
        this.calificacionPersonal = calificacionPersonal;
        this.diferencia = calificacionPersonal - calificacionComunidad;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public double getCalificacionComunidad() {
        return calificacionComunidad;
    }

    public double getCalificacionPersonal() {
        return calificacionPersonal;
    }

    public double getDiferencia() {
        return diferencia;
    }
    
    public String getAnalisisDiferencia() {
        if (diferencia > 0) {
            return "Tu valoración es mayor en " + String.format("%.2f", diferencia);
        } else if (diferencia < 0) {
            return "Comunidad valora más en " + String.format("%.2f", Math.abs(diferencia));
        } else {
            return "Coincide con la comunidad";
        }
    }
}