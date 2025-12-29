/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ventas.calidad;

/**
 *
 * @author andy
 */
public class VideojuegoCompletoDto {
    private String titulo;
    private int cantidadVentas;
    private Double totalVentas;
    private Double calificacion;
    private int totalCalificaciones;
    private String clasificacionEdad;
    private String categorias;

    public VideojuegoCompletoDto(String titulo, int cantidadVentas, Double totalVentas, Double calificacion, int totalCalificaciones, String clasificacionEdad, String categorias) {
        this.titulo = titulo;
        this.cantidadVentas = cantidadVentas;
        this.totalVentas = totalVentas;
        this.calificacion = calificacion;
        this.totalCalificaciones = totalCalificaciones;
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

    public Double getCalificacion() {
        return calificacion;
    }

    public int getTotalCalificaciones() {
        return totalCalificaciones;
    }

    public String getClasificacionEdad() {
        return clasificacionEdad;
    }

    public String getCategorias() {
        return categorias;
    }
    
    
}
