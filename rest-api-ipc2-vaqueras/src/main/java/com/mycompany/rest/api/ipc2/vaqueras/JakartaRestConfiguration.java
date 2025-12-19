package com.mycompany.rest.api.ipc2.vaqueras;

import biblioteca.resources.BibliotecaResource;
import categoria.resources.CategoriaResource;
import compra.resources.CompraResource;
import empresa.resources.ComisionResource;
import empresa.resources.EmpresaResource;
import grupo.resources.GrupoResource;
import jakarta.ws.rs.ApplicationPath;
import login.resources.LoginResource;
import usuario.resources.UsersResource; 
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import sistema.resources.SistemaResource;
import usuario.resources.SaldoResource;
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
        register(MultiPartFeature.class);

        property(ServerProperties.TRACING, "ALL");
        property(ServerProperties.TRACING_THRESHOLD, "VERBOSE");
        
    }
}