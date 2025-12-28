/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.comision.global;

import java.util.List;
/**
 *
 * @author andy
 */
public class ReporteGananciasGlobalesDto {
    private String empresa;
    private Double totalVentasEmpresa;
    private Double comisionPlataforma;
    
    public ReporteGananciasGlobalesDto(String empresa, Double totalVentasEmpresa, Double comisionPlataforma) {
        this.empresa = empresa;
        this.totalVentasEmpresa = totalVentasEmpresa;
        this.comisionPlataforma = comisionPlataforma;
    }
    
    public Double getTotalIngresoSistema() { 
        return totalVentasEmpresa + comisionPlataforma; 
    }

    public String getEmpresa() {
        return empresa;
    }

    public Double getTotalVentasEmpresa() {
        return totalVentasEmpresa;
    }

    public Double getComisionPlataforma() {
        return comisionPlataforma;
    }
    
}
