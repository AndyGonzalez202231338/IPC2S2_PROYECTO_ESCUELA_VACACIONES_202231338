package reportes.sistema.ingresos.empresa;

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

public class ReporteIngresosEmpresasService {
    
    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin) {
        Connection conn = null;
        
        try {
            
            conn = DBConnectionSingleton.getInstance().getConnection();
            

            List<IngresoEmpresaDto> empresas = obtenerDatosIngresos(conn, fechaInicio, fechaFin);
            ReporteIngresosEmpresasDto reporteCompleto = new ReporteIngresosEmpresasDto(empresas);
            
            imprimirReporteConsola(reporteCompleto, fechaInicio, fechaFin);
            
            return generarPDFJasper(reporteCompleto, fechaInicio, fechaFin);
            
        } catch (Exception e) {
            System.err.println("ERROR generando reporte: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error generando reporte: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
    
    private List<IngresoEmpresaDto> obtenerDatosIngresos(Connection conn, LocalDate fechaInicio, 
                                                        LocalDate fechaFin) throws SQLException {
        
        List<IngresoEmpresaDto> empresas = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("e.nombre as empresa, ")
           .append("COALESCE(SUM(c.monto_pago), 0) as total_ventas, ")
           .append("COALESCE(SUM((c.monto_pago * c.comision_aplicada / 100)), 0) as total_comision, ")
           .append("COUNT(c.id_compra) as total_transacciones ")
           .append("FROM empresa e ")
           .append("LEFT JOIN videojuego v ON e.id_empresa = v.id_empresa ")
           .append("LEFT JOIN compra c ON v.id_videojuego = c.id_videojuego ");
        
        // Construir condiciones WHERE
        List<String> condiciones = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            condiciones.add("c.fecha_compra BETWEEN ? AND ?");
            parametros.add(Date.valueOf(fechaInicio));
            parametros.add(Date.valueOf(fechaFin));
        }
        
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(" ");
        }
        
        sql.append("GROUP BY e.id_empresa, e.nombre ")
           .append("HAVING total_ventas > 0 OR total_comision > 0 ")
           .append("ORDER BY total_ventas DESC, e.nombre");
        
        System.out.println("SQL Ingresos Empresas: " + sql.toString());
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    empresas.add(new IngresoEmpresaDto(
                        rs.getString("empresa"),
                        rs.getDouble("total_ventas"),
                        rs.getDouble("total_comision"),
                        rs.getInt("total_transacciones")
                    ));
                }
            }
        }
        
        System.out.println("Empresas obtenidas: " + empresas.size());
        return empresas;
    }
    
    private byte[] generarPDFJasper(ReporteIngresosEmpresasDto reporte,
                                   LocalDate fechaInicio, LocalDate fechaFin) throws JRException {
        
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream reportStream = classLoader.getResourceAsStream("reports/ReporteIngresosEmpresas.jasper");
            
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo ReporteIngresosEmpresas.jasper");
            }
            
            // Preparar parámetros
            Map<String, Object> parameters = new HashMap<>();
            
            // Información del período
            if (fechaInicio != null && fechaFin != null) {
                parameters.put("periodoTexto", "Período: " + fechaInicio + " al " + fechaFin);
            } else {
                parameters.put("periodoTexto", "Período: Histórico Completo");
            }
            
            parameters.put("totalEmpresas", reporte.getEmpresas().size());
            parameters.put("totalTransacciones", reporte.getTotalTransacciones());
            parameters.put("totalVentasTodasEmpresas", reporte.getTotalVentasTodasEmpresas());
            parameters.put("totalComisionTodasEmpresas", reporte.getTotalComisionTodasEmpresas());
            parameters.put("totalIngresoSistema", reporte.getTotalIngresoSistema());
            

            JRDataSource dataSource = new JRBeanCollectionDataSource(reporte.getEmpresas());
            
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
    
    private void imprimirReporteConsola(ReporteIngresosEmpresasDto reporte,
                                       LocalDate fechaInicio, LocalDate fechaFin) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("REPORTE INGRESOS POR EMPRESA");
        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("=".repeat(120));
        
        System.out.printf("%-30s %-12s %-20s %-20s %-20s%n", 
            "EMPRESA", "TRANSACC.", "TOTAL VENTAS", "COMISIÓN SISTEMA", "TOTAL INGRESO");
        System.out.println("-".repeat(110));
        
        for (IngresoEmpresaDto empresa : reporte.getEmpresas()) {
            System.out.printf("%-30s %-12d Q%-19.2f Q%-19.2f Q%-19.2f%n",
                empresa.getEmpresa().length() > 30 ? 
                    empresa.getEmpresa().substring(0, 27) + "..." : empresa.getEmpresa(),
                empresa.getTotalTransacciones(),
                empresa.getTotalVentas(),
                empresa.getTotalComision(),
                empresa.getTotalIngresoSistema());
        }
        
        System.out.println("-".repeat(110));
        
        System.out.println("\nTOTALES GENERALES:");
        System.out.println("-".repeat(70));
        System.out.printf("%-40s: %d%n", "Total Empresas", reporte.getEmpresas().size());
        System.out.printf("%-40s: %d%n", "Total Transacciones", reporte.getTotalTransacciones());
        System.out.printf("%-40s: Q%,.2f%n", "Total Ventas Todas Empresas", reporte.getTotalVentasTodasEmpresas());
        System.out.printf("%-40s: Q%,.2f%n", "Total Comisión Sistema", reporte.getTotalComisionTodasEmpresas());
        System.out.printf("%-40s: Q%,.2f%n", "TOTAL INGRESO SISTEMA", reporte.getTotalIngresoSistema());
        
        System.out.println("=".repeat(120));
    }
}