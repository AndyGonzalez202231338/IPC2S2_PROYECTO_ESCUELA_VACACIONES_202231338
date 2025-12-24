/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.resources;

import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.VideojuegoDataInvalidException;
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
import java.util.Map;
import videojuego.dtos.NewVideojuegoRequest;
import videojuego.dtos.VideojuegoCategoriaRequest;
import videojuego.dtos.VideojuegoResponse;
import videojuego.models.Videojuego;
import videojuego.services.VideojuegoCrudService;

/**
 *
 * @author andy
 */
@Path("videojuegos")
@Produces(MediaType.APPLICATION_JSON)
public class VideojuegoResource {

    @GET
    public Response getAllVideojuegos(@QueryParam("incluirCategorias") boolean incluirCategorias) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            List<VideojuegoResponse> videojuegos = videojuegoService.getAllVideojuegos(incluirCategorias)
                    .stream()
                    .map(VideojuegoResponse::new)
                    .toList();
            return Response.ok(videojuegos).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @GET
    @Path("{id}")
    public Response getVideojuegoById(@PathParam("id") int id,
            @QueryParam("incluirCategorias") boolean incluirCategorias) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuego.models.Videojuego videojuego = videojuegoService.getVideojuegoById(id, incluirCategorias);
            return Response.ok(new VideojuegoResponse(videojuego)).build();
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
    @Path("buscar-titulo")
    public Response getVideojuegoByTitulo(@QueryParam("titulo") String titulo,
            @QueryParam("incluirCategorias") boolean incluirCategorias) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El parámetro 'titulo' es requerido\"}")
                        .build();
            }

            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuego.models.Videojuego videojuego = videojuegoService.getVideojuegoByTitulo(titulo, incluirCategorias);

            if (videojuego == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Videojuego no encontrado con título: " + titulo + "\"}")
                        .build();
            }

            return Response.ok(new VideojuegoResponse(videojuego)).build();

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
    public Response createVideojuego(NewVideojuegoRequest videojuegoRequest) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuego.models.Videojuego videojuegoCreado = videojuegoService.createVideojuego(videojuegoRequest);
            return Response.status(Response.Status.CREATED)
                    .entity(new VideojuegoResponse(videojuegoCreado))
                    .build();
        } catch (VideojuegoDataInvalidException e) {
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
    public Response updateVideojuego(@PathParam("id") int id, NewVideojuegoRequest videojuegoRequest) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuego.models.Videojuego videojuegoActualizado = videojuegoService.updateVideojuego(id, videojuegoRequest);
            return Response.ok(new VideojuegoResponse(videojuegoActualizado)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (VideojuegoDataInvalidException e) {
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
    public Response deleteVideojuego(@PathParam("id") int id) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuegoService.deleteVideojuego(id);
            return Response.ok()
                    .entity("{\"message\": \"Videojuego eliminado exitosamente\"}")
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

    @PUT
    @Path("{id}/categorias")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarCategoriasVideojuego(@PathParam("id") int id,
            VideojuegoCategoriaRequest categoriasRequest) {
        try {
            categoriasRequest.setId_videojuego(id);
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            videojuego.models.Videojuego videojuegoActualizado
                    = videojuegoService.actualizarCategoriasVideojuego(categoriasRequest);
            return Response.ok(new VideojuegoResponse(videojuegoActualizado)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (VideojuegoDataInvalidException e) {
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

    @GET
    @Path("{id}/categorias/aprobadas")
    public Response getCategoriasAprobadas(@PathParam("id") int id) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            List<categoria.models.Categoria> categorias = videojuegoService.getCategoriasAprobadas(id);
            List<categoria.dtos.CategoriaResponse> categoriasResponse = categorias.stream()
                    .map(categoria.dtos.CategoriaResponse::new)
                    .toList();
            return Response.ok(categoriasResponse).build();
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
    @Path("{id}/categorias")
    public Response getCategorias(@PathParam("id") int id) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            List<categoria.models.Categoria> categorias = videojuegoService.getCategorias(id);
            List<categoria.dtos.CategoriaResponse> categoriasResponse = categorias.stream()
                    .map(categoria.dtos.CategoriaResponse::new)
                    .toList();
            return Response.ok(categoriasResponse).build();
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

    @PUT
    @Path("{idVideojuego}/categorias/{idCategoria}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarEstadoCategoria(@PathParam("idVideojuego") int idVideojuego,
            @PathParam("idCategoria") int idCategoria,
            String estado) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            boolean actualizado = videojuegoService.actualizarEstadoCategoria(idVideojuego, idCategoria, estado);
            if (actualizado) {
                return Response.ok()
                        .entity("{\"message\": \"Estado de categoría actualizado exitosamente\"}")
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"No se pudo actualizar el estado\"}")
                        .build();
            }
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
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

    @GET
    @Path("verificar-titulo")
    public Response verificarTituloDisponible(@QueryParam("titulo") String titulo,
            @QueryParam("idEmpresa") int idEmpresa) {
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"El título es requerido\"}")
                        .build();
            }

            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            boolean disponible = videojuegoService.isTituloDisponible(titulo, idEmpresa);

            return Response.ok()
                    .entity("{\"disponible\": " + disponible + ", \"titulo\": \"" + titulo + "\"}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @GET
    @Path("empresa/{id_empresa}")
    public Response getVideojuegosByEmpresa(@PathParam("id_empresa") int idEmpresa,
            @QueryParam("incluirCategorias") boolean incluirCategorias) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            List<Videojuego> videojuegos = videojuegoService.getVideojuegosByEmpresa(idEmpresa, incluirCategorias);

            List<VideojuegoResponse> videojuegosResponse = videojuegos.stream()
                    .map(VideojuegoResponse::new)
                    .toList();

            return Response.ok(videojuegosResponse).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Bloquear comentarios de TODOS los videojuegos de una empresa
     */
    @PUT
    @Path("empresa/{idEmpresa}/bloquear-comentarios-todos")
    public Response bloquearComentariosTodosVideojuegos(@PathParam("idEmpresa") int idEmpresa) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            boolean bloqueados = videojuegoService.bloquearComentariosTodosVideojuegosEmpresa(idEmpresa);

            if (bloqueados) {
                return Response.ok()
                        .entity("{\"message\": \"Comentarios bloqueados para TODOS los videojuegos de la empresa\", "
                                + "\"empresa_id\": " + idEmpresa + ", "
                                + "\"accion\": \"BLOQUEAR_TODOS\"}")
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"No se pudieron bloquear los comentarios\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    /**
     * Desbloquear comentarios de TODOS los videojuegos de una empresa
     */
    @PUT
    @Path("empresa/{idEmpresa}/desbloquear-comentarios-todos")
    public Response desbloquearComentariosTodosVideojuegos(@PathParam("idEmpresa") int idEmpresa) {
        try {
            VideojuegoCrudService videojuegoService = new VideojuegoCrudService();
            boolean desbloqueados = videojuegoService.desbloquearComentariosTodosVideojuegosEmpresa(idEmpresa);

            if (desbloqueados) {
                return Response.ok()
                        .entity("{\"message\": \"Comentarios desbloqueados para TODOS los videojuegos de la empresa\", "
                                + "\"empresa_id\": " + idEmpresa + ", "
                                + "\"accion\": \"DESBLOQUEAR_TODOS\"}")
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("{\"error\": \"No se pudieron desbloquear los comentarios\"}")
                        .build();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

}
