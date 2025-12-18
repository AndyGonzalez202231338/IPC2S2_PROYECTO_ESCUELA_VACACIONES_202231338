/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.dtos;

import java.math.BigDecimal;
import java.sql.Date;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class VideojuegoMiniResponse {
    private int id_videojuego;
    private String titulo;
    private BigDecimal precio;
    private String clasificacion_edad;
    private Date fecha_lanzamiento;
    private int id_empresa; 
    
    public VideojuegoMiniResponse() {}
    
    public VideojuegoMiniResponse(Videojuego videojuego) {
        this.id_videojuego = videojuego.getId_videojuego();
        this.id_empresa = videojuego.getId_empresa();
        this.titulo = videojuego.getTitulo();
        this.precio = videojuego.getPrecio();
        this.clasificacion_edad = videojuego.getClasificacion_edad();
        this.fecha_lanzamiento = videojuego.getFecha_lanzamiento(); 
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getClasificacion_edad() {
        return clasificacion_edad;
    }

    public void setClasificacion_edad(String clasificacion_edad) {
        this.clasificacion_edad = clasificacion_edad;
    }

    public Date getFecha_lanzamiento() {
        return fecha_lanzamiento;
    }

    public void setFecha_lanzamiento(Date fecha_lanzamiento) {
        this.fecha_lanzamiento = fecha_lanzamiento;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }
    
    
}
