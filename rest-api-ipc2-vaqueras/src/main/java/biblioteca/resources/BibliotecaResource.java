/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.resources;


import biblioteca.dtos.BibliotecaResponse;
import biblioteca.dtos.NewBibliotecaRequest;
import biblioteca.services.BibliotecaService;
import exceptions.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author andy
 */
@Path("/biblioteca")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BibliotecaResource {
    
    private final BibliotecaService bibliotecaService;
    
    public BibliotecaResource() {
        this.bibliotecaService = new BibliotecaService();
    }
    
    /**
     * Agrega un juego a la biblioteca de un usuario de manera manual para compraobar funcionamiento
     * @param request
     * @return 
     */
    @POST
    public Response agregarABiblioteca(NewBibliotecaRequest request) {
        try {
            BibliotecaResponse biblioteca = bibliotecaService.agregarABiblioteca(request);
            
            return Response.status(Response.Status.CREATED)
                    .entity(biblioteca)
                    .build();
                    
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (EntityAlreadyExistsException e) {
            return buildErrorResponse(Response.Status.CONFLICT, e.getMessage());
            
        } catch (IllegalRequestException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene la biblioteca completa de un usuario
     * @param idUsuario
     * @return 
     */
    @GET
    @Path("/usuario/{idUsuario}")
    public Response obtenerBibliotecaUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            List<BibliotecaResponse> biblioteca = bibliotecaService.obtenerBibliotecaUsuario(idUsuario);
            
            return Response.ok(biblioteca).build();
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene un registro específico de biblioteca
     * @param idBiblioteca
     * @return 
     */
    @GET
    @Path("/{idBiblioteca}")
    public Response obtenerRegistroBiblioteca(@PathParam("idBiblioteca") int idBiblioteca) {
        try {
            BibliotecaResponse registro = bibliotecaService.obtenerRegistroBiblioteca(idBiblioteca);
            
            return Response.ok(registro).build();
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si un usuario tiene un juego específico en su biblioteca
     * @param idUsuario
     * @param idVideojuego
     * @return 
     */
    @GET
    @Path("/usuario/{idUsuario}/verificar/{idVideojuego}")
    public Response verificarJuegoEnBiblioteca(
            @PathParam("idUsuario") int idUsuario,
            @PathParam("idVideojuego") int idVideojuego) {
        
        try {
            boolean tieneJuego = bibliotecaService.usuarioTieneJuego(idUsuario, idVideojuego);
            
            VerificacionResponse response = new VerificacionResponse(tieneJuego);
            return Response.ok(response).build();
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Elimina un juego de la biblioteca (solo préstamos)
     * @param idBiblioteca
     * @return 
     */
    @DELETE
    @Path("/{idBiblioteca}")
    public Response eliminarDeBiblioteca(@PathParam("idBiblioteca") int idBiblioteca) {
        try {
            boolean eliminado = bibliotecaService.eliminarDeBiblioteca(idBiblioteca);
            
            if (eliminado) {
                return Response.noContent().build();
            } else {
                return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                    "No se pudo eliminar el registro");
            }
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene los juegos prestados de un usuario
     * @param idUsuario
     * @return 
     */
    @GET
    @Path("/usuario/{idUsuario}/prestamos")
    public Response obtenerJuegosPrestados(@PathParam("idUsuario") int idUsuario) {
        try {
            List<BibliotecaResponse> prestamos = bibliotecaService.obtenerJuegosPrestados(idUsuario);
            
            return Response.ok(prestamos).build();
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Obtiene los juegos comprados de un usuario
     * @param idUsuario
     * @return 
     */
    @GET
    @Path("/usuario/{idUsuario}/compras")
    public Response obtenerJuegosComprados(@PathParam("idUsuario") int idUsuario) {
        try {
            List<BibliotecaResponse> compras = bibliotecaService.obtenerJuegosComprados(idUsuario);
            
            return Response.ok(compras).build();
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Busca juegos en la biblioteca por título
     * @param idUsuario
     * @param titulo
     * @return 
     */
    @GET
    @Path("/usuario/{idUsuario}/buscar")
    public Response buscarEnBiblioteca(
            @PathParam("idUsuario") int idUsuario,
            @QueryParam("titulo") String titulo) {
        
        try {
            if (titulo == null || titulo.trim().isEmpty()) {
                return buildErrorResponse(Response.Status.BAD_REQUEST, 
                    "El parámetro 'titulo' es requerido para la búsqueda");
            }
            
            List<BibliotecaResponse> resultados = 
                bibliotecaService.buscarEnBibliotecaPorTitulo(idUsuario, titulo);
            
            return Response.ok(resultados).build();
            
        } catch (EntityNotFoundException e) {
            return buildErrorResponse(Response.Status.NOT_FOUND, e.getMessage());
            
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error en la base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Método auxiliar para construir respuestas de error en texto para un json
     * @param status
     * @param message
     * @return 
     */ 
    private Response buildErrorResponse(Response.Status status, String message) {
        ErrorResponse error = new ErrorResponse(message);
        return Response.status(status).entity(error).build();
    }
    
    // Clases internas para respuestas
    private static class ErrorResponse {
        private String error;
        private long timestamp;
        
        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = System.currentTimeMillis();
        }
        
        public String getError() {
            return error;
        }
        
        public long getTimestamp() {
            return timestamp;
        }
    }
    
    private static class VerificacionResponse {
        private boolean tieneJuego;
        
        public VerificacionResponse(boolean tieneJuego) {
            this.tieneJuego = tieneJuego;
        }
        
        public boolean isTieneJuego() {
            return tieneJuego;
        }
        
        public void setTieneJuego(boolean tieneJuego) {
            this.tieneJuego = tieneJuego;
        }
    }
    
}
