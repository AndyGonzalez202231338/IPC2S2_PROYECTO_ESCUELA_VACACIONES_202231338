package reportes.sistema.ventas.calidad;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

@Path("reportsventas")
public class ReporteTopVentasCalidadResource {
    
    @GET
    @Path("top-ventas-calidad-pdf")
    @Produces("application/pdf")
    public Response generarReportePDF(
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr,
            @QueryParam("clasificacion") String clasificacion,
            @QueryParam("categoria") String categoria,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @QueryParam("incluirCalificaciones") @DefaultValue("true") boolean incluirCalificaciones) {
        
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
            
            // Validar fechas
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
            
            // Validar límite
            if (limite <= 0 || limite > 50) {
                limite = 10; // Valor por defecto para cantidad de en el top
            }
            
            ReporteTopVentasCalidadService service = new ReporteTopVentasCalidadService();
            byte[] pdfBytes = service.generarReportePDF(fechaInicio, fechaFin, clasificacion, 
                                                       categoria, limite, incluirCalificaciones);
            
            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("El PDF generado está vacío")
                    .type(MediaType.TEXT_PLAIN)
                    .build();
            }
            
            // Crear nombre del archivo
            String filename = "reporte_top_ventas_calidad_";
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
                .entity("Error generando reporte: " + e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        }
    }
}