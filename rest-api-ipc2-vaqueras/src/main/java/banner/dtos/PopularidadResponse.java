/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banner.dtos;

/**
 *
 * @author andy
 */
public class PopularidadResponse {
        private int idVideojuego;
    private int idBiblioteca;
    private double promedioCalificacion;
    private int totalCalificaciones;
    private double scorePopularidad;
    private String categoria; // ALTA, MEDIA, BAJA
    private String recomendacion;
    private boolean confiable;
    
    
    public PopularidadResponse(int idVideojuego, int idBiblioteca, 
            double promedioCalificacion, int totalCalificaciones,
            double scorePopularidad, String categoria,
            String recomendacion, boolean confiable) {
        this.idVideojuego = idVideojuego;
        this.idBiblioteca = idBiblioteca;
        this.promedioCalificacion = promedioCalificacion;
        this.totalCalificaciones = totalCalificaciones;
        this.scorePopularidad = scorePopularidad;
        this.categoria = categoria;
        this.recomendacion = recomendacion;
        this.confiable = confiable;
    }

    public int getIdVideojuego() {
        return idVideojuego;
    }

    public void setIdVideojuego(int idVideojuego) {
        this.idVideojuego = idVideojuego;
    }

    public int getIdBiblioteca() {
        return idBiblioteca;
    }

    public void setIdBiblioteca(int idBiblioteca) {
        this.idBiblioteca = idBiblioteca;
    }

    public double getPromedioCalificacion() {
        return promedioCalificacion;
    }

    public void setPromedioCalificacion(double promedioCalificacion) {
        this.promedioCalificacion = promedioCalificacion;
    }

    public int getTotalCalificaciones() {
        return totalCalificaciones;
    }

    public void setTotalCalificaciones(int totalCalificaciones) {
        this.totalCalificaciones = totalCalificaciones;
    }

    public double getScorePopularidad() {
        return scorePopularidad;
    }

    public void setScorePopularidad(double scorePopularidad) {
        this.scorePopularidad = scorePopularidad;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getRecomendacion() {
        return recomendacion;
    }

    public void setRecomendacion(String recomendacion) {
        this.recomendacion = recomendacion;
    }

    public boolean isConfiable() {
        return confiable;
    }

    public void setConfiable(boolean confiable) {
        this.confiable = confiable;
    }
    
    
}
