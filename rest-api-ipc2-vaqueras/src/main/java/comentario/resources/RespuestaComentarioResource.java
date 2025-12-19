package comentario.resources;

import comentario.dto.RespuestaComentarioRequest;
import comentario.dto.RespuestaComentarioResponse;
import comentario.model.RespuestaComentario;
import comentario.services.RespuestaComentarioCrudService;
import exceptions.EntityNotFoundException;
import exceptions.RespuestaComentarioDataInvalidException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("respuestas-comentarios")
@Produces(MediaType.APPLICATION_JSON)
public class RespuestaComentarioResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRespuestaComentario(RespuestaComentarioRequest respuestaRequest) {
        try {
            RespuestaComentarioCrudService respuestaService = new RespuestaComentarioCrudService();
            RespuestaComentario respuestaCreada = 
                respuestaService.createRespuestaComentario(respuestaRequest);
            
            RespuestaComentarioResponse response = new RespuestaComentarioResponse(
                respuestaCreada.getId_respuesta(),
                respuestaCreada.getId_comentario_padre(),
                respuestaCreada.getId_usuario(),
                respuestaCreada.getComentario(),
                respuestaCreada.getFecha_hora()
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
                    
        } catch (RespuestaComentarioDataInvalidException e) {
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
    public Response getRespuestaById(@PathParam("id") int id) {
        try {
            RespuestaComentarioCrudService respuestaService = new RespuestaComentarioCrudService();
            RespuestaComentario respuesta = respuestaService.getRespuestaById(id);
            
            RespuestaComentarioResponse response = new RespuestaComentarioResponse(
                respuesta.getId_respuesta(),
                respuesta.getId_comentario_padre(),
                respuesta.getId_usuario(),
                respuesta.getComentario(),
                respuesta.getFecha_hora()
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
    @Path("comentario/{idComentarioPadre}")
    public Response getRespuestasPorComentarioPadre(@PathParam("idComentarioPadre") int idComentarioPadre) {
        try {
            if (idComentarioPadre <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del comentario padre debe ser mayor a 0\"}")
                        .build();
            }
            
            RespuestaComentarioCrudService respuestaService = new RespuestaComentarioCrudService();
            List<RespuestaComentarioResponse> respuestas = 
                respuestaService.getRespuestasPorComentarioPadre(idComentarioPadre)
                    .stream()
                    .map(r -> new RespuestaComentarioResponse(
                        r.getId_respuesta(),
                        r.getId_comentario_padre(),
                        r.getId_usuario(),
                        r.getComentario(),
                        r.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("usuario/{idUsuario}")
    public Response getRespuestasPorUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            if (idUsuario <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID de usuario debe ser mayor a 0\"}")
                        .build();
            }
            
            RespuestaComentarioCrudService respuestaService = new RespuestaComentarioCrudService();
            List<RespuestaComentarioResponse> respuestas = 
                respuestaService.getRespuestasPorUsuario(idUsuario)
                    .stream()
                    .map(r -> new RespuestaComentarioResponse(
                        r.getId_respuesta(),
                        r.getId_comentario_padre(),
                        r.getId_usuario(),
                        r.getComentario(),
                        r.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("videojuego/{idVideojuego}")
    public Response getRespuestasPorVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            if (idVideojuego <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del videojuego debe ser mayor a 0\"}")
                        .build();
            }
            
            RespuestaComentarioCrudService respuestaService = new RespuestaComentarioCrudService();
            List<RespuestaComentarioResponse> respuestas = 
                respuestaService.getRespuestasPorVideojuego(idVideojuego)
                    .stream()
                    .map(r -> new RespuestaComentarioResponse(
                        r.getId_respuesta(),
                        r.getId_comentario_padre(),
                        r.getId_usuario(),
                        r.getComentario(),
                        r.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(respuestas).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}