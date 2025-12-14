package user.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Rol {
    private int id_rol;
    private String nombre;
    private String descripcion;

    // Constructor por defecto necesario para Jackson
    public Rol() {
    }

    // Constructor con parámetros
    @JsonCreator
    public Rol(@JsonProperty("id_rol") int idRol, 
                @JsonProperty("nombre") String nombreRol, 
                @JsonProperty("descripcion") String descripcion) {
        this.id_rol = idRol;
        this.nombre = nombreRol;
        this.descripcion = descripcion;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}