package com.mycompany.rest.api.ipc2.vaqueras;

import biblioteca.resources.BibliotecaResource;
import categoria.resources.CategoriaResource;
import comentario.resources.CalificacionResource;
import comentario.resources.ComentarioResource;
import comentario.resources.RespuestaComentarioResource;
import compra.resources.CompraResource;
import empresa.resources.ComisionResource;
import empresa.resources.EmpresaResource;
import grupo.resources.GrupoResource;
import instalacion.resources.InstalacionJuegoResource;


import jakarta.ws.rs.ApplicationPath;
import login.resources.LoginResource;
import usuario.resources.UsersResource; 
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import reportes.empresa.feedback.ReporteFeedbackResource;
import reportes.empresa.top5juegos.ReporteTop5JuegosResource;
import reportes.sistema.comision.global.ReporteGananciasGlobalesResource;
import reportes.sistema.ventas.calidad.ReporteTopVentasCalidadResource;
import reportes.empresa.ventas.propia.ReporteVentasPropiasResource;
import sistema.resources.SistemaResource;
import usuario.resources.SaldoResource;
import videojuego.resources.MultimediaResource;
import videojuego.resources.VideojuegoResource;

@ApplicationPath("api-ipc2-vaqueras/v1")
public class JakartaRestConfiguration extends ResourceConfig {
    public JakartaRestConfiguration() {
        
        packages("com.mycompany.rest.api.ipc2.vaqueras.resources");
        
        register(LoginResource.class);
        register(UsersResource.class); 
        register(EmpresaResource.class);
        register(CategoriaResource.class);
        register(SistemaResource.class);
        register(ComisionResource.class);
        register(VideojuegoResource.class);
        register(CompraResource.class);
        register(SaldoResource.class);
        register(BibliotecaResource.class);
        register(GrupoResource.class);
        register(CalificacionResource.class);
        register(ComentarioResource.class);
        register(RespuestaComentarioResource.class);
        register(InstalacionJuegoResource.class);
        register(MultimediaResource.class);
        register(ReporteGananciasGlobalesResource.class);
        register(ReporteTopVentasCalidadResource.class);
        register(ReporteVentasPropiasResource.class);
        register(ReporteTop5JuegosResource.class);
        register(ReporteFeedbackResource.class);
        register(MultiPartFeature.class);
        
        property(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        property(ServerProperties.TRACING, "ALL");
        property(ServerProperties.TRACING_THRESHOLD, "VERBOSE");
        
         property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, true);
    }
}