/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.models;

import java.util.Date;
import user.models.Usuario;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class Compra {
    private int id_compra;
    private int id_usuario;
    private int id_videojuego;
    private double monto_pago;
    private Date fecha_compra;
    private double comision_aplicada;
    
    private Usuario usuario;
    private Videojuego videojuego;

    public Compra() {
    }

    public Compra(int id_compra, int id_usuario, int id_videojuego, 
                  double monto_pago, Date fecha_compra, double comision_aplicada) {
        this.id_compra = id_compra;
        this.id_usuario = id_usuario;
        this.id_videojuego = id_videojuego;
        this.monto_pago = monto_pago;
        this.fecha_compra = fecha_compra;
        this.comision_aplicada = comision_aplicada;
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
    
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Videojuego getVideojuego() {
        return videojuego;
    }

    public void setVideojuego(Videojuego videojuego) {
        this.videojuego = videojuego;
    }
}