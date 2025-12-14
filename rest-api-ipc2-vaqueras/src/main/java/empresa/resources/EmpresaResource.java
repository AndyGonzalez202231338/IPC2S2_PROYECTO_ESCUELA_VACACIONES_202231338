/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.resources;

import empresa.dtos.NewEmpresaRequest;
import empresa.dtos.EmpresaResponse;
import empresa.dtos.UpdateEmpresaRequest;
import empresa.models.Empresa;
import empresa.services.EmpresaCreator;
import empresa.services.EmpresaCrudService;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.EmpresaDataInvalidException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import user.services.UsersCrudService;
import user.dtos.UserResponse;
/**
 *
 * @author andy
 */
@Path("empresas")
public class EmpresaResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createEmpresa(NewEmpresaRequest empresaRequest) { 
        EmpresaCreator empresaCreator = new EmpresaCreator();

        try {
            Empresa empresaCreada = empresaCreator.createEmpresa(empresaRequest);
            return Response.status(Response.Status.CREATED)
                    .entity(new EmpresaResponse(empresaCreada))
                    .build();
                    
        } catch (EmpresaDataInvalidException e) {
            System.out.println("ERROR 400: Datos inválidos - " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityNotFoundException e) {
            System.out.println("ERROR 404: " + e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (Exception e) {
            System.out.println("ERROR 500: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("administradores-disponibles")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdministradoresDisponibles() {
        
        UsersCrudService usersService = new UsersCrudService();
        try {
            List<UserResponse> administradores = usersService.getAdministradoresSinEmpresa()
                    .stream()
                    .map(UserResponse::new)
                    .toList();
            
            return Response.ok(administradores).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmpresas() {
        EmpresaCrudService empresaService = new EmpresaCrudService();
        List<EmpresaResponse> empresas = empresaService.getAllEmpresas()
                .stream()
                .map(EmpresaResponse::new)
                .toList();
        return Response.ok(empresas).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getEmpresaById(@PathParam("id") int id) {
        EmpresaCrudService empresaService = new EmpresaCrudService();
        try {
            Empresa empresa = empresaService.getEmpresaById(id);
            return Response.ok(new EmpresaResponse(empresa)).build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateEmpresa(@PathParam("id") int id, UpdateEmpresaRequest empresaRequest) {
        EmpresaCrudService empresaService = new EmpresaCrudService();
        try {
            Empresa empresaActualizada = empresaService.updateEmpresa(id, empresaRequest);
            return Response.ok(new EmpresaResponse(empresaActualizada)).build();

        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Empresa no encontrada\"}")
                    .build();

        } catch (EmpresaDataInvalidException e) {
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
    public Response deleteEmpresa(@PathParam("id") int id) {
        EmpresaCrudService empresaService = new EmpresaCrudService();
        try {
            empresaService.deleteEmpresa(id);
            return Response.ok().build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}