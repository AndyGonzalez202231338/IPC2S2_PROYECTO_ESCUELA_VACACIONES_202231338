package reportes.sistema.ventas.calidad;

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

public class ReporteTopVentasCalidadService {
    
    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin, String clasificacion, String categoria, int limite, boolean incluirCalificaciones) {
        Connection conn = null;
        
        try {
            
            conn = DBConnectionSingleton.getInstance().getConnection();
            
            List<VideojuegoCompletoDto> juegosCompletos = obtenerDatosCombinados(conn, fechaInicio, fechaFin, clasificacion, categoria, limite, incluirCalificaciones);
            
            imprimirReporteConsola(juegosCompletos, fechaInicio, fechaFin, limite, incluirCalificaciones);
            
            return generarPDFJasper(juegosCompletos, fechaInicio, fechaFin, limite, incluirCalificaciones);
            
        } catch (Exception e) {
            
            e.printStackTrace();
            throw new RuntimeException("Error generando reporte: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) {}
            }
        }
    }
    
    private List<VideojuegoCompletoDto> obtenerDatosCombinados(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, String clasificacion,
        String categoria, int limite, boolean incluirCalificaciones) throws SQLException {
        
        List<VideojuegoCompletoDto> juegosCompletos = new ArrayList<>();
        
        // Primero obtener ventas
        List<VideojuegoVentasDto> ventas = obtenerTopVentas(conn, fechaInicio, fechaFin, clasificacion, categoria, limite);
        
        List<VideojuegoCalificacionDto> calificaciones = new ArrayList<>();
        if (incluirCalificaciones) {
            calificaciones = obtenerTopCalificaciones(conn, fechaInicio, fechaFin, clasificacion, categoria, limite);
        }
        
        // Combinar datos
        for (VideojuegoVentasDto venta : ventas) {
            // Buscar calificación correspondiente
            Double calif = null;
            int totalCalif = 0;
            if (incluirCalificaciones) {
                for (VideojuegoCalificacionDto califDto : calificaciones) {
                    if (califDto.getTitulo().equals(venta.getTitulo())) {
                        calif = califDto.getCalificacion();
                        totalCalif = califDto.getTotalCalificaciones();
                        break;
                    }
                }
            }
            
            juegosCompletos.add(new VideojuegoCompletoDto(
                venta.getTitulo(),
                venta.getCantidadVentas(),
                venta.getTotalVentas(),
                calif,
                totalCalif,
                venta.getClasificacionEdad(),
                venta.getCategorias()
            ));
        }
        
        return juegosCompletos;
    }
    
    private List<VideojuegoVentasDto> obtenerTopVentas(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, String clasificacion, 
        String categoria, int limite) throws SQLException {
        
        List<VideojuegoVentasDto> ventas = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("v.titulo, ")
           .append("COUNT(c.id_compra) as cantidad_ventas, ")
           .append("SUM(c.monto_pago) as total_ventas, ")
           .append("v.clasificacion_edad, ")
           .append("GROUP_CONCAT(DISTINCT cat.nombre SEPARATOR ', ') as categorias ")
           .append("FROM videojuego v ")
           .append("JOIN compra c ON v.id_videojuego = c.id_videojuego ")
           .append("LEFT JOIN videojuego_categoria vc ON v.id_videojuego = vc.id_videojuego ")
           .append("LEFT JOIN categoria cat ON vc.id_categoria = cat.id_categoria ");
        
        List<String> condiciones = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            condiciones.add("c.fecha_compra BETWEEN ? AND ?");
            parametros.add(Date.valueOf(fechaInicio));
            parametros.add(Date.valueOf(fechaFin));
        }
        
        if (clasificacion != null && !clasificacion.isEmpty()) {
            condiciones.add("v.clasificacion_edad = ?");
            parametros.add(clasificacion);
        }
        
        if (categoria != null && !categoria.isEmpty()) {
            condiciones.add("cat.nombre = ?");
            parametros.add(categoria);
        }
        
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(" ");
        }
        
        sql.append("GROUP BY v.id_videojuego, v.titulo, v.clasificacion_edad ")
           .append("ORDER BY cantidad_ventas DESC, total_ventas DESC ")
           .append("LIMIT ?");
        
        parametros.add(limite);
        
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ventas.add(new VideojuegoVentasDto(
                        rs.getString("titulo"),
                        rs.getInt("cantidad_ventas"),
                        rs.getDouble("total_ventas"),
                        rs.getString("clasificacion_edad"),
                        rs.getString("categorias") != null ? rs.getString("categorias") : "Sin categorías"
                    ));
                }
            }
        }
        
        return ventas;
    }
    
    private List<VideojuegoCalificacionDto> obtenerTopCalificaciones(Connection conn, LocalDate fechaInicio, LocalDate fechaFin, String clasificacion, String categoria, int limite) throws SQLException {
        
        List<VideojuegoCalificacionDto> calificaciones = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("v.titulo, ")
           .append("AVG(cal.calificacion) as promedio_calificacion, ")
           .append("COUNT(cal.id_calificacion) as total_calificaciones, ")
           .append("v.clasificacion_edad, ")
           .append("GROUP_CONCAT(DISTINCT cat.nombre SEPARATOR ', ') as categorias ")
           .append("FROM videojuego v ")
           .append("LEFT JOIN biblioteca_usuario bu ON v.id_videojuego = bu.id_videojuego ")
           .append("LEFT JOIN calificacion cal ON bu.id_biblioteca = cal.id_biblioteca ")
           .append("LEFT JOIN videojuego_categoria vc ON v.id_videojuego = vc.id_videojuego ")
           .append("LEFT JOIN categoria cat ON vc.id_categoria = cat.id_categoria ");
        
        List<String> condiciones = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        
        condiciones.add("cal.calificacion IS NOT NULL");
        
        if (fechaInicio != null && fechaFin != null) {
            condiciones.add("cal.fecha_hora BETWEEN ? AND ?");
            parametros.add(Timestamp.valueOf(fechaInicio.atStartOfDay()));
            parametros.add(Timestamp.valueOf(fechaFin.atTime(23, 59, 59)));
        }
        
        if (clasificacion != null && !clasificacion.isEmpty()) {
            condiciones.add("v.clasificacion_edad = ?");
            parametros.add(clasificacion);
        }
        
        if (categoria != null && !categoria.isEmpty()) {
            condiciones.add("cat.nombre = ?");
            parametros.add(categoria);
        }
        
        if (!condiciones.isEmpty()) {
            sql.append("WHERE ").append(String.join(" AND ", condiciones)).append(" ");
        }
        
        sql.append("GROUP BY v.id_videojuego, v.titulo, v.clasificacion_edad ")
           .append("HAVING total_calificaciones > 0 ")
           .append("ORDER BY promedio_calificacion DESC, total_calificaciones DESC ")
           .append("LIMIT ?");
        
        parametros.add(limite);
        
        System.out.println("SQL Calificaciones: " + sql.toString());
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Double promedio = rs.getDouble("promedio_calificacion");
                    int total = rs.getInt("total_calificaciones");
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
                    
                    double calificacionFinal = Math.round(promedio * factor * 100.0) / 100.0;
                    
                    calificaciones.add(new VideojuegoCalificacionDto(
                        rs.getString("titulo"),
                        calificacionFinal,
                        total,
                        rs.getString("clasificacion_edad"),
                        rs.getString("categorias") != null ? rs.getString("categorias") : "Sin categorías"
                    ));
                }
            }
        }
        
        return calificaciones;
    }
    
    private byte[] generarPDFJasper(List<VideojuegoCompletoDto> juegosCompletos,
                                   LocalDate fechaInicio, LocalDate fechaFin,
                                   int limite, boolean incluirCalificaciones) throws JRException {
        
        try {
            // Cargar el archivo .jasper
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream reportStream = classLoader.getResourceAsStream("reports/ReporteTopVentasCalidad.jasper");
            
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo ReporteTopVentasCalidad.jasper");
            }
            
            // Preparar parámetros
            Map<String, Object> parameters = new HashMap<>();
            
            // Información del período
            if (fechaInicio != null && fechaFin != null) {
                parameters.put("periodoTexto", "Período: " + fechaInicio + " al " + fechaFin);
            } else {
                parameters.put("periodoTexto", "Período: Histórico Completo");
            }
            
            // Filtros aplicados
            String filtrosTexto = "";
            parameters.put("filtrosTexto", filtrosTexto.isEmpty() ? "Sin filtros aplicados" : filtrosTexto);
            
            parameters.put("limite", limite);
            parameters.put("incluirCalificaciones", incluirCalificaciones);
            parameters.put("totalJuegos", juegosCompletos.size());
            

            JRDataSource dataSource = new JRBeanCollectionDataSource(juegosCompletos);
            
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, dataSource);
            
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
            
            return baos.toByteArray();
            
        } catch (JRException e) {
            System.err.println("Error de JasperReports: " + e.getMessage());
            throw e;
        }
    }
    
    private void imprimirReporteConsola(List<VideojuegoCompletoDto> juegosCompletos,LocalDate fechaInicio, LocalDate fechaFin, int limite, boolean incluirCalificaciones) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("REPORTE TOP VENTAS Y CALIDAD");
        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("Límite: " + limite + " juegos");
        System.out.println("Incluir calificaciones: " + incluirCalificaciones);
        System.out.println("=".repeat(120));
        
        if (incluirCalificaciones) {
            System.out.printf("%-40s %-10s %-15s %-10s %-12s %-8s%n", 
                "TÍTULO", "VENTAS", "TOTAL (Q)", "CALIF.", "TOTAL CALIF.", "CLASIF.");
        } else {
            System.out.printf("%-40s %-10s %-15s %-8s%n", 
                "TÍTULO", "VENTAS", "TOTAL (Q)", "CLASIF.");
        }
        System.out.println("-".repeat(120));
        
        for (VideojuegoCompletoDto juego : juegosCompletos) {
            String tituloTruncado = juego.getTitulo().length() > 40 ? 
                juego.getTitulo().substring(0, 37) + "..." : juego.getTitulo();
            
            if (incluirCalificaciones) {
                String califStr = juego.getCalificacion() != null ? 
                    String.format("%.2f", juego.getCalificacion()) : "N/A";
                String totalCalifStr = juego.getTotalCalificaciones() > 0 ? 
                    String.valueOf(juego.getTotalCalificaciones()) : "0";
                
                System.out.printf("%-40s %-10d Q%-14.2f %-10s %-12s %-8s%n",
                    tituloTruncado,
                    juego.getCantidadVentas(),
                    juego.getTotalVentas(),
                    califStr,
                    totalCalifStr,
                    juego.getClasificacionEdad());
            } else {
                System.out.printf("%-40s %-10d Q%-14.2f %-8s%n",
                    tituloTruncado,
                    juego.getCantidadVentas(),
                    juego.getTotalVentas(),
                    juego.getClasificacionEdad());
            }
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.println("RESUMEN:");
        System.out.println("-".repeat(60));
        System.out.println("Total juegos listados: " + juegosCompletos.size());
        System.out.println("=".repeat(120));
    }
}