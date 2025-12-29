package reportes.sistema.ranking.usuarios;

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

public class ReporteRankingUsuariosService {
    
    public byte[] generarReportePDF(LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        Connection conn = null;
        
        try {
            conn = DBConnectionSingleton.getInstance().getConnection();
            
            List<UsuarioRankingDto> usuariosCombinados = obtenerDatosCombinados(conn, fechaInicio, fechaFin, limite);
            
            imprimirReporteConsola(usuariosCombinados, fechaInicio, fechaFin, limite);
            
            // Generar PDF con JasperReports
            return generarPDFJasper(usuariosCombinados, fechaInicio, fechaFin, limite);
            
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
    
    private List<UsuarioRankingDto> obtenerDatosCombinados(Connection conn, LocalDate fechaInicio, 
                                                      LocalDate fechaFin, int limite) throws SQLException {
    
    List<UsuarioRankingDto> usuarios = new ArrayList<>();
    
    // Agregar usuarios de compras
    List<UsuarioRankingDto> usuariosCompras = obtenerUsuariosCompras(conn, fechaInicio, fechaFin, limite);
    usuarios.addAll(usuariosCompras);
    
    
    List<UsuarioRankingDto> usuariosCalificaciones = obtenerUsuariosCalificaciones(conn, fechaInicio, fechaFin, limite);
    usuarios.addAll(usuariosCalificaciones);
    
    return usuarios;
}
    
    private List<UsuarioRankingDto> obtenerUsuariosCompras(Connection conn, LocalDate fechaInicio, 
                                                         LocalDate fechaFin, int limite) throws SQLException {
        
        List<UsuarioRankingDto> usuarios = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append("u.nombre as nombre_usuario, ")
           .append("COUNT(DISTINCT c.id_videojuego) as total_juegos_comprados, ")
           .append("SUM(c.monto_pago) as total_gastado, ")
           .append("COALESCE(u.pais, 'No especificado') as pais ")
           .append("FROM usuario u ")
           .append("JOIN compra c ON u.id_usuario = c.id_usuario ");
        
        List<Object> parametros = new ArrayList<>();
        
        if (fechaInicio != null && fechaFin != null) {
            sql.append("WHERE c.fecha_compra BETWEEN ? AND ? ");
            parametros.add(Date.valueOf(fechaInicio));
            parametros.add(Date.valueOf(fechaFin));
        }
        
        sql.append("GROUP BY u.id_usuario, u.nombre, u.pais ")
           .append("HAVING total_juegos_comprados > 0 ")
           .append("ORDER BY total_juegos_comprados DESC, total_gastado DESC ")
           .append("LIMIT ?");
        
        parametros.add(limite);
        
        
        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                stmt.setObject(i + 1, parametros.get(i));
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(new UsuarioRankingDto(
                        rs.getString("nombre_usuario"),
                        rs.getInt("total_juegos_comprados"),
                        rs.getDouble("total_gastado"),
                        rs.getString("pais")
                    ));
                }
            }
        }
        
        return usuarios;
    }
    
    private List<UsuarioRankingDto> obtenerUsuariosCalificaciones(Connection conn, LocalDate fechaInicio,
                                                         LocalDate fechaFin, int limite) throws SQLException {
    
    List<UsuarioRankingDto> usuarios = new ArrayList<>();
    
    StringBuilder sql = new StringBuilder();
    sql.append("SELECT ")
       .append("u.nombre as nombre_usuario, ")
       .append("COUNT(ca.id_calificacion) as total_calificaciones, ")  // Contar calificaciones
       .append("COALESCE(u.pais, 'No especificado') as pais ")
       .append("FROM usuario u ")
       .append("JOIN calificacion ca ON u.id_usuario = ca.id_usuario ")  // JOIN con calificacion
       .append("WHERE 1 = 1 ");
    
    List<Object> parametros = new ArrayList<>();
    
    if (fechaInicio != null && fechaFin != null) {
        sql.append("AND ca.fecha_hora BETWEEN ? AND ? ");  // Usar fecha_hora de calificacion
        parametros.add(Timestamp.valueOf(fechaInicio.atStartOfDay()));
        parametros.add(Timestamp.valueOf(fechaFin.atTime(23, 59, 59)));
    }
    
    sql.append("GROUP BY u.id_usuario, u.nombre, u.pais ")
       .append("HAVING total_calificaciones > 0 ")
       .append("ORDER BY total_calificaciones DESC ")
       .append("LIMIT ?");
    
    parametros.add(limite);
    
    System.out.println("SQL Usuarios Calificaciones: " + sql.toString());
    
    try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
        for (int i = 0; i < parametros.size(); i++) {
            stmt.setObject(i + 1, parametros.get(i));
        }
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                usuarios.add(new UsuarioRankingDto(
                    rs.getString("nombre_usuario"),
                    rs.getInt("total_calificaciones"),
                    rs.getString("pais")
                ));
            }
        }
    }
    
    System.out.println("Usuarios calificaciones obtenidos: " + usuarios.size());
    return usuarios;
}
    
