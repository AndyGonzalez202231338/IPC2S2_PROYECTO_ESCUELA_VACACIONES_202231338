package comentario.resources;

import comentario.dto.ComentarioRequest;
import comentario.dto.ComentarioResponse;
import comentario.model.Comentario;
import comentario.services.ComentarioCrudService;
import exceptions.ComentarioDataInvalidException;
import exceptions.EntityNotFoundException;
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

@Path("comentarios")
@Produces(MediaType.APPLICATION_JSON)
public class ComentarioResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createComentario(ComentarioRequest comentarioRequest) {
        try {
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            Comentario comentarioCreado = comentarioService.createComentario(comentarioRequest);
            
            ComentarioResponse response = new ComentarioResponse(
                comentarioCreado.getId_comentario(),
                comentarioCreado.getId_usuario(),
                comentarioCreado.getId_biblioteca(),
                comentarioCreado.getComentario(),
                comentarioCreado.getFecha_hora()
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
                    
        } catch (ComentarioDataInvalidException e) {
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
    public Response getComentarioById(@PathParam("id") int id) {
        try {
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            Comentario comentario = comentarioService.getComentarioById(id);
            
            ComentarioResponse response = new ComentarioResponse(
                comentario.getId_comentario(),
                comentario.getId_usuario(),
                comentario.getId_biblioteca(),
                comentario.getComentario(),
                comentario.getFecha_hora()
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
    @Path("usuario/{idUsuario}")
    public Response getComentariosPorUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            if (idUsuario <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID de usuario debe ser mayor a 0\"}")
                        .build();
            }
            
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            List<ComentarioResponse> comentarios = comentarioService.getComentariosPorUsuario(idUsuario)
                    .stream()
                    .map(c -> new ComentarioResponse(
                        c.getId_comentario(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getComentario(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(comentarios).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("biblioteca/{idBiblioteca}")
    public Response getComentariosPorBiblioteca(@PathParam("idBiblioteca") int idBiblioteca) {
        try {
            if (idBiblioteca <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID de biblioteca debe ser mayor a 0\"}")
                        .build();
            }
            
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            List<ComentarioResponse> comentarios = comentarioService.getComentariosPorBiblioteca(idBiblioteca)
                    .stream()
                    .map(c -> new ComentarioResponse(
                        c.getId_comentario(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getComentario(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(comentarios).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("videojuego/{idVideojuego}")
    public Response getComentariosPorVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            if (idVideojuego <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del videojuego debe ser mayor a 0\"}")
                        .build();
            }
            
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            List<ComentarioResponse> comentarios = comentarioService.getComentariosPorVideojuego(idVideojuego)
                    .stream()
                    .map(c -> new ComentarioResponse(
                        c.getId_comentario(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getComentario(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(comentarios).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("usuario/{idUsuario}/videojuego/{idVideojuego}")
    public Response getComentariosPorUsuarioYVideojuego(
            @PathParam("idUsuario") int idUsuario,
            @PathParam("idVideojuego") int idVideojuego) {
        try {
            if (idUsuario <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID de usuario debe ser mayor a 0\"}")
                        .build();
            }
            
            if (idVideojuego <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del videojuego debe ser mayor a 0\"}")
                        .build();
            }
            
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            List<ComentarioResponse> comentarios = comentarioService.getComentariosPorUsuarioYVideojuego(idUsuario, idVideojuego)
                    .stream()
                    .map(c -> new ComentarioResponse(
                        c.getId_comentario(),
                        c.getId_usuario(),
                        c.getId_biblioteca(),
                        c.getComentario(),
                        c.getFecha_hora()
                    ))
                    .collect(Collectors.toList());
            
            return Response.ok(comentarios).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @GET
    @Path("estadisticas/videojuego/{idVideojuego}")
    public Response getTotalComentariosVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            if (idVideojuego <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El ID del videojuego debe ser mayor a 0\"}")
                        .build();
            }
            
            ComentarioCrudService comentarioService = new ComentarioCrudService();
            int totalComentarios = comentarioService.getTotalComentariosPorVideojuego(idVideojuego);
            
            String jsonResponse = String.format(
                "{\"totalComentarios\": %d, \"idVideojuego\": %d}",
                totalComentarios,
                idVideojuego
            );
            
            return Response.ok(jsonResponse).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
}