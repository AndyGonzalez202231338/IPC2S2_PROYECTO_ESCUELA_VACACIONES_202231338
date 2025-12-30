/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.top5juegos;

/**
 *
 * @author andy
 */

public class EstadisticasDto {
    private int totalJuegosVendidos;
    private int totalCompradores;
    private int totalVentas;
    private double porcentajeTop5;

    public EstadisticasDto() {
        this.porcentajeTop5 = 0.0;
    }

    public int getTotalJuegosVendidos() {
        return totalJuegosVendidos;
    }

    public void setTotalJuegosVendidos(int totalJuegosVendidos) {
        this.totalJuegosVendidos = totalJuegosVendidos;
    }

    public int getTotalCompradores() {
        return totalCompradores;
    }

    public void setTotalCompradores(int totalCompradores) {
        this.totalCompradores = totalCompradores;
    }

    public int getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(int totalVentas) {
        this.totalVentas = totalVentas;
    }

    public double getPorcentajeTop5() {
        return porcentajeTop5;
    }

    public void setPorcentajeTop5(double porcentajeTop5) {
        this.porcentajeTop5 = porcentajeTop5;
    }
}
