package comentario.resources;


import comentario.dto.CalificacionRequest;
import comentario.dto.CalificacionResponse;
import comentario.model.Calificacion;
import comentario.services.CalificacionCrudService;
import exceptions.CalificacionDataInvalidException;
import exceptions.EntityNotFoundException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("calificaciones")
@Produces(MediaType.APPLICATION_JSON)
public class CalificacionResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCalificacion(CalificacionRequest calificacionRequest) {
        try {
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            Calificacion calificacionCreada = calificacionService.createCalificacion(calificacionRequest);
            
            CalificacionResponse response = new CalificacionResponse(
                calificacionCreada.getId_calificacion(),
                calificacionCreada.getId_usuario(),
                calificacionCreada.getId_biblioteca(),
                calificacionCreada.getCalificacion(),
                calificacionCreada.getFecha_hora()
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
                    
        } catch (CalificacionDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("{id}")
    public Response getCalificacionById(@PathParam("id") int id) {
        try {
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            Calificacion calificacion = calificacionService.getCalificacionById(id);
            
            CalificacionResponse response = new CalificacionResponse(
                calificacion.getId_calificacion(),
                calificacion.getId_usuario(),
                calificacion.getId_biblioteca(),
                calificacion.getCalificacion(),
                calificacion.getFecha_hora()
            );
            
            return Response.ok(response).build();
                    
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
        @GET
    @Path("videojuego/{idVideojuego}")
    public Response getCalificacionesPorVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            if (idVideojuego <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del videojuego debe ser mayor a 0\"}")
                        .build();
            }
            
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            List<CalificacionResponse> calificaciones = calificacionService.getCalificacionesPorVideojuego(idVideojuego)
                    .stream()
                    .map(c -> new CalificacionResponse(
                        c.getId_calificacion(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getCalificacion(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(calificaciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("usuario/{idUsuario}/biblioteca/{idBiblioteca}")
    public Response getCalificacionPorUsuarioYBiblioteca(
            @PathParam("idUsuario") int idUsuario,
            @PathParam("idBiblioteca") int idBiblioteca) {
        try {
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            Calificacion calificacion = calificacionService.getCalificacionPorUsuarioYBiblioteca(idUsuario, idBiblioteca);
            
            CalificacionResponse response = new CalificacionResponse(
                calificacion.getId_calificacion(),
                calificacion.getId_usuario(),
                calificacion.getId_biblioteca(),
                calificacion.getCalificacion(),
                calificacion.getFecha_hora()
            );
            
            return Response.ok(response).build();
                    
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("biblioteca/{idBiblioteca}")
    public Response getCalificacionesPorBiblioteca(@PathParam("idBiblioteca") int idBiblioteca) {
        try {
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            List<CalificacionResponse> calificaciones = calificacionService.getCalificacionesPorBiblioteca(idBiblioteca)
                    .stream()
                    .map(c -> new CalificacionResponse(
                        c.getId_calificacion(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getCalificacion(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(calificaciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    

    @GET
    @Path("verificar")
    public Response verificarUsuarioYaCalifico(
            @QueryParam("idUsuario") int idUsuario,
            @QueryParam("idBiblioteca") int idBiblioteca) {
        try {
            if (idUsuario <= 0 || idBiblioteca <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"IDs de usuario y biblioteca deben ser mayores a 0\"}")
                        .build();
            }
            
            CalificacionCrudService calificacionService = new CalificacionCrudService();
            boolean yaCalifico = calificacionService.usuarioYaCalificoBiblioteca(idUsuario, idBiblioteca);
            
            return Response.ok()
                    .entity("{\"yaCalifico\": " + yaCalifico + 
                            ", \"idUsuario\": " + idUsuario + 
                            ", \"idBiblioteca\": " + idBiblioteca + "}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
}