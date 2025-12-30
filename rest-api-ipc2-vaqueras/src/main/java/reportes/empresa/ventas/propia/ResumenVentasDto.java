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
public class ResumenVentasDto {
    private BigDecimal totalBruto;
    private BigDecimal totalComision;
    private BigDecimal totalNeto;
    private int totalVideojuegos;
    private int totalVentas;
    private BigDecimal porcentajeComision;
    
    public ResumenVentasDto(BigDecimal totalBruto, BigDecimal totalComision, BigDecimal totalNeto, int totalVideojuegos, int totalVentas, BigDecimal porcentajeComision) {
        this.totalBruto = totalBruto;
        this.totalComision = totalComision;
        this.totalNeto = totalNeto;
        this.totalVideojuegos = totalVideojuegos;
        this.totalVentas = totalVentas;
        this.porcentajeComision = porcentajeComision;
    }
    
    // Método para calcular el porcentaje promedio de comisión
    public BigDecimal getPorcentajeComisionPromedio() {
        if (totalBruto.compareTo(BigDecimal.ZERO) > 0) {
            return totalComision
                .multiply(new BigDecimal("100"))
                .divide(totalBruto, 2, BigDecimal.ROUND_HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalBruto() {
        return totalBruto;
    }

    public BigDecimal getTotalComision() {
        return totalComision;
    }

    public BigDecimal getTotalNeto() {
        return totalNeto;
    }

    public int getTotalVideojuegos() {
        return totalVideojuegos;
    }

    public int getTotalVentas() {
        return totalVentas;
    }

    public BigDecimal getPorcentajeComision() {
        return porcentajeComision;
    }
    
    
}
