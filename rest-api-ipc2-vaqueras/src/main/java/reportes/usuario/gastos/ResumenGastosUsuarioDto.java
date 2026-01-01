/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.gastos;

/**
 *
 * @author andy
 */
public class ResumenGastosUsuarioDto {

    private String nombreUsuario;
    private double totalGastado;

    public ResumenGastosUsuarioDto(String nombreUsuario, double totalGastado) {
        this.nombreUsuario = nombreUsuario;
        this.totalGastado = totalGastado;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public double getTotalGastado() {
        return totalGastado;
    }
}

