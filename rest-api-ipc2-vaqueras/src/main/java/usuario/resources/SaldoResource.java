/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package usuario.resources;

import user.dtos.OperacionSaldoRequest;
import user.dtos.OperacionSaldoResponse;
import user.services.SaldoService;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 *
 * @author andy
 */
@Path("saldo")
@Produces(MediaType.APPLICATION_JSON)
public class SaldoResource {
    
    @POST
    @Path("debitar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response debitarSaldo(OperacionSaldoRequest request) {
        try {
            SaldoService saldoService = new SaldoService();
            OperacionSaldoResponse response = saldoService.debitarSaldo(
                request.getId_usuario(), 
                request.getMonto()
            );
            
            return Response.ok(response).build();
            
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (SaldoInsuficienteException e) {
            return Response.status(Response.Status.PAYMENT_REQUIRED)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
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
    
    @POST
    @Path("acreditar")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response acreditarSaldo(OperacionSaldoRequest request) {
        try {
            SaldoService saldoService = new SaldoService();
            OperacionSaldoResponse response = saldoService.acreditarSaldo(
                request.getId_usuario(), 
                request.getMonto()
            );
            
            return Response.ok(response).build();
            
        } catch (EntityNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
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
    @Path("{idUsuario}")
    public Response obtenerSaldo(@PathParam("idUsuario") int idUsuario) {
        try {
            SaldoService saldoService = new SaldoService();
            OperacionSaldoResponse response = saldoService.obtenerSaldo(idUsuario);
            
            return Response.ok(response).build();
            
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
}