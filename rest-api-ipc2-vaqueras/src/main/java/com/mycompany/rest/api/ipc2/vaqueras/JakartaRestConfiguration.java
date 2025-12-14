package com.mycompany.rest.api.ipc2.vaqueras;

import empresa.resources.EmpresaResource;
import jakarta.ws.rs.ApplicationPath;
import login.resources.LoginResource;
import usuario.resources.UsersResource; 
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

@ApplicationPath("api-ipc2-vaqueras/v1")
public class JakartaRestConfiguration extends ResourceConfig {
    public JakartaRestConfiguration() {
        
        packages("com.mycompany.rest.api.ipc2.vaqueras.resources");
        
        register(LoginResource.class);
        register(UsersResource.class); 
        register(EmpresaResource.class);
        register(MultiPartFeature.class);
        
        property(ServerProperties.TRACING, "ALL");
        property(ServerProperties.TRACING_THRESHOLD, "VERBOSE");
        
    }
}