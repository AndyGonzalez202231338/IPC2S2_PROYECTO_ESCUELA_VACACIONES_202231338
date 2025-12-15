package sistema.resources;

import sistema.dtos.ConfiguracionResponse;
import sistema.dtos.UpdateConfiguracionRequest;
import sistema.services.SistemaCrudService;
import exceptions.EntityNotFoundException;
import exceptions.ConfiguracionDataInvalidException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("sistema")
@Produces(MediaType.APPLICATION_JSON)
public class SistemaResource {
    
    @GET
    @Path("configuraciones")
    public Response getAllConfiguraciones() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            List<ConfiguracionResponse> configuraciones = sistemaService.getAllConfiguraciones()
                    .stream()
                    .map(ConfiguracionResponse::new)
                    .toList();
            return Response.ok(configuraciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("configuraciones/activas")
    public Response getConfiguracionesActivas() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            List<ConfiguracionResponse> configuraciones = sistemaService.getConfiguracionesActivas()
                    .stream()
                    .map(ConfiguracionResponse::new)
                    .toList();
            return Response.ok(configuraciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("configuraciones/search")
    public Response searchConfiguraciones(@QueryParam("q") String searchTerm) {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            List<ConfiguracionResponse> configuraciones = sistemaService.searchConfiguraciones(searchTerm)
                    .stream()
                    .map(ConfiguracionResponse::new)
                    .toList();
            return Response.ok(configuraciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("configuraciones/{id}")
    public Response getConfiguracionById(@PathParam("id") int id) {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            sistema.models.ConfiguracionSistema config = sistemaService.getConfiguracionById(id);
            return Response.ok(new ConfiguracionResponse(config)).build();
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
    @Path("configuraciones/nombre/{nombre}")
    public Response getConfiguracionByNombre(@PathParam("nombre") String nombre) {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            sistema.models.ConfiguracionSistema config = sistemaService.getConfiguracionByNombre(nombre);
            return Response.ok(new ConfiguracionResponse(config)).build();
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
    @Path("configuraciones/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateConfiguracion(@PathParam("id") int id, UpdateConfiguracionRequest configRequest) {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            sistema.models.ConfiguracionSistema configActualizada = 
                sistemaService.updateConfiguracion(id, configRequest);
            
            return Response.ok(new ConfiguracionResponse(configActualizada)).build();
            
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
                    
        } catch (ConfiguracionDataInvalidException e) {
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
    @Path("valores")
    public Response getValoresConfiguraciones() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            
            // Obtener valores específicos
            double comisionGlobal = sistemaService.getComisionGlobal();
            int edadMinima = sistemaService.getEdadMinimaAdolescentes();
            int maxMiembros = sistemaService.getMaxMiembrosGrupo();
            
            // Crear respuesta JSON
            String jsonResponse = String.format(
                "{\"comision_global\": %.2f, \"edad_minima\": %d, \"max_miembros_grupo\": %d}",
                comisionGlobal, edadMinima, maxMiembros
            );
            
            return Response.ok(jsonResponse).build();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("comision-global")
    public Response getComisionGlobal() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            double comision = sistemaService.getComisionGlobal();
            return Response.ok("{\"comision_global\": " + comision + "}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("edad-minima")
    public Response getEdadMinima() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            int edad = sistemaService.getEdadMinimaAdolescentes();
            return Response.ok("{\"edad_minima\": " + edad + "}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("max-miembros-grupo")
    public Response getMaxMiembrosGrupo() {
        try {
            SistemaCrudService sistemaService = new SistemaCrudService();
            int max = sistemaService.getMaxMiembrosGrupo();
            return Response.ok("{\"max_miembros_grupo\": " + max + "}").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
}