/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ranking.usuarios;

/**
 *
 * @author andy
 */
public class UsuarioResenasDto {
    private String nombreUsuario;
    private int totalResenasEscritas;
    private String pais;

    public UsuarioResenasDto(String nombreUsuario, int totalResenasEscritas, String pais) {
        this.nombreUsuario = nombreUsuario;
        this.totalResenasEscritas = totalResenasEscritas;
        this.pais = pais;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getTotalResenasEscritas() {
        return totalResenasEscritas;
    }

    public String getPais() {
        return pais;
    }
    
}
