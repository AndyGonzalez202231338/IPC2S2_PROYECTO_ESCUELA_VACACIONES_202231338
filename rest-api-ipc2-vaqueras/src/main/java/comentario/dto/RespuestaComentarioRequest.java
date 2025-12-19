/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comentario.dto;

/**
 *
 * @author andy
 */
public class RespuestaComentarioRequest {
    private int id_comentario_padre;
    private int id_usuario;
    private String comentario;

    public int getId_comentario_padre() {
        return id_comentario_padre;
    }

    public void setId_comentario_padre(int id_comentario_padre) {
        this.id_comentario_padre = id_comentario_padre;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    
    
}
