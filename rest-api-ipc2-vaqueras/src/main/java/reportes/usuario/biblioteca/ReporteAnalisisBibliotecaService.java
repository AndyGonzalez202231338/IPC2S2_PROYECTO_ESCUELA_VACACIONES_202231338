/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.biblioteca;

import conexion.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReporteAnalisisBibliotecaService {

    public byte[] generarReportePDF(int idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {

            String nombreUsuario = obtenerNombreUsuario(conn, idUsuario);
            int totalJuegos = obtenerTotalJuegosBiblioteca(conn, idUsuario, fechaInicio, fechaFin);

            List<AnalisisJuegoBibliotecaDto> analisisJuegos
                    = obtenerAnalisisJuegos(conn, idUsuario, fechaInicio, fechaFin);

            System.out.println("Juegos en reporte: " + analisisJuegos.size());

            Map<String, Object> params = new HashMap<>();
            params.put("nombreUsuario", nombreUsuario);
            params.put("totalJuegos", totalJuegos);
            params.put("periodoTexto", getPeriodoTexto(fechaInicio, fechaFin));
            params.put("fechaGeneracion", new java.util.Date());

            JRBeanCollectionDataSource mainDataSource
                    = new JRBeanCollectionDataSource(analisisJuegos);

            InputStream reportStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("reports/ReporteAnalisisBiblioteca.jasper");

            if (reportStream == null) {
                throw new RuntimeException("No se encontró ReporteAnalisisBiblioteca.jasper");
            }

            JasperPrint print = JasperFillManager.fillReport(
                    reportStream,
                    params,
                    mainDataSource
            );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(print, baos);

            return baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generando reporte de análisis de biblioteca", e);
        }
    }

    private String obtenerNombreUsuario(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT nombre FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getString("nombre") : "Usuario ID " + idUsuario;
        }
    }

    private int obtenerTotalJuegosBiblioteca(Connection conn, int idUsuario, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT COUNT(DISTINCT bu.id_videojuego) AS total
            FROM biblioteca_usuario bu
            JOIN compra c ON bu.id_compra = c.id_compra
            WHERE bu.id_usuario = ? 
            AND bu.tipo_adquisicion = 'COMPRA'
        """);

        if (fechaInicio != null && fechaFin != null) {
            sql.append(" AND c.fecha_compra BETWEEN ? AND ?");
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, idUsuario);

            if (fechaInicio != null && fechaFin != null) {
                ps.setDate(2, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(3, java.sql.Date.valueOf(fechaFin));
            }

            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("total") : 0;
        }
    }

    private List<AnalisisJuegoBibliotecaDto> obtenerAnalisisJuegos(Connection conn, int idUsuario,
            LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {

        List<AnalisisJuegoBibliotecaDto> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
        -- Primero obtenemos los videojuegos del usuario con su calificación personal
        WITH juegos_usuario AS (
            SELECT 
                v.id_videojuego,
                v.titulo,
                c.calificacion as calificacion_personal
            FROM biblioteca_usuario bu
            JOIN compra co ON bu.id_compra = co.id_compra
            JOIN videojuego v ON bu.id_videojuego = v.id_videojuego
            LEFT JOIN calificacion c ON bu.id_biblioteca = c.id_biblioteca 
                                     AND c.id_usuario = ?
            WHERE bu.id_usuario = ? 
            AND bu.tipo_adquisicion = 'COMPRA'
    """);

        if (fechaInicio != null && fechaFin != null) {
            sql.append(" AND co.fecha_compra BETWEEN ? AND ?");
        }

        sql.append("""
            AND c.calificacion IS NOT NULL
        )
        
        -- Ahora unimos con las calificaciones de TODOS los usuarios
        SELECT 
            ju.titulo,
            ju.calificacion_personal,
            AVG(c2.calificacion) AS promedio_comunidad,
            COUNT(DISTINCT c2.id_usuario) AS total_usuarios
        FROM juegos_usuario ju
        LEFT JOIN biblioteca_usuario bu2 ON ju.id_videojuego = bu2.id_videojuego
        LEFT JOIN calificacion c2 ON bu2.id_biblioteca = c2.id_biblioteca
        GROUP BY ju.id_videojuego, ju.titulo, ju.calificacion_personal
        ORDER BY ju.titulo
    """);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;

            // Primer parámetro para calificación personal
            ps.setInt(paramIndex++, idUsuario);

            // Segundo parámetro para WHERE principal
            ps.setInt(paramIndex++, idUsuario);

            if (fechaInicio != null && fechaFin != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaFin));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String titulo = rs.getString("titulo");
                double calPersonal = rs.getDouble("calificacion_personal");
                double promedioComunidad = rs.getDouble("promedio_comunidad");
                int totalUsuarios = rs.getInt("total_usuarios");

                // Aplicar fórmula
                Double calComunidadFinal = null;
                if (totalUsuarios > 0) {
                    calComunidadFinal = calcularNotaFinalComunidad(promedioComunidad, totalUsuarios);
                }

                lista.add(new AnalisisJuegoBibliotecaDto(
                        titulo,
                        calComunidadFinal,
                        calPersonal
                ));
            }
        }

        return lista;
    }

    /**
     * Replica exactamente la lógica de
     * CalificacionCrudService.getNotaFinalVideojuego()
     */
    private Double calcularNotaFinalComunidad(double promedio, int total) {
        if (total == 0) {
            return null;  // No hay calificaciones de comunidad
        }

        double factor;
        if (total == 1) {
            factor = 0.6;
        } else if (total == 2) {
            factor = 0.75;
        } else if (total <= 4) {
            factor = 0.9;
        } else {
            factor = 1.0;
        }

        // Redondear a 2 decimales (igual que tu método original)
        return Math.round(promedio * factor * 100.0) / 100.0;
    }

    private List<CategoriaFavoritaDto> obtenerCategoriasFavoritas(Connection conn, int idUsuario, LocalDate fechaInicio, LocalDate fechaFin, int totalJuegos) throws SQLException {

        List<CategoriaFavoritaDto> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT 
                cat.nombre,
                COUNT(DISTINCT vc.id_videojuego) AS cantidad_juegos
            FROM biblioteca_usuario bu
            JOIN compra c ON bu.id_compra = c.id_compra
            JOIN videojuego v ON bu.id_videojuego = v.id_videojuego
            JOIN videojuego_categoria vc ON v.id_videojuego = vc.id_videojuego
            JOIN categoria cat ON vc.id_categoria = cat.id_categoria
            WHERE bu.id_usuario = ? 
            AND bu.tipo_adquisicion = 'COMPRA'
            AND vc.estado = 'APROBADA'
        """);

        if (fechaInicio != null && fechaFin != null) {
            sql.append(" AND c.fecha_compra BETWEEN ? AND ?");
        }

        sql.append("""
            GROUP BY cat.id_categoria, cat.nombre
            HAVING COUNT(DISTINCT vc.id_videojuego) > 0
            ORDER BY cantidad_juegos DESC
            LIMIT 10
        """);

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, idUsuario);

            if (fechaInicio != null && fechaFin != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(paramIndex++, java.sql.Date.valueOf(fechaFin));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new CategoriaFavoritaDto(
                        rs.getString("nombre"),
                        rs.getInt("cantidad_juegos"),
                        totalJuegos
                ));
            }
        }

        return lista;
    }

    private String getPeriodoTexto(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null) {
            return "Del " + fechaInicio + " al " + fechaFin;
        }
        return "Histórico completo";
    }
}
