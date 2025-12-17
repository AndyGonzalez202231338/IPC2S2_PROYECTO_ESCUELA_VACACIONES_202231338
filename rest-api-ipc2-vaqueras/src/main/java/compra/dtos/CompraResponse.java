/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.dtos;

import compra.models.Compra;
import java.math.BigDecimal;
import java.util.Date;
import user.dtos.UserResponse;
import videojuego.dtos.VideojuegoResponse;

/**
 *
 * @author andy
 */
public class CompraResponse {

    private int id_compra;
    private int id_usuario;
    private int id_videojuego;
    private double monto_pago;
    private Date fecha_compra;
    private double comision_aplicada;

    private UserResponse usuario;
    private VideojuegoResponse videojuego;

    public CompraResponse(Compra compra) {
        this.id_compra = compra.getId_compra();
        this.id_usuario = compra.getId_usuario();
        this.id_videojuego = compra.getId_videojuego();
        this.monto_pago = compra.getMonto_pago();
        this.fecha_compra = compra.getFecha_compra();
        this.comision_aplicada = compra.getComision_aplicada();

        if (compra.getUsuario() != null) {
            this.usuario = new UserResponse(compra.getUsuario());

        }

        if (compra.getVideojuego() != null) {
            this.videojuego = new VideojuegoResponse(compra.getVideojuego());

        }
    }

    public int getId_compra() {
        return id_compra;
    }

    public void setId_compra(int id_compra) {
        this.id_compra = id_compra;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public double getMonto_pago() {
        return monto_pago;
    }

    public void setMonto_pago(double monto_pago) {
        this.monto_pago = monto_pago;
    }

    public Date getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(Date fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public double getComision_aplicada() {
        return comision_aplicada;
    }

    public void setComision_aplicada(double comision_aplicada) {
        this.comision_aplicada = comision_aplicada;
    }

    public UserResponse getUsuario() {
        return usuario;
    }

    public void setUsuario(UserResponse usuario) {
        this.usuario = usuario;
    }

    public VideojuegoResponse getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(VideojuegoResponse videojuego) {
        this.videojuego = videojuego;
    }

}
