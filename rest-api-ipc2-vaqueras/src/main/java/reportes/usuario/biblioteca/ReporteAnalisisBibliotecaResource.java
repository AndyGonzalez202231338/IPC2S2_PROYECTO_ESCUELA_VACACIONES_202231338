/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.biblioteca;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;

/**
 *
 * @author andy
 */
@Path("reports/analisis-biblioteca")
public class ReporteAnalisisBibliotecaResource {
    
    @GET
    @Path("pdf")
    @Produces("application/pdf")
    public Response generarPDF(
            @QueryParam("idUsuario") int idUsuario,
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr) {
        
        if (idUsuario <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("ID de usuario inválido")
                    .build();
        }
        
        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;
        
        try {
            if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
                fechaInicio = LocalDate.parse(fechaInicioStr);
            }
            if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
                fechaFin = LocalDate.parse(fechaFinStr);
            }
            
            // Validar que fechaFin no sea anterior a fechaInicio
            if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("La fecha fin no puede ser anterior a la fecha inicio")
                        .build();
            }
            
            ReporteAnalisisBibliotecaService service = new ReporteAnalisisBibliotecaService();
            byte[] pdf = service.generarReportePDF(idUsuario, fechaInicio, fechaFin);
            
            String filename = "analisis_biblioteca_usuario_" + idUsuario;
            if (fechaInicio != null && fechaFin != null) {
                filename += "_" + fechaInicio + "_" + fechaFin;
            }
            filename += ".pdf";
            
            return Response.ok(pdf)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando reporte: " + e.getMessage())
                    .build();
        }
    }
}

