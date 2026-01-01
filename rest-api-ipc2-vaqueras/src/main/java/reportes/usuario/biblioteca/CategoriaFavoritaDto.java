/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.biblioteca;

/**
 *
 * @author andy
 */
public class CategoriaFavoritaDto {
    
    private String nombreCategoria;
    private int cantidadJuegos;
    private double porcentaje;
    
    public CategoriaFavoritaDto(String nombreCategoria, int cantidadJuegos, int totalJuegos) {
        this.nombreCategoria = nombreCategoria;
        this.cantidadJuegos = cantidadJuegos;
        this.porcentaje = totalJuegos > 0 ? (cantidadJuegos * 100.0) / totalJuegos : 0;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public int getCantidadJuegos() {
        return cantidadJuegos;
    }

    public double getPorcentaje() {
        return porcentaje;
    }
    
    public String getPorcentajeFormateado() {
        return String.format("%.1f%%", porcentaje);
    }
}