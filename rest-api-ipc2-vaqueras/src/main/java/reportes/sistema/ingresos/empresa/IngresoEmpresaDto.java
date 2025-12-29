/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ingresos.empresa;

/**
 *
 * @author andy
 */
public class IngresoEmpresaDto {
    private String empresa;
    private Double totalVentas;
    private Double totalComision;
    private int totalTransacciones;

    public IngresoEmpresaDto(String empresa, Double totalVentas, Double totalComision, int totalTransacciones) {
        this.empresa = empresa;
        this.totalVentas = totalVentas;
        this.totalComision = totalComision;
        this.totalTransacciones = totalTransacciones;
    }
    
    public Double getTotalIngresoSistema() { 
        return totalVentas + totalComision; 
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public Double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(Double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Double getTotalComision() {
        return totalComision;
    }

    public void setTotalComision(Double totalComision) {
        this.totalComision = totalComision;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }

    public void setTotalTransacciones(int totalTransacciones) {
        this.totalTransacciones = totalTransacciones;
    }
    
    
}
