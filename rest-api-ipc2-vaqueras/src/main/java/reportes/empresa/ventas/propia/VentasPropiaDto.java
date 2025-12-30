/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.ventas.propia;

import java.math.BigDecimal;

/**
 *
 * @author andy
 */
public class VentasPropiaDto {
    private String tituloVideojuego;
    private int cantidadVentas;
    private BigDecimal montoBruto;
    private BigDecimal comisionPlataforma;
    private BigDecimal ingresoNeto;
    private BigDecimal porcentajeComision;

    public VentasPropiaDto(String tituloVideojuego, int cantidadVentas, BigDecimal montoBruto, BigDecimal comisionPlataforma, BigDecimal porcentajeComision) {
        this.tituloVideojuego = tituloVideojuego;
        this.cantidadVentas = cantidadVentas;
        this.montoBruto = montoBruto;
        this.comisionPlataforma = comisionPlataforma;
        this.ingresoNeto = montoBruto.subtract(comisionPlataforma);
        this.porcentajeComision = porcentajeComision;
    }

    public String getTituloVideojuego() {
        return tituloVideojuego;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public BigDecimal getMontoBruto() {
        return montoBruto;
    }

    public BigDecimal getComisionPlataforma() {
        return comisionPlataforma;
    }

    public BigDecimal getIngresoNeto() {
        return ingresoNeto;
    }

    public BigDecimal getPorcentajeComision() {
        return porcentajeComision;
    }
    
    
}
