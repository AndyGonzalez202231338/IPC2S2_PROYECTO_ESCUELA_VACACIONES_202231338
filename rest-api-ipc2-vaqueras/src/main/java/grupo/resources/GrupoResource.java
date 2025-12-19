package grupo.resources;

import grupo.dtos.GrupoResponse;
import grupo.dtos.IntegranteResponse;
import grupo.dtos.NewGrupoRequest;
import grupo.services.GrupoLogicaService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("/grupos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GrupoResource {

    private final GrupoLogicaService grupoLogicService;

    public GrupoResource() {
        this.grupoLogicService = new GrupoLogicaService();
    }

    @POST
    public Response crearGrupo(NewGrupoRequest grupoRequest) {

        try {
            GrupoResponse grupoCreado = grupoLogicService.crearGrupoConValidaciones(grupoRequest);
            return Response.status(Response.Status.CREATED)
                    .entity(grupoCreado)
                    .build();

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    @POST
    @Path("/{idGrupo}/participantes/{idUsuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response agregarParticipante(@PathParam("idGrupo") int idGrupo, @PathParam("idUsuario") int idUsuario) {

        try {
            boolean agregado = grupoLogicService.agregarParticipanteConValidaciones(idGrupo, idUsuario);

            if (agregado) {
                return Response.status(Response.Status.CREATED)
                        .entity("{\"mensaje\": \"Participante agregado exitosamente\"}")
                        .build();
            } else {
                return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo agregar el participante");
            }

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/{idGrupo}/participantes/{idUsuario}")
    public Response eliminarParticipante(
            @PathParam("idGrupo") int idGrupo,
            @PathParam("idUsuario") int idUsuario,
            @HeaderParam("X-User-ID") int idUsuarioSolicitante) {

        try {
            boolean eliminado = grupoLogicService.eliminarParticipanteConValidaciones(
                    idGrupo, idUsuario, idUsuarioSolicitante);

            if (eliminado) {
                return Response.noContent().build();
            } else {
                return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo eliminar el participante");
            }

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    @DELETE
    @Path("/{idGrupo}")
    public Response eliminarGrupo(
            @PathParam("idGrupo") int idGrupo,
            @HeaderParam("X-User-ID") int idUsuarioSolicitante) {

        try {
            boolean eliminado = grupoLogicService.eliminarGrupoConValidaciones(idGrupo, idUsuarioSolicitante);

            if (eliminado) {
                return Response.noContent().build();
            } else {
                return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                        "No se pudo eliminar el grupo");
            }

        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    @GET
    @Path("/usuario/{idUsuario}")
    public Response obtenerGruposDeUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            java.util.List<GrupoResponse> grupos = grupoLogicService.obtenerGruposDeUsuario(idUsuario);
            return Response.ok(grupos).build();

        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }

    @GET
    @Path("/{idGrupo}/espacios-disponibles")
    public Response obtenerEspaciosDisponibles(@PathParam("idGrupo") int idGrupo) {
        try {
            int espacios = grupoLogicService.obtenerEspaciosDisponibles(idGrupo);
            return Response.ok("{\"espacios_disponibles\": " + espacios + "}").build();

        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                    "Error en la base de datos: " + e.getMessage());
        }
    }
    
    @GET
    @Path("/{idGrupo}/integrantes")
    public Response obtenerIntegrantes(@PathParam("idGrupo") int idGrupo) {
        
        System.out.println("Obteniendo integrantes del grupo: " + idGrupo);
        
        try {
            List<IntegranteResponse> integrantes = grupoLogicService.obtenerIntegrantesDelGrupo(idGrupo);
            
            return Response.ok(integrantes).build();
            
        } catch (IllegalArgumentException e) {
            return buildErrorResponse(Response.Status.BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            return buildErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,
                "Error en la base de datos: " + e.getMessage());
        }
    }

    private Response buildErrorResponse(Response.Status status, String message) {
        return Response.status(status)
                .entity("{\"error\": \"" + message + "\"}")
                .build();
    }
}
