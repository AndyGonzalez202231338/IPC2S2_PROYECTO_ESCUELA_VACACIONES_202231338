/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.comision.global;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.time.LocalDate;
/**
 *
 * @author andy
 */
@Path("reports")
public class ReporteGananciasGlobalesResource {
    
    @GET
    @Path("ganancias-globales-pdf")
    @Produces("application/pdf")
    public Response generarReportePDF(
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
                    .build();
            }
            
            // Validar que fecha inicio no sea después de fecha fin
            if (fechaInicio != null && fechaFin != null && fechaInicio.isAfter(fechaFin)) {
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Fecha inicio no puede ser después de fecha fin")
                    .build();
            }
            
            ReporteGananciasGlobalesService service = new ReporteGananciasGlobalesService();
            byte[] pdfBytes = service.generarReportePDF(fechaInicio, fechaFin);
            
            // Validar que se generaron bytes
            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("El PDF generado está vacío")
                    .build();
            }
            
            // Crear respuesta con el PDF
            String filename = "reporte_ganancias_globales_" + 
                (fechaInicio != null ? fechaInicio.toString() + "_" + fechaFin.toString() : "historico") + 
                ".pdf";
            
            return Response.ok(pdfBytes)
                .type("application/pdf")
                .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                .header("Content-Length", pdfBytes.length)
                .build();
            
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en el servidor
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("Error generando reporte: " + e.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
        }
    }
    
}
