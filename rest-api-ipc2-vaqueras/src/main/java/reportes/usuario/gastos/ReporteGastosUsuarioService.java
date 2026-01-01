/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.gastos;

import conexion.DBConnectionSingleton;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author andy
 */
public class ReporteGastosUsuarioService {

    public byte[] generarReportePDF(int idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {

            String nombreUsuario = obtenerNombreUsuario(conn, idUsuario);
            List<CompraUsuarioDto> compras = obtenerCompras(conn, idUsuario, fechaInicio, fechaFin);

            double totalGastado = compras.stream()
                    .mapToDouble(CompraUsuarioDto::getMontoPagado)
                    .sum();

            InputStream reportStream = getClass().getClassLoader()
                    .getResourceAsStream("reports/ReporteGastosUsuario.jasper");

            if (reportStream == null) {
                throw new RuntimeException("No se encontró ReporteGastosUsuario.jasper");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("nombreUsuario", nombreUsuario);
            params.put("totalGastado", String.format("%.2f", totalGastado));
            
            if (fechaInicio != null && fechaFin != null) {
                params.put("periodoTexto",
                        "Del " + fechaInicio + " al " + fechaFin);
            } else {
                params.put("periodoTexto", "Histórico completo");
            }

            // IMPORTANTE: Pasar el datasource real, no usar JREmptyDataSource
            JRBeanCollectionDataSource comprasDataSource = new JRBeanCollectionDataSource(compras);
            
            // Llenar el reporte con el datasource correcto
            JasperPrint print = JasperFillManager.fillReport(
                    reportStream,
                    params,
                    comprasDataSource  // ¡Esto es lo importante!
            );

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(print, baos);

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte de gastos", e);
        }
    }

    private String obtenerNombreUsuario(Connection conn, int idUsuario) throws SQLException {
        String sql = "SELECT nombre FROM usuario WHERE id_usuario = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        }
        return "Usuario ID " + idUsuario;
    }

    private List<CompraUsuarioDto> obtenerCompras(
            Connection conn,
            int idUsuario,
            LocalDate fechaInicio,
            LocalDate fechaFin) throws SQLException {

        List<CompraUsuarioDto> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("""
            SELECT c.fecha_compra, v.titulo, c.monto_pago
            FROM compra c
            JOIN videojuego v ON c.id_videojuego = v.id_videojuego
            WHERE c.id_usuario = ?
        """);

        if (fechaInicio != null && fechaFin != null) {
            sql.append(" AND c.fecha_compra BETWEEN ? AND ?");
        }

        sql.append(" ORDER BY c.fecha_compra DESC");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, idUsuario);

            if (fechaInicio != null && fechaFin != null) {
                ps.setDate(2, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(3, java.sql.Date.valueOf(fechaFin));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new CompraUsuarioDto(
                        rs.getDate("fecha_compra").toLocalDate(),
                        rs.getString("titulo"),
                        rs.getDouble("monto_pago")
                ));
            }
        }

        return lista;
    }
}
