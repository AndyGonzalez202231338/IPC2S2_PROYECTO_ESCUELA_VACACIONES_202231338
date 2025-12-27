package instalacion.resources;

import db.InstalacionJuegoDB;
import exceptions.EntityNotFoundException;
import exceptions.InstalacionDataInvalidException;
import exceptions.InstalacionRestrictionException;
import instalacion.dtos.InstalacionResponse;
import instalacion.dtos.JuegoPrestable;
import instalacion.dtos.NewInstalacionRequest;
import instalacion.models.InstalacionJuego;
import instalacion.services.InstalacionJuegoCrudService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("instalaciones")
@Produces(MediaType.APPLICATION_JSON)
public class InstalacionJuegoResource {

    @GET
    public Response getAllInstalaciones() {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            List<InstalacionResponse> instalaciones = service.getAllInstalaciones()
                    .stream()
                    .map(InstalacionResponse::new)
                    .toList();
            return Response.ok(instalaciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @GET
    @Path("{id}")
    public Response getInstalacionById(@PathParam("id") int id) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            InstalacionJuego instalacion = service.getInstalacionById(id);
            return Response.ok(new InstalacionResponse(instalacion)).build();
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
    public Response createInstalacion(NewInstalacionRequest request) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            InstalacionJuego instalacionCreada = service.createInstalacion(request);
            return Response.status(Response.Status.CREATED)
                    .entity(new InstalacionResponse(instalacionCreada))
                    .build();
        } catch (InstalacionDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (InstalacionRestrictionException e) {
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

    @GET
    @Path("usuario/{id_usuario}")
    public Response getInstalacionesByUsuario(@PathParam("id_usuario") int id_usuario) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            List<InstalacionResponse> instalaciones = service.getInstalacionesByUsuario(id_usuario)
                    .stream()
                    .map(InstalacionResponse::new)
                    .toList();
            return Response.ok(instalaciones).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @POST
    @Path("instalar-comprado/{id_usuario}/{id_videojuego}/{id_usuario_instala}")
    public Response instalarJuegoComprado(@PathParam("id_usuario") int id_usuario,
            @PathParam("id_videojuego") int id_videojuego, @PathParam("id_usuario_instala") int id_usuario_instala) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            InstalacionJuego instalacion = service.instalarJuegoComprado(id_usuario, id_videojuego, id_usuario_instala);
            return Response.status(Response.Status.CREATED)
                    .entity(new InstalacionResponse(instalacion))
                    .build();
        } catch (InstalacionDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (InstalacionRestrictionException e) {
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

    @POST
    @Path("instalar-prestado/{id_usuario}/{id_videojuego}/{id_usuario_instala}")
    public Response instalarJuegoPrestado(@PathParam("id_usuario") int id_usuario,
            @PathParam("id_videojuego") int id_videojuego, @PathParam("id_usuario_instala") int id_usuario_instala) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            InstalacionJuego instalacion = service.instalarJuegoPrestado(id_usuario, id_videojuego, id_usuario_instala);
            return Response.status(Response.Status.CREATED)
                    .entity(new InstalacionResponse(instalacion))
                    .build();
        } catch (InstalacionDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (InstalacionRestrictionException e) {
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

    @POST
    @Path("desinstalar/{id_instalacion}")
    public Response desinstalarJuego(@PathParam("id_instalacion") int id_instalacion) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            InstalacionJuego instalacion = service.desinstalarJuego(id_instalacion);
            return Response.ok(new InstalacionResponse(instalacion)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (InstalacionDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (InstalacionRestrictionException e) {
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

    @GET
    @Path("puede-instalar-prestado/{id_usuario}")
    public Response puedeInstalarJuegoPrestado(@PathParam("id_usuario") int id_usuario) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            boolean puedeInstalar = service.puedeInstalarJuegoPrestado(id_usuario);
            return Response.ok()
                    .entity("{\"puede_instalar\": " + puedeInstalar + ", \"id_usuario\": " + id_usuario + "}")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }

    @GET
    @Path("prestables/{id_usuario}")
    public Response getJuegosPrestables(@PathParam("id_usuario") int id_usuario) {
        try {
            System.out.println("GET /prestables/" + id_usuario + " - Iniciando...");

            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            List<JuegoPrestable> juegos = service.getJuegosPrestablesPorGrupo(id_usuario);

            System.out.println("GET /prestables/" + id_usuario + " - Encontrados " + juegos.size() + " juegos");

            return Response.ok(juegos).build();
        } catch (Exception e) {
            System.err.println("Error en GET /prestables/" + id_usuario + ":");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("prestables-agrupados/{id_usuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJuegosPrestablesAgrupados(@PathParam("id_usuario") int id_usuario) {
        try {
            System.out.println("=== GET /prestables-agrupados/" + id_usuario + " (DEFINITIVO) ===");

            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            List<JuegoPrestable> juegos = service.getJuegosPrestablesPorGrupo(id_usuario);

            System.out.println("Total juegos encontrados en BD: " + juegos.size());

            // Si no hay juegos, devolver respuesta vacía
            if (juegos.isEmpty()) {
                Map<String, Object> respuestaVacia = new HashMap<>();
                respuestaVacia.put("success", true);
                respuestaVacia.put("message", "No hay juegos disponibles para préstamo");
                respuestaVacia.put("usuario_id", id_usuario);
                respuestaVacia.put("total_juegos", 0);
                respuestaVacia.put("grupos", new ArrayList<>());

                return Response.ok(respuestaVacia).build();
            }

            // Agrupar juegos por grupo (estructura eficiente)
            Map<Integer, Map<String, Object>> gruposMap = new LinkedHashMap<>();

            for (JuegoPrestable juego : juegos) {
                int grupoId = juego.getId_grupo();

                // Inicializar grupo si no existe
                if (!gruposMap.containsKey(grupoId)) {
                    Map<String, Object> grupoInfo = new HashMap<>();
                    grupoInfo.put("id_grupo", grupoId);
                    grupoInfo.put("nombre_grupo", juego.getNombre_grupo() != null
                            ? juego.getNombre_grupo() : "Grupo " + grupoId);
                    grupoInfo.put("juegos", new ArrayList<Map<String, Object>>());
                    gruposMap.put(grupoId, grupoInfo);
                }

                // Crear objeto juego simplificado pero informativo
                Map<String, Object> juegoInfo = new HashMap<>();
                juegoInfo.put("id_videojuego", juego.getId_videojuego());
                juegoInfo.put("titulo", juego.getTitulo() != null ? juego.getTitulo() : "Sin título");
                juegoInfo.put("descripcion", juego.getDescripcion() != null
                        ? (juego.getDescripcion().length() > 100
                        ? juego.getDescripcion().substring(0, 100) + "..."
                        : juego.getDescripcion()) : "");
                juegoInfo.put("clasificacion_edad", juego.getClasificacion_edad() != null
                        ? juego.getClasificacion_edad() : "E");

                // Información del propietario
                Map<String, Object> propietarioInfo = new HashMap<>();
                propietarioInfo.put("id_usuario", juego.getId_usuario_propietario());
                propietarioInfo.put("nombre", juego.getNombre_propietario() != null
                        ? juego.getNombre_propietario() : "Usuario "
                        + juego.getId_usuario_propietario());
                juegoInfo.put("propietario", propietarioInfo);

                juegoInfo.put("ya_prestado", juego.isYa_prestado());
                juegoInfo.put("puede_solicitar", !juego.isYa_prestado());

                
                ((List<Map<String, Object>>) gruposMap.get(grupoId).get("juegos")).add(juegoInfo);
            }

            // Convertir mapa de grupos a lista
            List<Map<String, Object>> gruposLista = new ArrayList<>(gruposMap.values());

            // Calcular estadísticas
            int totalJuegosUnicos = juegos.size();
            int totalGrupos = gruposLista.size();
            int totalPropietarios = juegos.stream()
                    .map(JuegoPrestable::getId_usuario_propietario)
                    .collect(Collectors.toSet())
                    .size();

            // Construir respuesta final
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("success", true);
            respuesta.put("usuario_id", id_usuario);
            respuesta.put("total_juegos", totalJuegosUnicos);
            respuesta.put("total_grupos", totalGrupos);
            respuesta.put("total_propietarios", totalPropietarios);

            respuesta.put("grupos", gruposLista);

            // Agregar resumen por grupo
            List<Map<String, Object>> resumenGrupos = new ArrayList<>();
            for (Map<String, Object> grupo : gruposLista) {
                Map<String, Object> resumen = new HashMap<>();
                resumen.put("id_grupo", grupo.get("id_grupo"));
                resumen.put("nombre_grupo", grupo.get("nombre_grupo"));
                resumen.put("total_juegos", ((List<?>) grupo.get("juegos")).size());
                resumenGrupos.add(resumen);
            }
            respuesta.put("resumen_grupos", resumenGrupos);



            return Response.ok(respuesta)
                    .header("X-Total-Juegos", String.valueOf(totalJuegosUnicos))
                    .header("X-Total-Grupos", String.valueOf(totalGrupos))
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            // Respuesta de error detallada pero controlada
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error al obtener juegos prestables");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("usuario_id", id_usuario);

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorResponse)
                    .build();
        }
    }

    @GET
    @Path("prestables-ids/{id_usuario}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJuegosPrestablesSoloIds(@PathParam("id_usuario") int id_usuario) {
        try {
            InstalacionJuegoCrudService service = new InstalacionJuegoCrudService();
            List<JuegoPrestable> juegos = service.getJuegosPrestablesPorGrupo(id_usuario);

            // Extraer solo IDs de videojuegos disponibles
            List<Integer> videojuegoIds = juegos.stream()
                    .map(JuegoPrestable::getId_videojuego)
                    .distinct()
                    .collect(Collectors.toList());

            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("usuario_id", id_usuario);
            respuesta.put("videojuego_ids", videojuegoIds);
            respuesta.put("total_disponibles", videojuegoIds.size());

            return Response.ok(respuesta).build();

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error obteniendo IDs");
            error.put("message", e.getMessage());
            return Response.status(500).entity(error).build();
        }
    }

}
