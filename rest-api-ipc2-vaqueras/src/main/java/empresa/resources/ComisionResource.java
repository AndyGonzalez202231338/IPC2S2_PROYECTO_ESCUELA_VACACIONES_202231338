/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.resources;

import empresa.dtos.NuevaComisionRequest;
import empresa.models.Comision;
import empresa.services.ComisionService;
import exceptions.EntityNotFoundException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *
 * @author andy
 */
@Path("empresas/{idEmpresa}/comisiones")
@Produces(MediaType.APPLICATION_JSON)
public class ComisionResource {

    @GET
    @Path("actual")
    public Response getComisionActual(@PathParam("idEmpresa") int idEmpresa) {
        try {
            ComisionService comisionService = new ComisionService();
            Comision comision = comisionService.getComisionActualEmpresa(idEmpresa);

            // Convertir a JSON
            String json = String.format(
                    "{\"id_comision\": %d, \"id_empresa\": %d, \"porcentaje\": %.2f, "
                    + "\"fecha_inicio\": \"%s\", \"fecha_final\": %s}",
                    comision.getId_comision(),
                    comision.getId_empresa(),
                    comision.getPorcentaje(),
                    new SimpleDateFormat("yyyy-MM-dd").format(comision.getFecha_inicio()),
                    comision.getFecha_final() != null
                    ? "\"" + new SimpleDateFormat("yyyy-MM-dd").format(comision.getFecha_final()) + "\""
                    : "null"
            );

            return Response.ok(json).build();

        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    public Response getTodasComisiones(@PathParam("idEmpresa") int idEmpresa) {
        try {
            ComisionService comisionService = new ComisionService();
            List<Comision> comisiones = comisionService.getTodasComisionesEmpresa(idEmpresa);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            StringBuilder json = new StringBuilder("[");

            for (int i = 0; i < comisiones.size(); i++) {
                Comision c = comisiones.get(i);
                json.append(String.format(
                        "{\"id_comision\": %d, \"porcentaje\": %.2f, "
                        + "\"fecha_inicio\": \"%s\", \"fecha_final\": %s}",
                        c.getId_comision(),
                        c.getPorcentaje(),
                        sdf.format(c.getFecha_inicio()),
                        c.getFecha_final() != null ? "\"" + sdf.format(c.getFecha_final()) + "\"" : "null"
                
                ));

                if (i < comisiones.size() - 1) {
                    json.append(",");
                }
            }

            json.append("]");

            return Response.ok(json.toString()).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @POST
@Consumes(MediaType.APPLICATION_JSON)
public Response crearComision(@PathParam("idEmpresa") int idEmpresa, NuevaComisionRequest request) {
    try {
        // Obtener el porcentaje global actual
        sistema.services.SistemaCrudService sistemaService = new sistema.services.SistemaCrudService();
        double porcentajeGlobal = sistemaService.getComisionGlobal();
        
        // Si no viene fecha_inicio, usar fecha actual
        java.util.Date fechaInicio = request.getFecha_inicio();
        if (fechaInicio == null) {
            fechaInicio = new java.util.Date(); // Fecha actual
        }
        
        // Convertir java.util.Date a java.sql.Date
        java.sql.Date fechaInicioSql = new java.sql.Date(fechaInicio.getTime());
        java.sql.Date fechaFinalSql = null;
        if (request.getFecha_final() != null) {
            fechaFinalSql = new java.sql.Date(request.getFecha_final().getTime());
        }
        
        // Crear comisión específica
        ComisionService comisionService = new ComisionService();
        Comision nuevaComision = comisionService.crearComisionEspecifica(
            idEmpresa,
            request.getPorcentaje(),
            fechaInicioSql,
            fechaFinalSql,
            porcentajeGlobal
        );
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String json = String.format(
            "{\"message\": \"Comisión creada exitosamente\", " +
            "\"id_comision\": %d, \"porcentaje\": %.2f, " +
            "\"tipo_comision\": \"%s\", " +
            "\"fecha_inicio\": \"%s\", " +
            "\"fecha_final\": %s}",
            nuevaComision.getId_comision(),
            nuevaComision.getPorcentaje(),
            nuevaComision.getTipo_comision(),
            sdf.format(nuevaComision.getFecha_inicio()),
            nuevaComision.getFecha_final() != null ? 
                "\"" + sdf.format(nuevaComision.getFecha_final()) + "\"" : "null"
        );
        
        return Response.status(Response.Status.CREATED)
                .entity(json)
                .build();
                
    } catch (Exception e) {
        e.printStackTrace();
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\": \"" + e.getMessage() + "\"}")
                .build();
    }
}
}
