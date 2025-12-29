/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ventas.calidad;

/**
 *
 * @author andy
 */
public class VideojuegoVentasDto {
    private String titulo;
    private int cantidadVentas;
    private Double totalVentas;
    private String clasificacionEdad;
    private String categorias;
    
    public VideojuegoVentasDto(String titulo, int cantidadVentas, Double totalVentas, 
                               String clasificacionEdad, String categorias) {
        this.titulo = titulo;
        this.cantidadVentas = cantidadVentas;
        this.totalVentas = totalVentas;
        this.clasificacionEdad = clasificacionEdad;
        this.categorias = categorias;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public Double getTotalVentas() {
        return totalVentas;
    }

    public String getClasificacionEdad() {
        return clasificacionEdad;
    }

    public String getCategorias() {
        return categorias;
    }
    
}