private byte[] generarPDFJasper(List<UsuarioRankingDto> usuarios,
                               LocalDate fechaInicio, LocalDate fechaFin,
                               int limite) throws JRException {
    
    try {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream reportStream = classLoader.getResourceAsStream("reports/ReporteRankingUsuarios.jasper");
        
        if (reportStream == null) {
            throw new RuntimeException("No se encontró el archivo ReporteRankingUsuarios.jasper");
        }
        
        // Separar las listas
        List<UsuarioRankingDto> usuariosCompras = new ArrayList<>();
        List<UsuarioRankingDto> usuariosCalificaciones = new ArrayList<>();
        
        for (UsuarioRankingDto usuario : usuarios) {
            if ("COMPRAS".equals(usuario.getTipo())) {
                usuariosCompras.add(usuario);
            } else {
                usuariosCalificaciones.add(usuario);
            }
        }
        
        Map<String, Object> parameters = new HashMap<>();
        
        // Información del período
        if (fechaInicio != null && fechaFin != null) {
            parameters.put("periodoTexto", "Período: " + fechaInicio + " al " + fechaFin);
        } else {
            parameters.put("periodoTexto", "Período: Histórico Completo");
        }
        
        parameters.put("limite", limite);
        parameters.put("totalCompras", usuariosCompras.size());
        parameters.put("totalCalificaciones", usuariosCalificaciones.size());
        
        // Crear DataSources
        parameters.put("usuariosComprasDataSource", new JRBeanCollectionDataSource(usuariosCompras));
        parameters.put("usuariosCalificacionesDataSource", new JRBeanCollectionDataSource(usuariosCalificaciones));
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, new JREmptyDataSource());
        
        // Exportar a PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, baos);
        
        return baos.toByteArray();
        
    } catch (JRException e) {
        System.err.println("Error de JasperReports: " + e.getMessage());
        throw e;
    }
}
    
    private void imprimirReporteConsola(List<UsuarioRankingDto> usuarios, LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        System.out.println("\n" + "=".repeat(120));
        System.out.println("REPORTE RANKING DE USUARIOS");
        if (fechaInicio != null && fechaFin != null) {
            System.out.println("Período: " + fechaInicio + " hasta " + fechaFin);
        } else {
            System.out.println("Período: Histórico Completo");
        }
        System.out.println("Límite: " + limite + " usuarios por categoría");
        System.out.println("=".repeat(120));
        
        List<UsuarioRankingDto> compras = new ArrayList<>();
        List<UsuarioRankingDto> resenas = new ArrayList<>();
        
        for (UsuarioRankingDto usuario : usuarios) {
            if ("COMPRAS".equals(usuario.getTipo())) {
                compras.add(usuario);
            } else {
                resenas.add(usuario);
            }
        }
        
        if (!compras.isEmpty()) {
            System.out.println("\nTOP " + limite + " USUARIOS CON MÁS JUEGOS COMPRADOS:");
            System.out.println("-".repeat(90));
            System.out.printf("%-30s %-20s %-20s %-20s%n", 
                "USUARIO", "JUEGOS COMPRADOS", "TOTAL GASTADO (Q)", "PAÍS");
            System.out.println("-".repeat(90));
            
            for (UsuarioRankingDto usuario : compras) {
                System.out.printf("%-30s %-20d Q%-19.2f %-20s%n",
                    usuario.getNombreUsuario().length() > 30 ? 
                        usuario.getNombreUsuario().substring(0, 27) + "..." : usuario.getNombreUsuario(),
                    usuario.getTotalJuegosComprados(),
                    usuario.getTotalGastado(),
                    usuario.getPais() != null && usuario.getPais().length() > 20 ? 
                        usuario.getPais().substring(0, 17) + "..." : usuario.getPais());
            }
        }
        if (!resenas.isEmpty()) {
            System.out.println("\n\nTOP " + limite + " USUARIOS CON MÁS RESEÑAS ESCRITAS:");
            System.out.println("-".repeat(70));
            System.out.printf("%-30s %-20s %-20s%n", 
                "USUARIO", "RESEÑAS ESCRITAS", "PAÍS");
            System.out.println("-".repeat(70));
            
            for (UsuarioRankingDto usuario : resenas) {
                System.out.printf("%-30s %-20d %-20s%n",
                    usuario.getNombreUsuario().length() > 30 ? 
                        usuario.getNombreUsuario().substring(0, 27) + "..." : usuario.getNombreUsuario(),
                    usuario.getTotalCalificaciones(),
                    usuario.getPais() != null && usuario.getPais().length() > 20 ? 
                        usuario.getPais().substring(0, 17) + "..." : usuario.getPais());
            }
        }
        
        System.out.println("\n" + "=".repeat(120));
        System.out.println("RESUMEN:");
        System.out.println("-".repeat(60));
        System.out.println("Total usuarios en compras: " + compras.size());
        System.out.println("Total usuarios en reseñas: " + resenas.size());
        System.out.println("=".repeat(120));
    }
}