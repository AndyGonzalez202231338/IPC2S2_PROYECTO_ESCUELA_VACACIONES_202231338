/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ranking.usuarios;

/**
 *
 * @author andy
 */
public class UsuarioComprasDto {
    private String nombreUsuario;
    private int totalJuegosComprados;
    private Double totalGastado;
    private String pais;

    public UsuarioComprasDto(String nombreUsuario, int totalJuegosComprados, Double totalGastado, String pais) {
        this.nombreUsuario = nombreUsuario;
        this.totalJuegosComprados = totalJuegosComprados;
        this.totalGastado = totalGastado;
        this.pais = pais;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getTotalJuegosComprados() {
        return totalJuegosComprados;
    }

    public Double getTotalGastado() {
        return totalGastado;
    }

    public String getPais() {
        return pais;
    }
    
}
