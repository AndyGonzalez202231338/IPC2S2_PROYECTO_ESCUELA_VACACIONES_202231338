package reportes.empresa.ventas.propia;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

@Path("reports/ventas-propias")
public class ReporteVentasPropiasResource {
    
    @GET
    @Path("pdf")
    @Produces("application/pdf")
    public Response generarReportePDF(
            @QueryParam("idEmpresa") int idEmpresa,
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr) {
        
        try {
            LocalDate fechaInicio = null;
            LocalDate fechaFin = null;
            
            // Parsear fechas si están presentes
            if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                fechaInicio = LocalDate.parse(fechaInicioStr);
            }
            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                fechaFin = LocalDate.parse(fechaFinStr);
            }
            
            // Validar que si una fecha está presente, ambas deben estarlo
            if ((fechaInicio != null && fechaFin == null) || 
                (fechaInicio == null && fechaFin != null)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe especificar ambas fechas o ninguna")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            // Validar que fecha inicio no sea después de fecha fin
            if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Fecha inicio no puede ser después de fecha fin")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            // Validar que idEmpresa sea válido
            if (idEmpresa <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID de empresa inválido")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            ReporteVentasPropiasService service = new ReporteVentasPropiasService();
            byte[] pdfBytes = service.generarReportePDF(fechaInicio, fechaFin, idEmpresa);
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("El PDF generado está vacío")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            String filename = "reporte_ventas_propias_empresa_" + idEmpresa + "_";
            if (fechaInicio != null && fechaFin != null) {
                filename += fechaInicio.toString() + "_" + fechaFin.toString();
            } else {
                filename += "historico";
            }
            filename += ".pdf";
            
            return Response.ok(pdfBytes)
                .type("application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Length", pdfBytes.length)
                .build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error generando reporte de ventas propias: " + e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        }
    }
    
    @GET
    @Path("datos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerDatosReporte(
            @QueryParam("idEmpresa") int idEmpresa,
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr) {
        
        try {
            LocalDate fechaInicio = null;
            LocalDate fechaFin = null;
            
            if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                fechaInicio = LocalDate.parse(fechaInicioStr);
            }
            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                fechaFin = LocalDate.parse(fechaFinStr);
            }
            
            if ((fechaInicio != null && fechaFin == null) || 
                (fechaInicio == null && fechaFin != null)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe especificar ambas fechas o ninguna")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Fecha inicio no puede ser después de fecha fin")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            if (idEmpresa <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID de empresa inválido")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            ReporteVentasPropiasService service = new ReporteVentasPropiasService();
            Object datos = service.obtenerDatosReporte(fechaInicio, fechaFin, idEmpresa);
            
            return Response.ok(datos)
                .type(MediaType.APPLICATION_JSON)
                .build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error obteniendo datos del reporte: " + e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        }
    }
}