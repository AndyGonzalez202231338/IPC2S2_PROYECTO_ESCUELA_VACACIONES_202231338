package banner.resources;

import banner.models.Banner;
import banner.services.BannerCrudService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("banners")
@Produces(MediaType.APPLICATION_JSON)
public class BannerResource {
    
    
    @GET
    @Path("activos")
    public Response getBannersActivos() {
        try {
            BannerCrudService service = new BannerCrudService();
            return Response.ok(service.getBannersActivos()).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\":\"Error\"}")
                    .build();
        }
    }
    
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response crearBanner(Banner banner) {
        try {
            BannerCrudService service = new BannerCrudService();
            Banner bannerCreado = service.crearBanner(banner);
            
            if (bannerCreado != null) {
                return Response.status(201).entity(bannerCreado).build();
            }
            return Response.status(500)
                    .entity("{\"error\":\"No se pudo crear\"}")
                    .build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(400)
                    .entity("{\"error\":\"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\":\"Error interno\"}")
                    .build();
        }
    }
    
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response actualizarBanner(@PathParam("id") int id, Banner banner) {
        try {
            banner.setId_banner(id);
            BannerCrudService service = new BannerCrudService();
            
            if (service.actualizarBanner(banner)) {
                return Response.ok().entity(banner).build();
            }
            return Response.status(500)
                    .entity("{\"error\":\"No se pudo actualizar\"}")
                    .build();
            
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\":\"Error interno\"}")
                    .build();
        }
    }
    
    
    @DELETE
    @Path("{id}")
    public Response eliminarBanner(@PathParam("id") int id) {
        try {
            BannerCrudService service = new BannerCrudService();
            
            if (service.eliminarBanner(id)) {
                return Response.ok()
                        .entity("{\"message\":\"Banner eliminado\"}")
                        .build();
            }
            return Response.status(500)
                    .entity("{\"error\":\"No se pudo eliminar\"}")
                    .build();
            
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\":\"Error interno\"}")
                    .build();
        }
    }
    
    
    @GET
    public Response getAllBanners() {
        try {
            BannerCrudService service = new BannerCrudService();
            return Response.ok(service.getAllBanners()).build();
        } catch (Exception e) {
            return Response.status(500)
                    .entity("{\"error\":\"Error\"}")
                    .build();
        }
    }
}