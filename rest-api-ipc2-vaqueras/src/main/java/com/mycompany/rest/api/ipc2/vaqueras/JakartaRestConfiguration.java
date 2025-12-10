package com.mycompany.rest.api.ipc2.vaqueras;

import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("api-ipc2-vaqueras/v1")
public class JakartaRestConfiguration extends ResourceConfig {
    public JakartaRestConfiguration() {
        //registrar el paquete con los recursos
        packages("com.mycompany.rest.api.ipc2.resources");
    
        // Registrar MultiPartFeature para manejar archivos
        register(MultiPartFeature.class);
    }
}
