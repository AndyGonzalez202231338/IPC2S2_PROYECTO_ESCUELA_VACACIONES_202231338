/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.resources;

import exceptions.EntityNotFoundException;
import exceptions.MultimediaDataInvalidException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import videojuego.dtos.MultimediaResponse;
import videojuego.dtos.NewMultimediaRequest;
import videojuego.models.Multimedia;
import videojuego.services.MultimediaCrudService;


/**
 *
 * @author andy
 */
@Path("multimedia")
@Produces(MediaType.APPLICATION_JSON)
public class MultimediaResource {
    
    @GET
    public Response getAllMultimedia() {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            List<MultimediaResponse> multimedia = multimediaService.getAllMultimedia()
                    .stream()
                    .map(MultimediaResponse::new)
                    .toList();
            return Response.ok(multimedia).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("{id}")
    public Response getMultimediaById(@PathParam("id") int id) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            Multimedia multimedia = multimediaService.getMultimediaById(id);
            return Response.ok(new MultimediaResponse(multimedia)).build();
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
    public Response getMultimediaByVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            List<MultimediaResponse> multimedia = multimediaService.getMultimediaByVideojuego(idVideojuego)
                    .stream()
                    .map(MultimediaResponse::new)
                    .toList();
            return Response.ok(multimedia).build();
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
    @Path("videojuego/{idVideojuego}/count")
    public Response countMultimediaByVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            int count = multimediaService.countMultimediaByVideojuego(idVideojuego);
            return Response.ok()
                    .entity("{\"count\": " + count + ", \"id_videojuego\": " + idVideojuego + "}")
                    .build();
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
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createMultimedia(NewMultimediaRequest multimediaRequest) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            Multimedia multimediaCreada = multimediaService.createMultimedia(multimediaRequest);
            return Response.status(Response.Status.CREATED)
                    .entity(new MultimediaResponse(multimediaCreada))
                    .build();
        } catch (MultimediaDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @POST
    @Path("videojuego/{idVideojuego}/multiple")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearMultiplesImagenes(@PathParam("idVideojuego") int idVideojuego, 
        List<NewMultimediaRequest> requests) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            List<Multimedia> multimediasCreadas = 
                    multimediaService.crearMultiplesImagenes(idVideojuego, requests);
            
            List<MultimediaResponse> responses = multimediasCreadas.stream()
                    .map(MultimediaResponse::new)
                    .toList();
            
            return Response.status(Response.Status.CREATED)
                    .entity(responses)
                    .build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (MultimediaDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateMultimedia(@PathParam("id") int id, NewMultimediaRequest multimediaRequest) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            Multimedia multimediaActualizada = multimediaService.updateMultimedia(id, multimediaRequest);
            return Response.ok(new MultimediaResponse(multimediaActualizada)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (MultimediaDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @DELETE
    @Path("{id}")
    public Response deleteMultimedia(@PathParam("id") int id) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            multimediaService.deleteMultimedia(id);
            return Response.ok()
                    .entity("{\"message\": \"Multimedia eliminada exitosamente\"}")
                    .build();
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
    
    @DELETE
    @Path("videojuego/{idVideojuego}")
    public Response deleteMultimediaByVideojuego(@PathParam("idVideojuego") int idVideojuego) {
        try {
            MultimediaCrudService multimediaService = new MultimediaCrudService();
            multimediaService.deleteMultimediaByVideojuego(idVideojuego);
            return Response.ok()
                    .entity("{\"message\": \"Todas las multimedias del videojuego eliminadas exitosamente\"}")
                    .build();
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
}
