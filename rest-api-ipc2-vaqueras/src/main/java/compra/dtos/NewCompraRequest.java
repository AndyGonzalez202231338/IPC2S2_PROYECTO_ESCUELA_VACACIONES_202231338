/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.dtos;

import java.sql.Date;



/**
 *
 * @author andy
 */
public class NewCompraRequest {
    private int id_usuario;
    private int id_videojuego;
    private Date fecha_compra;
    
    public NewCompraRequest() {
    }

    public NewCompraRequest(int id_usuario, int id_videojuego, Date fecha_compra) {
        this.id_usuario = id_usuario;
        this.id_videojuego = id_videojuego;
        this.fecha_compra = fecha_compra;
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

    public Date getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(Date fecha_compra) {
        this.fecha_compra = fecha_compra;
    }
}