/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.feedback;

import conexion.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/**
 *
 * @author andy
 */
public class ReporteFeedbackService {

    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) {
    Connection conn = null;

    try {
        conn = DBConnectionSingleton.getInstance().getConnection();

        String nombreEmpresa = obtenerNombreEmpresa(conn, idEmpresa);
        
        List<JuegoRatingDto> juegosConRatings = obtenerJuegosConRatings(conn, fechaInicio, fechaFin, idEmpresa);
        
        List<PeorCalificacionDto> peoresCalificaciones = obtenerPeoresCalificaciones(conn, fechaInicio, fechaFin, idEmpresa);
        
        EstadisticasFeedbackDto estadisticas = calcularEstadisticas(juegosConRatings, peoresCalificaciones);

        imprimirReporteConsola(juegosConRatings, peoresCalificaciones, estadisticas, fechaInicio, fechaFin, nombreEmpresa);
        return generarPDFJasper(conn, juegosConRatings, peoresCalificaciones, estadisticas, fechaInicio, fechaFin, idEmpresa, nombreEmpresa);

    } catch (Exception e) {
        System.err.println("ERROR generando reporte de feedback: " + e.getMessage());
        e.printStackTrace();
        throw new RuntimeException("Error generando reporte: " + e.getMessage(), e);
    } finally {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}

    private List<JuegoRatingDto> obtenerJuegosConRatings(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) throws SQLException {
        List<JuegoRatingDto> juegos = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("v.id_videojuego, ")
           .append("v.titulo as titulo_juego, ")
           .append("COUNT(DISTINCT c.id_calificacion) as total_calificaciones, ")
           .append("AVG(c.calificacion) as promedio_rating, ")
           .append("MIN(c.calificacion) as peor_calificacion, ")
           .append("MAX(c.calificacion) as mejor_calificacion ")
           .append("FROM videojuego v ")
           .append("LEFT JOIN biblioteca_usuario bu ON v.id_videojuego = bu.id_videojuego ")
           .append("LEFT JOIN calificacion c ON bu.id_biblioteca = c.id_biblioteca ")
           .append("WHERE v.id_empresa = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND c.fecha_hora BETWEEN ? AND ? ");
            parametros.add(java.sql.Timestamp.valueOf(fechaInicio.atStartOfDay()));
            parametros.add(java.sql.Timestamp.valueOf(fechaFin.atTime(23, 59, 59)));
        }

        sql.append("GROUP BY v.id_videojuego, v.titulo ").append("ORDER BY promedio_rating DESC, v.titulo ASC");

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double promedio = rs.getDouble("promedio_rating");
                    int totalCalificaciones = rs.getInt("total_calificaciones");
                    
                    if (totalCalificaciones > 0) {
                        juegos.add(new JuegoRatingDto(
                            rs.getInt("id_videojuego"),
                            rs.getString("titulo_juego"),
                            totalCalificaciones,
                            Math.round(promedio * 10.0) / 10.0, // Redondear a 1 decimal
                            rs.getInt("peor_calificacion"),
                            rs.getInt("mejor_calificacion")
                        ));
                    }
                }
            }
        }

        System.out.println("Juegos con ratings obtenidos: " + juegos.size());
        return juegos;
    }

    private List<PeorCalificacionDto> obtenerPeoresCalificaciones(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) throws SQLException {
        List<PeorCalificacionDto> peoresCalificaciones = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("v.titulo as titulo_juego, ")
           .append("u.nombre as nombre_usuario, ")
           .append("c.calificacion, ")
           .append("c.fecha_hora, ")
           .append("c.id_calificacion ")
           .append("FROM calificacion c ")
           .append("JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca ")
           .append("JOIN videojuego v ON bu.id_videojuego = v.id_videojuego ")
           .append("JOIN usuario u ON c.id_usuario = u.id_usuario ")
           .append("WHERE v.id_empresa = ? ")
           .append("AND c.calificacion < 3 ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND c.fecha_hora BETWEEN ? AND ? ");
            parametros.add(java.sql.Timestamp.valueOf(fechaInicio.atStartOfDay()));
            parametros.add(java.sql.Timestamp.valueOf(fechaFin.atTime(23, 59, 59)));
        }

        sql.append("ORDER BY c.calificacion ASC, c.fecha_hora DESC ")
           .append("LIMIT 20");

        System.out.println("SQL Peores Calificaciones: " + sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    peoresCalificaciones.add(new PeorCalificacionDto(
                        posicion++,
                        rs.getString("titulo_juego"),
                        rs.getString("nombre_usuario"),
                        rs.getInt("calificacion"),
                        rs.getTimestamp("fecha_hora").toLocalDateTime()
                    ));
                }
            }
        }

        System.out.println("Peores calificaciones obtenidas: " + peoresCalificaciones.size());
        return peoresCalificaciones;
    }

    private EstadisticasFeedbackDto calcularEstadisticas(List<JuegoRatingDto> juegos, List<PeorCalificacionDto> peoresCalif) {
        EstadisticasFeedbackDto estadisticas = new EstadisticasFeedbackDto();
        
        estadisticas.setTotalJuegosCalificados(juegos.size());
        estadisticas.setTotalPeoresCalificaciones(peoresCalif.size());
        
        // Calcular promedio general
        if (!juegos.isEmpty()) {
            double sumaPromedios = juegos.stream()
                .mapToDouble(JuegoRatingDto::getPromedioRating)
                .sum();
            estadisticas.setPromedioGeneral(Math.round((sumaPromedios / juegos.size()) * 10.0) / 10.0);
            
            // Encontrar juego mejor calificado
            JuegoRatingDto mejorJuego = juegos.stream()
                .max(Comparator.comparingDouble(JuegoRatingDto::getPromedioRating))
                .orElse(null);
            if (mejorJuego != null) {
                estadisticas.setMejorJuego(mejorJuego.getTituloJuego());
                estadisticas.setMejorRating(mejorJuego.getPromedioRating());
            }
            
            // Encontrar juego peor calificado
            JuegoRatingDto peorJuego = juegos.stream()
                .min(Comparator.comparingDouble(JuegoRatingDto::getPromedioRating))
                .orElse(null);
            if (peorJuego != null) {
                estadisticas.setPeorJuego(peorJuego.getTituloJuego());
                estadisticas.setPeorRating(peorJuego.getPromedioRating());
            }
        }
        
        return estadisticas;
    }

    private byte[] generarPDFJasper(Connection conn, List<JuegoRatingDto> juegosConRatings, List<PeorCalificacionDto> peoresCalificaciones, EstadisticasFeedbackDto estadisticas, LocalDate fechaInicio, LocalDate fechaFin,
        int idEmpresa, String nombreEmpresa) throws JRException {

    try {
        InputStream reportStream = getClass().getClassLoader()
            .getResourceAsStream("reports/ReporteFeedback.jasper");

        if (reportStream == null) {
            throw new RuntimeException("No se encontró ReporteFeedback.jasper en resources/reports/");
        }

        // Preparar parámetros
        Map<String, Object> parameters = new HashMap<>();

        // Información del período
        if (fechaInicio != null && fechaFin != null) {
            parameters.put("periodoTexto", "Período: " + fechaInicio.toString() + " al " + fechaFin.toString());
            parameters.put("subtituloReporte", "Del " + fechaInicio.toString() + " al " + fechaFin.toString());
        } else {
            parameters.put("periodoTexto", "Período: Histórico Completo");
            parameters.put("subtituloReporte", "Histórico Completo");
        }

        parameters.put("tituloReporte", "REPORTE DE FEEDBACK");
        parameters.put("nombreEmpresa", nombreEmpresa);
        parameters.put("idEmpresa", idEmpresa);
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        parameters.put("fechaGeneracion", sdf.format(new java.util.Date()));

        // Estadísticas
        parameters.put("totalJuegosCalificados", estadisticas.getTotalJuegosCalificados());
        parameters.put("totalPeoresCalificaciones", estadisticas.getTotalPeoresCalificaciones());
        parameters.put("promedioGeneral", String.format("%.1f", estadisticas.getPromedioGeneral()));
        parameters.put("mejorJuego", estadisticas.getMejorJuego());
        parameters.put("mejorRating", String.format("%.1f", estadisticas.getMejorRating()));
        parameters.put("peorJuego", estadisticas.getPeorJuego());
        parameters.put("peorRating", String.format("%.1f", estadisticas.getPeorRating()));

        // DataSources para subreportes
        parameters.put("juegosDataSource", new JRBeanCollectionDataSource(juegosConRatings));
        parameters.put("peoresCalifDataSource", new JRBeanCollectionDataSource(peoresCalificaciones));
        parameters.put("tieneJuegos", !juegosConRatings.isEmpty());
        parameters.put("tienePeoresCalif", !peoresCalificaciones.isEmpty());
        parameters.put("SUBREPORT_DIR", "reports/");

        System.out.println("DEBUG - Llenando reporte...");
        
        JREmptyDataSource mainDataSource = new JREmptyDataSource(1);
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, mainDataSource);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

        return baos.toByteArray();

    } catch (Exception e) {
        System.err.println("ERROR en generarPDFJasper: " + e.getMessage());
        e.printStackTrace();
        throw new JRException("Error generando PDF: " + e.getMessage(), e);
    }
}
    
    private String obtenerNombreEmpresa(Connection conn, int idEmpresa) throws SQLException {
        String sql = "SELECT nombre FROM empresa WHERE id_empresa = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpresa);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        }
        
        return "Empresa ID: " + idEmpresa;
    }

    private void imprimirReporteConsola(List<JuegoRatingDto> juegos, List<PeorCalificacionDto> peoresCalif, EstadisticasFeedbackDto estadisticas, LocalDate fechaInicio, LocalDate fechaFin,
        String nombreEmpresa) {

        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("=".repeat(120));

        if (!juegos.isEmpty()) {
            System.out.println("\nJUEGOS CON CALIFICACIONES PROMEDIO:");
            System.out.println("-".repeat(100));
            System.out.printf("%-40s %-15s %-15s %-15s %-15s%n",
                    "JUEGO", "CALIFICACIONES", "PROMEDIO", "PEOR", "MEJOR");
            System.out.println("-".repeat(100));

            for (JuegoRatingDto juego : juegos) {
                System.out.printf("%-40s %-15d %-15.1f %-15d %-15d%n",
                        juego.getTituloJuego().length() > 40 
                            ? juego.getTituloJuego().substring(0, 37) + "..." 
                            : juego.getTituloJuego(),
                        juego.getTotalCalificaciones(),
                        juego.getPromedioRating(),
                        juego.getPeorCalificacion(),
                        juego.getMejorCalificacion());
            }

            System.out.println("\nESTADÍSTICAS GENERALES:");
            System.out.println("-".repeat(50));
            System.out.println("Total juegos calificados: " + estadisticas.getTotalJuegosCalificados());
            System.out.printf("Promedio general: %.1f%n", estadisticas.getPromedioGeneral());
            System.out.println("Mejor juego: " + estadisticas.getMejorJuego() + " (" + estadisticas.getMejorRating() + ")");
            System.out.println("Peor juego: " + estadisticas.getPeorJuego() + " (" + estadisticas.getPeorRating() + ")");
        }

        if (!peoresCalif.isEmpty()) {
            System.out.println("\nPEORES CALIFICACIONES (< 3):");
            System.out.println("-".repeat(90));
            System.out.printf("%-5s %-30s %-20s %-10s %-20s%n",
                    "#", "JUEGO", "USUARIO", "RATING", "FECHA");
            System.out.println("-".repeat(90));

            for (PeorCalificacionDto calif : peoresCalif) {
                System.out.printf("%-5d %-30s %-20s %-10d %-20s%n",
                        calif.getPosicion(),
                        calif.getTituloJuego().length() > 30 
                            ? calif.getTituloJuego().substring(0, 27) + "..." 
                            : calif.getTituloJuego(),
                        calif.getNombreUsuario().length() > 20 
                            ? calif.getNombreUsuario().substring(0, 17) + "..." 
                            : calif.getNombreUsuario(),
                        calif.getCalificacion(),
                        calif.getFechaHora().toString());
            }
            System.out.println("Total peores calificaciones: " + peoresCalif.size());
        }

        if (juegos.isEmpty() && peoresCalif.isEmpty()) {
            System.out.println("\nNo se encontraron calificaciones para los criterios seleccionados.");
        }
        
        System.out.println("=".repeat(120));
    }

    /**
     * Método alternativo para obtener datos en formato JSON/objeto
     */
    public Map<String, Object> obtenerDatosReporte(LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) {
        Connection conn = null;

        try {
            conn = DBConnectionSingleton.getInstance().getConnection();

            // Obtener nombre de la empresa
            String nombreEmpresa = obtenerNombreEmpresa(conn, idEmpresa);
            
            // Obtener datos
            List<JuegoRatingDto> juegosConRatings = obtenerJuegosConRatings(conn, fechaInicio, fechaFin, idEmpresa);
            List<PeorCalificacionDto> peoresCalificaciones = obtenerPeoresCalificaciones(conn, fechaInicio, fechaFin, idEmpresa);
            EstadisticasFeedbackDto estadisticas = calcularEstadisticas(juegosConRatings, peoresCalificaciones);

            // Crear respuesta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("empresa", nombreEmpresa);
            respuesta.put("idEmpresa", idEmpresa);
            respuesta.put("juegosConRatings", juegosConRatings);
            respuesta.put("peoresCalificaciones", peoresCalificaciones);
            respuesta.put("estadisticas", estadisticas);

            if (fechaInicio != null && fechaFin != null) {
                respuesta.put("periodo", fechaInicio + " - " + fechaFin);
            } else {
                respuesta.put("periodo", "Histórico completo");
            }

            respuesta.put("fechaGeneracion", new java.util.Date());

            return respuesta;

        } catch (Exception e) {
            System.err.println("ERROR obteniendo datos del reporte: " + e.getMessage());
            throw new RuntimeException("Error obteniendo datos: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}
