/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package banner.models;

import comentario.model.Calificacion;
import java.util.List;

/**
 *
 * @author andy
 */
public class AlgoritmoPopularidad {


    public static double calcularPopularidadSimple(double promedioCalificacion, int totalCalificaciones) {
        if (totalCalificaciones == 0) {
            return 0.0; // Sin calificaciones
        }

        if (totalCalificaciones == 1) {
            // Con solo una calificación, reducir el peso
            return (promedioCalificacion / 5.0) * 100 * 0.5; // 50% del valor
        }

        if (totalCalificaciones == 2) {
            // Con dos calificaciones
            return (promedioCalificacion / 5.0) * 100 * 0.7; // 70% del valor
        }

        if (totalCalificaciones == 3) {
            // Con tres calificaciones
            return (promedioCalificacion / 5.0) * 100 * 0.85; // 85% del valor
        }

        // Con 4 o más calificaciones, usar valor completo
        return (promedioCalificacion / 5.0) * 100;
    }


    public static double calcularPopularidadBasica(double promedioCalificacion, int totalCalificaciones) {
        if (totalCalificaciones == 0) {
            return 0.0;
        }

        // Fórmula: (promedio * factor_de_cantidad) escalado a 0-100
        double factorCantidad = calcularFactorCantidad(totalCalificaciones);
        return promedioCalificacion * 20 * factorCantidad; // 20 para escalar a 0-100
    }

    /**
     * Factor basado en cantidad de calificaciones
     */
    private static double calcularFactorCantidad(int totalCalificaciones) {
        switch (totalCalificaciones) {
            case 0:
                return 0.0;
            case 1:
                return 0.3;  // 30% del valor
            case 2:
                return 0.5;  // 50%
            case 3:
                return 0.7;  // 70%
            case 4:
                return 0.8;  // 80%
            case 5:
                return 0.9;  // 90%
            default:
                return 1.0; // 100% para 6 o más
        }
    }

    /**
     * Solo clasifica en 3 categorías simples
     * @param scorePopularidad
     * @return 
     */
    public static String clasificarPopularidadSimple(double scorePopularidad) {
        if (scorePopularidad >= 70) {
            return "ALTA";
        }
        if (scorePopularidad >= 40) {
            return "MEDIA";
        }
        return "BAJA";
    }

    /**
     * Determina si es confiable basado en cantidad de calificaciones
     */
    public static boolean esConfiable(int totalCalificaciones) {
        return totalCalificaciones >= 3; // Necesita al menos 3 calificaciones para ser confiable
    }

    /**
     * Recomienda si mostrar o no la popularidad
     */
    public static String getRecomendacion(int totalCalificaciones) {
        if (totalCalificaciones == 0) {
            return "SIN CALIFICACIONES";
        }
        if (totalCalificaciones == 1) {
            return "SOLO 1 CALIFICACIÓN - POCO CONFIABLE";
        }
        if (totalCalificaciones == 2) {
            return "SOLO 2 CALIFICACIONES - LIMITADO";
        }
        if (totalCalificaciones <= 5) {
            return "POCAS CALIFICACIONES";
        }
        return "CONFIABLE";
    }
}
