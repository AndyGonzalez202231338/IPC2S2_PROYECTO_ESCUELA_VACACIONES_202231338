/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.resources;

import compra.dtos.CompraResponse;
import compra.dtos.NewCompraRequest;
import compra.services.CompraCrudService;
import exceptions.CompraDataInvalidException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
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
import java.util.Map;

/**
 *
 * @author andy
 */
@Path("compras")
@Produces(MediaType.APPLICATION_JSON)
public class CompraResource {
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCompra(NewCompraRequest compraRequest) {
        try {
            CompraCrudService compraService = new CompraCrudService();
            compra.models.Compra compraCreada = compraService.createCompra(compraRequest);
            
            CompraResponse response = new CompraResponse(compraCreada);
            
            return Response.status(Response.Status.CREATED)
                    .entity(response)
                    .build();
        } catch (CompraDataInvalidException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.PAYMENT_REQUIRED)
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
    public Response getAllCompras() {
        try {
            CompraCrudService compraService = new CompraCrudService();
            List<CompraResponse> compras = compraService.getAllCompras()
                    .stream()
                    .map(CompraResponse::new)
                    .toList();
            return Response.ok(compras).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
    
    @GET
    @Path("{id}")
    public Response getCompraById(@PathParam("id") int id) {
        try {
            CompraCrudService compraService = new CompraCrudService();
            compra.models.Compra compra = compraService.getCompraById(id);
            return Response.ok(new CompraResponse(compra)).build();
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
    @Path("usuario/{idUsuario}")
    public Response getComprasByUsuario(@PathParam("idUsuario") int idUsuario) {
        try {
            CompraCrudService compraService = new CompraCrudService();
            List<CompraResponse> compras = compraService.getComprasByUsuario(idUsuario)
                    .stream()
                    .map(CompraResponse::new)
                    .toList();
            return Response.ok(compras).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Error interno del servidor\"}")
                    .build();
        }
    }
}