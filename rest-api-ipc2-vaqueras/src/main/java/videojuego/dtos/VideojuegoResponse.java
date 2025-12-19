/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.dtos;

import categoria.dtos.CategoriaResponse;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class VideojuegoResponse {
    private int id_videojuego;
    private int id_empresa;
    private String titulo;
    private String descripcion;
    private String recursos_minimos;
    private BigDecimal precio;
    private String clasificacion_edad;
    private Date fecha_lanzamiento;
    private boolean comentarios_bloqueados;
    private List<CategoriaResponse> categorias;
    
        public VideojuegoResponse(Videojuego videojuego) {
        this.id_videojuego = videojuego.getId_videojuego();
        this.id_empresa = videojuego.getId_empresa();
        this.titulo = videojuego.getTitulo();
        this.descripcion = videojuego.getDescripcion();
        this.recursos_minimos = videojuego.getRecursos_minimos();
        this.precio = videojuego.getPrecio();
        this.clasificacion_edad = videojuego.getClasificacion_edad();
        this.fecha_lanzamiento = videojuego.getFecha_lanzamiento();
        this.comentarios_bloqueados = videojuego.isComentarios_bloqueados();
        
        // Convertir categorías a DTOs si exiten categorias relacionadas al videojuego
        if (videojuego.getCategorias() != null) {
            this.categorias = videojuego.getCategorias().stream()
                    .map(CategoriaResponse::new)
                    .toList();
        }
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getRecursos_minimos() {
        return recursos_minimos;
    }

    public void setRecursos_minimos(String recursos_minimos) {
        this.recursos_minimos = recursos_minimos;
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

    public List<CategoriaResponse> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<CategoriaResponse> categorias) {
        this.categorias = categorias;
    }
    
        public boolean isComentarios_bloqueados() {
        return comentarios_bloqueados;
    }

    public void setComentarios_bloqueados(boolean comentarios_bloqueados) {
        this.comentarios_bloqueados = comentarios_bloqueados;
    }
        
    
}
