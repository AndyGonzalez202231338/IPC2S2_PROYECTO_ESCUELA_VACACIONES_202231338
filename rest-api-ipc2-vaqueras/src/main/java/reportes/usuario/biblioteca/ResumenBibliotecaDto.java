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
public class ResumenBibliotecaDto {
    
    private String nombreUsuario;
    private int totalJuegosBiblioteca;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    
    public ResumenBibliotecaDto(String nombreUsuario, int totalJuegosBiblioteca, 
                               LocalDate fechaInicio, LocalDate fechaFin) {
        this.nombreUsuario = nombreUsuario;
        this.totalJuegosBiblioteca = totalJuegosBiblioteca;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getTotalJuegosBiblioteca() {
        return totalJuegosBiblioteca;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }
    
    public String getPeriodoTexto() {
        if (fechaInicio != null && fechaFin != null) {
            return "Del " + fechaInicio + " al " + fechaFin;
        }
        return "Histórico completo";
    }
}