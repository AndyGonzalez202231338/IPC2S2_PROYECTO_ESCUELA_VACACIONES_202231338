/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.gastos;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.time.LocalDate;
/**
 *
 * @author andy
 */
@Path("reports/gastos-usuario")
public class ReporteGastosUsuarioResource {

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

        if (fechaInicioStr != null && fechaFinStr != null) {
            fechaInicio = LocalDate.parse(fechaInicioStr);
            fechaFin = LocalDate.parse(fechaFinStr);
        }

        ReporteGastosUsuarioService service = new ReporteGastosUsuarioService();
        byte[] pdf = service.generarReportePDF(idUsuario, fechaInicio, fechaFin);

        return Response.ok(pdf)
                .header("Content-Disposition",
                        "attachment; filename=\"reporte_gastos_usuario_" + idUsuario + ".pdf\"")
                .build();
    }
}
