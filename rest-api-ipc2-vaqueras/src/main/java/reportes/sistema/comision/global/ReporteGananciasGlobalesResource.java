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
            if ((fechaInicio != null && fechaFin == null)
                    || (fechaInicio == null && fechaFin != null)) {
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
            String filename = "reporte_ganancias_globales_"
                    + (fechaInicio != null ? fechaInicio.toString() + "_" + fechaFin.toString() : "historico")
                    + ".pdf";

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

    @GET
    @Path("ingresos-empresas-pdf")
    @Produces("application/pdf")
    public Response generarReporteIngresosEmpresasPDF(
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
            if ((fechaInicio != null && fechaFin == null)
                    || (fechaInicio == null && fechaFin != null)) {
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

            reportes.sistema.ingresos.empresa.ReporteIngresosEmpresasService service
                    = new reportes.sistema.ingresos.empresa.ReporteIngresosEmpresasService();

            byte[] pdfBytes = service.generarReportePDF(fechaInicio, fechaFin);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("El PDF generado está vacío")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            // Crear nombre del archivo
            String filename = "reporte_ingresos_empresas_";
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
                    .entity("Error generando reporte de ingresos por empresa: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

    @GET
    @Path("ranking-usuarios-pdf")
    @Produces("application/pdf")
    public Response generarReporteRankingUsuariosPDF(
            @QueryParam("fechaInicio") String fechaInicioStr,
            @QueryParam("fechaFin") String fechaFinStr,
            @QueryParam("limite") @DefaultValue("10") int limite) {

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
            if ((fechaInicio != null && fechaFin == null)
                    || (fechaInicio == null && fechaFin != null)) {
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
                limite = 10; // Valor por defecto
            }

            reportes.sistema.ranking.usuarios.ReporteRankingUsuariosService service
                    = new reportes.sistema.ranking.usuarios.ReporteRankingUsuariosService();

            byte[] pdfBytes = service.generarReportePDF(fechaInicio, fechaFin, limite);

            if (pdfBytes == null || pdfBytes.length == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("El PDF generado está vacío")
                        .type(MediaType.TEXT_PLAIN)
                        .build();
            }

            // Crear nombre del archivo
            String filename = "reporte_ranking_usuarios_";
            if (fechaInicio != null && fechaFin != null) {
                filename += fechaInicio.toString() + "_" + fechaFin.toString();
            } else {
                filename += "historico";
            }
            filename += "_limite_" + limite + ".pdf";

            return Response.ok(pdfBytes)
                    .type("application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Length", pdfBytes.length)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error generando reporte de ranking de usuarios: " + e.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
    }

}
