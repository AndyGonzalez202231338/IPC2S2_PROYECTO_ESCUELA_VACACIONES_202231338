/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ventas.calidad;

/**
 *
 * @author andy
 */
public class VideojuegoCalificacionDto {
    private String titulo;
    private Double calificacion;
    private int totalCalificaciones;
    private String clasificacionEdad;
    private String categorias;
    
    public VideojuegoCalificacionDto(String titulo, Double calificacion, int totalCalificaciones,
                                     String clasificacionEdad, String categorias) {
        this.titulo = titulo;
        this.calificacion = calificacion;
        this.totalCalificaciones = totalCalificaciones;
        this.clasificacionEdad = clasificacionEdad;
        this.categorias = categorias;
    }

    public String getTitulo() {
        return titulo;
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
