/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.comision.global;

import conexion.DBConnectionSingleton;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReporteGananciasGlobalesService {
    
    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin) {
        Connection conn = null;
        
        try {           
            conn = DBConnectionSingleton.getInstance().getConnection();
            
            // Obtener datos para el reporte
            List<ReporteGananciasGlobalesDto> empresas = obtenerDatosGanancias(conn, fechaInicio, fechaFin);
            ReporteGananciasGlobalesCompletoDto reporteCompleto = new ReporteGananciasGlobalesCompletoDto(empresas);
            
            // Imprimir en consola
            imprimirReporteConsola(reporteCompleto, fechaInicio, fechaFin);
            
            // Generar PDF con JasperReports
            byte[] pdfBytes = generarPDFJasper(reporteCompleto, fechaInicio, fechaFin);
            
            return pdfBytes;
            
        } catch (SQLException e) {
            System.err.println("ERROR SQL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error en base de datos: " + e.getMessage(), e);
        } catch (JRException e) {
            System.err.println("ERROR JasperReports: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("ERROR inesperado: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error inesperado: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
    }
    
    private byte[] generarPDFJasper(ReporteGananciasGlobalesCompletoDto reporte, 
                                   LocalDate fechaInicio, LocalDate fechaFin) throws JRException {
        
        try {
            // Obtener el classloader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            // Intentar cargar el archivo .jasper
            String[] rutasPosibles = {
                "reports/ReporteGananciasGlobales.jasper",
                "/reports/ReporteGananciasGlobales.jasper",
                "ReporteGananciasGlobales.jasper",
                "/ReporteGananciasGlobales.jasper"
            };
            
            InputStream reportStream = null;
            String rutaEncontrada = null;
            
            for (String ruta : rutasPosibles) {
                reportStream = classLoader.getResourceAsStream(ruta);
                if (reportStream != null) {
                    rutaEncontrada = ruta;
                    break;
                }
            }
            
            if (reportStream == null) {
                // Intentar cargar desde el sistema de archivos
                try {
                    String projectPath = System.getProperty("user.dir");
                    String filePath = projectPath + "/src/main/resources/reports/ReporteGananciasGlobales.jasper";
                    
                    java.io.File file = new java.io.File(filePath);
                    if (file.exists()) {
                        reportStream = new java.io.FileInputStream(file);
                        rutaEncontrada = filePath;
                    } else {
                        System.out.println("Archivo no existe en: " + filePath);
                    }
                } catch (Exception e) {
                    System.out.println("Error buscando archivo: " + e.getMessage());
                }
            }
            
            if (reportStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo ReporteGananciasGlobales.jasper. " +
                    "Verifica que esté en src/main/resources/reports/");
            }
            
            // Preparar parámetros
            Map<String, Object> parameters = new HashMap<>();
            
            if (fechaInicio != null && fechaFin != null) {
                parameters.put("fechaInicio", java.sql.Date.valueOf(fechaInicio));
                parameters.put("fechaFin", java.sql.Date.valueOf(fechaFin));
                parameters.put("periodoTexto", "Período: " + fechaInicio + " al " + fechaFin);
            } else {
                parameters.put("periodoTexto", "Período: Histórico Completo");
            }
            
            parameters.put("totalIngresoSistema", reporte.getTotalIngresoSistema());
            parameters.put("totalComisionPlataforma", reporte.getTotalComisionPlataforma());
            parameters.put("totalVentasEmpresas", reporte.getTotalVentasEmpresas());
            
            // Crear datasource
            JRDataSource dataSource = new JRBeanCollectionDataSource(reporte.getEmpresas());
            
            // Llenar reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
            
            // Exportar a PDF
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            
            byte[] pdfBytes = baos.toByteArray();
            
            // Cerrar stream
            reportStream.close();
            
            return pdfBytes;
            
        } catch (JRException e) {
            System.err.println("ERROR JasperReports en generarPDFJasper: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("ERROR en generarPDFJasper: " + e.getMessage());
            throw new JRException(e);
        }
    }
    
    // Método alternativo si JasperReports falla
    private byte[] generarPDFAlternativo(ReporteGananciasGlobalesCompletoDto reporte, 
                                        LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            
            // Crear un HTML simple y convertirlo (ejemplo básico)
            String html = "<html><head><title>Reporte Ganancias Globales</title></head><body>";
            html += "<h1>CENTRO UNIVERSITARIO DE OCCIDENTE (CUNOC)</h1>";
            html += "<h2>ESCUELA DE VACACIONES</h2>";
            html += "<h2>REPORTE GANANCIAS GLOBALES (SISTEMA)</h2>";
            
            if (fechaInicio != null && fechaFin != null) {
                html += "<p><strong>Período:</strong> " + fechaInicio + " al " + fechaFin + "</p>";
            } else {
                html += "<p><strong>Período:</strong> Histórico Completo</p>";
            }
            
            html += "<table border='1' style='width:100%; border-collapse: collapse;'>";
            html += "<tr><th>EMPRESA</th><th>VENTAS EMPRESA</th><th>COMISIÓN SISTEMA</th><th>TOTAL INGRESO</th></tr>";
            
            for (ReporteGananciasGlobalesDto empresa : reporte.getEmpresas()) {
                html += "<tr>";
                html += "<td>" + empresa.getEmpresa() + "</td>";
                html += "<td align='right'>Q" + String.format("%,.2f", empresa.getTotalVentasEmpresa()) + "</td>";
                html += "<td align='right'>Q" + String.format("%,.2f", empresa.getComisionPlataforma()) + "</td>";
                html += "<td align='right'>Q" + String.format("%,.2f", empresa.getTotalIngresoSistema()) + "</td>";
                html += "</tr>";
            }
            
            html += "</table><br><br>";
            html += "<h3>TOTALES GENERALES:</h3>";
            html += "<p><strong>Total Ventas a Empresas:</strong> Q" + String.format("%,.2f", reporte.getTotalVentasEmpresas()) + "</p>";
            html += "<p><strong>Total Comisión Plataforma:</strong> Q" + String.format("%,.2f", reporte.getTotalComisionPlataforma()) + "</p>";
            html += "<p><strong>TOTAL INGRESO SISTEMA:</strong> Q" + String.format("%,.2f", reporte.getTotalIngresoSistema()) + "</p>";
            html += "</body></html>";
            
            // Convertir HTML a bytes (esto es solo un ejemplo, en producción usarías una librería)
            return html.getBytes("UTF-8");
            
        } catch (Exception e) {
            System.err.println("ERROR en método alternativo: " + e.getMessage());
            return ("ERROR: " + e.getMessage()).getBytes();
        }
    }
    
    private List<ReporteGananciasGlobalesDto> obtenerDatosGanancias(Connection conn, 
        LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        // Mantén el mismo código que ya tienes
        List<ReporteGananciasGlobalesDto> empresas = new ArrayList<>();
        
        String sql = "SELECT " +
                     "e.nombre as empresa, " +
                     "COALESCE(SUM(c.monto_pago), 0) as total_ventas_empresa, " +
                     "COALESCE(SUM((c.monto_pago * c.comision_aplicada / 100)), 0) as comision_plataforma " +
                     "FROM empresa e " +
                     "LEFT JOIN videojuego v ON e.id_empresa = v.id_empresa " +
                     "LEFT JOIN compra c ON v.id_videojuego = c.id_videojuego ";
        
        if (fechaInicio != null && fechaFin != null) {
            sql += "WHERE c.fecha_compra BETWEEN ? AND ? ";
        } else {
            sql += "WHERE 1=1 ";
        }
        
        sql += "GROUP BY e.id_empresa, e.nombre " +
               "ORDER BY e.nombre";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (fechaInicio != null && fechaFin != null) {
                stmt.setDate(1, Date.valueOf(fechaInicio));
                stmt.setDate(2, Date.valueOf(fechaFin));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empresas.add(new ReporteGananciasGlobalesDto(
                        rs.getString("empresa"),
                        rs.getDouble("total_ventas_empresa"),
                        rs.getDouble("comision_plataforma")
                    ));
                }
            }
        }
        
        return empresas;
    }
    
    private void imprimirReporteConsola(ReporteGananciasGlobalesCompletoDto reporte, 
                                       LocalDate fechaInicio, LocalDate fechaFin) {
        // Mantén el mismo código de impresión
    }
}