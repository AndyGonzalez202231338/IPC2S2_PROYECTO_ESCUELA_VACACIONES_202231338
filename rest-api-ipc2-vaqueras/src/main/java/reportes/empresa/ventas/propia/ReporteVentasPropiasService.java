package reportes.empresa.ventas.propia;

import conexion.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReporteVentasPropiasService {

    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin, int idEmpresa) {
        Connection conn = null;

        try {
            conn = DBConnectionSingleton.getInstance().getConnection();

            BigDecimal porcentajeComision = obtenerPorcentajeComision(conn, idEmpresa, fechaInicio, fechaFin);

            List<VentasPropiaDto> ventas = obtenerVentasPorEmpresa(conn, fechaInicio, fechaFin, idEmpresa, porcentajeComision);

            ResumenVentasDto resumen = calcularResumen(ventas, porcentajeComision);

            imprimirReporteConsola(ventas, resumen, fechaInicio, fechaFin, idEmpresa, porcentajeComision);

            return generarPDFJasper(ventas, resumen, fechaInicio, fechaFin, idEmpresa, porcentajeComision);

        } catch (Exception e) {
            System.err.println("ERROR generando reporte de ventas propias: " + e.getMessage());
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

    /**
     * Obtiene el porcentaje de comisión activo para una empresa
     */
    private BigDecimal obtenerPorcentajeComision(Connection conn, int idEmpresa,
            LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT porcentaje FROM comision ")
                .append("WHERE id_empresa = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            // Buscar comisión activa durante el período
            sql.append("AND (fecha_inicio <= ? OR fecha_inicio IS NULL) ")
                    .append("AND (fecha_final >= ? OR fecha_final IS NULL) ")
                    .append("ORDER BY fecha_inicio DESC LIMIT 1");

            parametros.add(java.sql.Date.valueOf(fechaFin));
            parametros.add(java.sql.Date.valueOf(fechaInicio));
        } else {
            // Buscar la comisión global más reciente
            sql.append("ORDER BY fecha_inicio DESC LIMIT 1");
        }

        System.out.println("SQL para obtener comisión: " + sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("porcentaje");
                }
            }
        }

        // Si no se encuentra comisión específica, usar valor por defecto
        System.out.println("No se encontró comisión específica para la empresa, usando valor por defecto (30%)");
        return new BigDecimal("30.00");
    }

    private List<VentasPropiaDto> obtenerVentasPorEmpresa(Connection conn, LocalDate fechaInicio,
            LocalDate fechaFin, int idEmpresa,
            BigDecimal porcentajeComision) throws SQLException {

        List<VentasPropiaDto> ventas = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
                .append("v.titulo as titulo_videojuego, ")
                .append("COUNT(c.id_compra) as cantidad_ventas, ")
                .append("SUM(c.monto_pago) as monto_bruto, ")
                .append("SUM(c.comision_aplicada) as comision_total ")
                .append("FROM videojuego v ")
                .append("JOIN compra c ON v.id_videojuego = c.id_videojuego ")
                .append("WHERE v.id_empresa = ? ");

        List<Object> parametros = new ArrayList<>();
        parametros.add(idEmpresa);

        if (fechaInicio != null && fechaFin != null) {
            sql.append("AND c.fecha_compra BETWEEN ? AND ? ");
            parametros.add(java.sql.Date.valueOf(fechaInicio));
            parametros.add(java.sql.Date.valueOf(fechaFin));
        }

        sql.append("GROUP BY v.id_videojuego, v.titulo ")
                .append("HAVING cantidad_ventas > 0 ")
                .append("ORDER BY monto_bruto DESC");

        System.out.println("SQL Ventas Propias: " + sql.toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BigDecimal montoBruto = rs.getBigDecimal("monto_bruto");
                    BigDecimal comisionCalculada = montoBruto
                            .multiply(porcentajeComision)
                            .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);

                    ventas.add(new VentasPropiaDto(
                            rs.getString("titulo_videojuego"),
                            rs.getInt("cantidad_ventas"),
                            montoBruto,
                            comisionCalculada,
                            porcentajeComision
                    ));
                }
            }
        }

        System.out.println("Ventas obtenidas: " + ventas.size());
        return ventas;
    }

    private ResumenVentasDto calcularResumen(List<VentasPropiaDto> ventas, BigDecimal porcentajeComision) {
        BigDecimal totalBruto = BigDecimal.ZERO;
        BigDecimal totalComision = BigDecimal.ZERO;
        BigDecimal totalNeto = BigDecimal.ZERO;
        int totalVentas = 0;

        for (VentasPropiaDto venta : ventas) {
            totalBruto = totalBruto.add(venta.getMontoBruto());
            totalComision = totalComision.add(venta.getComisionPlataforma());
            totalNeto = totalNeto.add(venta.getIngresoNeto());
            totalVentas += venta.getCantidadVentas();
        }

        return new ResumenVentasDto(
                totalBruto,
                totalComision,
                totalNeto,
                ventas.size(),
                totalVentas,
                porcentajeComision
        );
    }

    private byte[] generarPDFJasper(List<VentasPropiaDto> ventas,
            ResumenVentasDto resumen,
            LocalDate fechaInicio, LocalDate fechaFin,
            int idEmpresa, BigDecimal porcentajeComision) throws JRException {

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream reportStream = classLoader.getResourceAsStream("reports/ReporteVentasPropias.jasper");

            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo ReporteVentasPropias.jasper");
            }

            String nombreEmpresa = obtenerNombreEmpresa(idEmpresa);

            List<ComisionDto> historialComisiones = obtenerHistorialComisiones(idEmpresa);

            Map<String, Object> parameters = new HashMap<>();

            if (fechaInicio != null && fechaFin != null) {
                parameters.put("periodoTexto", "Período: " + fechaInicio + " al " + fechaFin);
            } else {
                parameters.put("periodoTexto", "Período: Histórico Completo");
            }

            parameters.put("nombreEmpresa", nombreEmpresa);
            parameters.put("idEmpresa", idEmpresa);

            parameters.put("totalVideojuegos", resumen.getTotalVideojuegos());
            parameters.put("totalVentas", resumen.getTotalVentas());
            parameters.put("totalBruto", resumen.getTotalBruto());
            parameters.put("totalComision", resumen.getTotalComision());
            parameters.put("totalNeto", resumen.getTotalNeto());
            parameters.put("porcentajeComisionAplicado", resumen.getPorcentajeComision());
            parameters.put("porcentajeComisionPromedio", resumen.getPorcentajeComisionPromedio());

            // Historial de comisiones para el reporte
            parameters.put("historialComisiones", new JRBeanCollectionDataSource(historialComisiones));
            parameters.put("tieneHistorialComisiones", !historialComisiones.isEmpty());

            // Fecha de generación
            parameters.put("fechaGeneracion", new java.util.Date());

            JRDataSource dataSourceVentas = new JRBeanCollectionDataSource(ventas);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSourceVentas);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);

            return baos.toByteArray();

        } catch (JRException e) {
            System.err.println("Error de JasperReports: " + e.getMessage());
            throw e;
        }
    }

    private List<ComisionDto> obtenerHistorialComisiones(int idEmpresa) {
        Connection conn = null;
        List<ComisionDto> historial = new ArrayList<>();

        try {
            conn = DBConnectionSingleton.getInstance().getConnection();
            String sql = "SELECT * FROM comision WHERE id_empresa = ? ORDER BY fecha_inicio DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idEmpresa);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        historial.add(new ComisionDto(
                                rs.getInt("id_comision"),
                                rs.getInt("id_empresa"),
                                rs.getBigDecimal("porcentaje"),
                                rs.getDate("fecha_inicio"),
                                rs.getDate("fecha_final"),
                                rs.getString("tipo_comision")
                        ));
                    }
                }
            }

            System.out.println("Historial de comisiones obtenido: " + historial.size() + " registros");

        } catch (SQLException e) {
            System.err.println("Error obteniendo historial de comisiones: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }

        return historial;
    }

    private String obtenerNombreEmpresa(int idEmpresa) {
        Connection conn = null;
        try {
            conn = DBConnectionSingleton.getInstance().getConnection();
            String sql = "SELECT nombre FROM empresa WHERE id_empresa = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idEmpresa);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("nombre");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo nombre de empresa: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
        return "Empresa ID: " + idEmpresa;
    }

    private void imprimirReporteConsola(List<VentasPropiaDto> ventas,
            ResumenVentasDto resumen,
            LocalDate fechaInicio, LocalDate fechaFin,
            int idEmpresa, BigDecimal porcentajeComision) {

        System.out.println("\n" + "=".repeat(120));
        System.out.println("REPORTE DE VENTAS PROPIAS");
        System.out.println("Empresa ID: " + idEmpresa);
        System.out.println("Comisión aplicada: " + porcentajeComision + "%");
        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("=".repeat(120));

        if (!ventas.isEmpty()) {
            System.out.println("\nDETALLE DE VENTAS POR VIDEOJUEGO:");
            System.out.println("-".repeat(110));
            System.out.printf("%-40s %-15s %-15s %-15s %-15s %-10s%n",
                    "VIDEOJUEGO", "VENTAS", "MONTO BRUTO", "COMISIÓN", "INGRESO NETO", "% COMISIÓN");
            System.out.println("-".repeat(110));

            for (VentasPropiaDto venta : ventas) {
                System.out.printf("%-40s %-15d Q%-14.2f Q%-14.2f Q%-14.2f %-9.1f%%%n",
                        venta.getTituloVideojuego().length() > 40
                        ? venta.getTituloVideojuego().substring(0, 37) + "..." : venta.getTituloVideojuego(),
                        venta.getCantidadVentas(),
                        venta.getMontoBruto(),
                        venta.getComisionPlataforma(),
                        venta.getIngresoNeto(),
                        venta.getPorcentajeComision());
            }

            System.out.println("\n" + "=".repeat(120));
            System.out.println("RESUMEN GENERAL:");
            System.out.println("-".repeat(60));
            System.out.println("Total videojuegos con ventas: " + resumen.getTotalVideojuegos());
            System.out.println("Total ventas realizadas: " + resumen.getTotalVentas());
            System.out.printf("Monto bruto total: Q%.2f%n", resumen.getTotalBruto());
            System.out.printf("Comisión total de plataforma: Q%.2f%n", resumen.getTotalComision());
            System.out.printf("Ingreso neto total: Q%.2f%n", resumen.getTotalNeto());
            System.out.printf("Porcentaje de comisión aplicado: %.1f%%%n", resumen.getPorcentajeComision());
            System.out.printf("Porcentaje promedio de comisión: %.1f%%%n", resumen.getPorcentajeComisionPromedio());
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

            BigDecimal porcentajeComision = obtenerPorcentajeComision(conn, idEmpresa, fechaInicio, fechaFin);

            List<VentasPropiaDto> ventas = obtenerVentasPorEmpresa(conn, fechaInicio, fechaFin, idEmpresa, porcentajeComision);

            ResumenVentasDto resumen = calcularResumen(ventas, porcentajeComision);

            String nombreEmpresa = obtenerNombreEmpresa(idEmpresa);

            List<ComisionDto> historialComisiones = obtenerHistorialComisiones(idEmpresa);

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("empresa", nombreEmpresa);
            respuesta.put("idEmpresa", idEmpresa);
            respuesta.put("porcentajeComision", porcentajeComision);
            respuesta.put("ventas", ventas);
            respuesta.put("resumen", resumen);
            respuesta.put("historialComisiones", historialComisiones);

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
