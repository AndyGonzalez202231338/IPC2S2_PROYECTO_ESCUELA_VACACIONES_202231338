/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package categoria.resources;

import categoria.dtos.CategoriaResponse;
import categoria.dtos.NewCategoriaRequest;
import categoria.services.CategoriaCrudService;
import exceptions.CategoriaDataInvalidException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

/**
 *
 * @author andy
 */
@Path("categorias")
@Produces(MediaType.APPLICATION_JSON)
public class CategoriaResource {
    
    @GET
    public Response getAllCategorias() {
        try {
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            List<CategoriaResponse> categorias = categoriaService.getAllCategorias()
                    .stream()
                    .map(CategoriaResponse::new)
                    .toList();
            return Response.ok(categorias).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    
    @GET
    @Path("{id}")
    public Response getCategoriaById(@PathParam("id") int id) {
        try {
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            categoria.models.Categoria categoria = categoriaService.getCategoriaById(id);
            return Response.ok(new CategoriaResponse(categoria)).build();
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
    public Response createCategoria(NewCategoriaRequest categoriaRequest) {
        try {
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            categoria.models.Categoria categoriaCreada = categoriaService.createCategoria(categoriaRequest);
            return Response.status(Response.Status.CREATED)
                    .entity(new CategoriaResponse(categoriaCreada))
                    .build();
        } catch (CategoriaDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCategoria(@PathParam("id") int id, NewCategoriaRequest categoriaRequest) {
        try {
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            categoria.models.Categoria categoriaActualizada = categoriaService.updateCategoria(id, categoriaRequest);
            return Response.ok(new CategoriaResponse(categoriaActualizada)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (CategoriaDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
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
    public Response deleteCategoria(@PathParam("id") int id) {
        try {
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            categoriaService.deleteCategoria(id);
            return Response.ok()
                    .entity("{\"message\": \"Categoría eliminada exitosamente\"}")
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
    
    @GET
    @Path("verificar-nombre")
    public Response verificarNombreDisponible(@QueryParam("nombre") String nombre) {
        try {
            if (nombre == null || nombre.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El nombre es requerido\"}")
                        .build();
            }
            
            CategoriaCrudService categoriaService = new CategoriaCrudService();
            boolean disponible = categoriaService.isNombreCategoriaDisponible(nombre);
            
            return Response.ok()
                    .entity("{\"disponible\": " + disponible + ", \"nombre\": \"" + nombre + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
}

