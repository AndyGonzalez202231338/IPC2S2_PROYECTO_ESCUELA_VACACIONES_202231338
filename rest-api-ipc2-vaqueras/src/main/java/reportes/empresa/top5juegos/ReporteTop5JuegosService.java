/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.top5juegos;

import conexion.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReporteTop5JuegosService {

    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) {
        Connection conn = null;

        try {
            System.out.println("\n=== GENERANDO REPORTE TOP 5 JUEGOS MÁS VENDIDOS ===");
            System.out.println("Empresa ID: " + idEmpresa);
            System.out.println("Fechas: " + fechaInicio + " - " + fechaFin);

            conn = DBConnectionSingleton.getInstance().getConnection();

            // 1. Obtener nombre de la empresa
            String nombreEmpresa = obtenerNombreEmpresa(conn, idEmpresa);
            
            // 2. Obtener datos del Top 5 para la empresa específica
            List<TopJuegoDto> topJuegos = obtenerTop5JuegosEmpresa(conn, fechaInicio, fechaFin, idEmpresa);

            // 3. Calcular estadísticas generales para la empresa
            EstadisticasDto estadisticas = calcularEstadisticasEmpresa(conn, fechaInicio, fechaFin, idEmpresa, topJuegos);

            // 4. Imprimir en consola (para debug)
            imprimirReporteConsola(topJuegos, estadisticas, fechaInicio, fechaFin, nombreEmpresa);

            // 5. Generar PDF con JasperReports
            System.out.println("Generando PDF...");
            return generarPDFJasper(topJuegos, estadisticas, fechaInicio, fechaFin, idEmpresa, nombreEmpresa);

        } catch (Exception e) {
            System.err.println("ERROR generando reporte Top 5 Juegos: " + e.getMessage());
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

    private List<TopJuegoDto> obtenerTop5JuegosEmpresa(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) throws SQLException {
        List<TopJuegoDto> topJuegos = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("v.titulo as titulo_juego, ")
           .append("e.nombre as empresa_desarrolladora, ")
           .append("COUNT(c.id_compra) as cantidad_ventas ")
           .append("FROM compra c ")
           .append("JOIN videojuego v ON c.id_videojuego = v.id_videojuego ")
           .append("JOIN empresa e ON v.id_empresa = e.id_empresa ")
           .append("WHERE e.id_empresa = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND c.fecha_compra BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }

        sql.append("GROUP BY v.id_videojuego, v.titulo, e.nombre ")
           .append("ORDER BY cantidad_ventas DESC, v.titulo ASC ")
           .append("LIMIT 5");

        System.out.println("SQL Top 5 Juegos Empresa: " + sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                int posicion = 1;
                while (rs.next()) {
                    topJuegos.add(new TopJuegoDto(
                        posicion++,
                        rs.getString("titulo_juego"),
                        rs.getString("empresa_desarrolladora"),
                        rs.getInt("cantidad_ventas")
                    ));
                }
            }
        }

        System.out.println("Juegos obtenidos para empresa " + idEmpresa + ": " + topJuegos.size());
        return topJuegos;
    }

    private EstadisticasDto calcularEstadisticasEmpresa(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, 
                                                       int idEmpresa, List<TopJuegoDto> topJuegos) throws SQLException {
        EstadisticasDto estadisticas = new EstadisticasDto();

        // Obtener estadísticas generales para la empresa
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("COUNT(DISTINCT v.id_videojuego) as total_juegos_vendidos, ")
           .append("COUNT(DISTINCT c.id_usuario) as total_compradores, ")
           .append("COUNT(c.id_compra) as total_ventas ")
           .append("FROM compra c ")
           .append("JOIN videojuego v ON c.id_videojuego = v.id_videojuego ")
           .append("WHERE v.id_empresa = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND c.fecha_compra BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    estadisticas.setTotalJuegosVendidos(rs.getInt("total_juegos_vendidos"));
                    estadisticas.setTotalCompradores(rs.getInt("total_compradores"));
                    estadisticas.setTotalVentas(rs.getInt("total_ventas"));
                }
            }
        }

        // Calcular porcentaje del top 5 respecto al total de la empresa
        if (estadisticas.getTotalVentas() > 0 && !topJuegos.isEmpty()) {
            int ventasTop5 = topJuegos.stream()
                .mapToInt(TopJuegoDto::getCantidadVentas)
                .sum();
            double porcentaje = (ventasTop5 * 100.0) / estadisticas.getTotalVentas();
            estadisticas.setPorcentajeTop5(porcentaje);
        }

        return estadisticas;
    }

    private byte[] generarPDFJasper(List<TopJuegoDto> topJuegos,
                                   EstadisticasDto estadisticas,
                                   LocalDate fechaInicio, LocalDate fechaFin,
                                   int idEmpresa, String nombreEmpresa) throws JRException {

        try {
            // Cargar el archivo .jasper
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream reportStream = classLoader.getResourceAsStream("reports/ReporteTop5Juegos.jasper");

            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo ReporteTop5Juegos.jasper");
            }

            // Preparar parámetros
            Map<String, Object> parameters = new HashMap<>();

            // Información del período - CORREGIDO: Usar String simple
            if (fechaInicio != null && fechaFin != null) {
                parameters.put("periodoTexto", "Período: " + fechaInicio.toString() + " al " + fechaFin.toString());
                parameters.put("tituloReporte", "TOP 5 JUEGOS MÁS VENDIDOS");
                parameters.put("subtituloReporte", "Del " + fechaInicio.toString() + " al " + fechaFin.toString());
            } else {
                parameters.put("periodoTexto", "Período: Histórico Completo");
                parameters.put("tituloReporte", "TOP 5 JUEGOS MÁS VENDIDOS");
                parameters.put("subtituloReporte", "Histórico Completo");
            }

            // Información de la empresa
            parameters.put("nombreEmpresa", nombreEmpresa);
            parameters.put("idEmpresa", idEmpresa);
            
            // Formatear fecha de generación
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            parameters.put("fechaGeneracion", sdf.format(new java.util.Date()));

            // Estadísticas generales
            parameters.put("totalJuegosVendidos", estadisticas.getTotalJuegosVendidos());
            parameters.put("totalCompradores", estadisticas.getTotalCompradores());
            parameters.put("totalVentas", estadisticas.getTotalVentas());
            parameters.put("porcentajeTop5", String.format("%.1f", estadisticas.getPorcentajeTop5()));

            // Preparar datasource para el top 5
            JRDataSource dataSource = new JRBeanCollectionDataSource(topJuegos);

            // Llenar reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);

            // Exportar a PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            System.err.println("Error de JasperReports: " + e.getMessage());
            throw e;
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

    private void imprimirReporteConsola(List<TopJuegoDto> topJuegos,
                                       EstadisticasDto estadisticas,
                                       LocalDate fechaInicio, LocalDate fechaFin,
                                       String nombreEmpresa) {

        System.out.println("\n" + "=".repeat(120));
        System.out.println("REPORTE TOP 5 JUEGOS MÁS VENDIDOS");
        System.out.println("Empresa: " + nombreEmpresa);
        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("=".repeat(120));

        if (!topJuegos.isEmpty()) {
            System.out.println("\nTOP 5 JUEGOS MÁS VENDIDOS:");
            System.out.println("-".repeat(80));
            System.out.printf("%-5s %-40s %-25s %-10s%n",
                    "POS", "JUEGO", "EMPRESA", "VENTAS");
            System.out.println("-".repeat(80));

            for (TopJuegoDto juego : topJuegos) {
                System.out.printf("%-5d %-40s %-25s %-10d%n",
                        juego.getPosicion(),
                        juego.getTituloJuego().length() > 40 
                            ? juego.getTituloJuego().substring(0, 37) + "..." 
                            : juego.getTituloJuego(),
                        juego.getEmpresaDesarrolladora().length() > 25 
                            ? juego.getEmpresaDesarrolladora().substring(0, 22) + "..." 
                            : juego.getEmpresaDesarrolladora(),
                        juego.getCantidadVentas());
            }

            System.out.println("\n" + "=".repeat(120));
            System.out.println("ESTADÍSTICAS GENERALES:");
            System.out.println("-".repeat(50));
            System.out.println("Total de juegos vendidos: " + estadisticas.getTotalJuegosVendidos());
            System.out.println("Total de compradores únicos: " + estadisticas.getTotalCompradores());
            System.out.println("Total de ventas: " + estadisticas.getTotalVentas());
            System.out.printf("Porcentaje del Top 5: %.1f%%%n", estadisticas.getPorcentajeTop5());
            System.out.println("=".repeat(120));
        } else {
            System.out.println("\nNo se encontraron ventas para los criterios seleccionados.");
        }
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
            
            // Obtener datos del Top 5 para la empresa específica
            List<TopJuegoDto> topJuegos = obtenerTop5JuegosEmpresa(conn, fechaInicio, fechaFin, idEmpresa);

            // Calcular estadísticas
            EstadisticasDto estadisticas = calcularEstadisticasEmpresa(conn, fechaInicio, fechaFin, idEmpresa, topJuegos);

            // Crear respuesta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("empresa", nombreEmpresa);
            respuesta.put("idEmpresa", idEmpresa);
            respuesta.put("topJuegos", topJuegos);
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