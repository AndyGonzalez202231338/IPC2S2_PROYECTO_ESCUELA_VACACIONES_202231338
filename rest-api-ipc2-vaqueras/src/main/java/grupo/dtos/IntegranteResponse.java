/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grupo.dtos;

/**
 *
 * @author andy
 */
public class IntegranteResponse {

    private int id_usuario;
    private String nombre;
    private String correo;
    private String pais;
    private String telefono;
    private boolean es_creador;

    public IntegranteResponse() {
    }

    public IntegranteResponse(int id_usuario, String nombre, String correo, boolean es_creador) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.correo = correo;
        this.es_creador = es_creador;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEs_creador() {
        return es_creador;
    }

    public void setEs_creador(boolean es_creador) {
        this.es_creador = es_creador;
    }
    
    
}
