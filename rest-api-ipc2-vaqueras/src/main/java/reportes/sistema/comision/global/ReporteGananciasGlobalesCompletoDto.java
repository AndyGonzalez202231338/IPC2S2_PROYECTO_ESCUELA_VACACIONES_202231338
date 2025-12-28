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
public class ReporteGananciasGlobalesCompletoDto {
    private List<ReporteGananciasGlobalesDto> empresas;
    private Double totalIngresoSistema;
    private Double totalComisionPlataforma;
    private Double totalVentasEmpresas;
    
    public ReporteGananciasGlobalesCompletoDto(List<ReporteGananciasGlobalesDto> empresas) {
        this.empresas = empresas;
        calcularTotales();
    }
    
    private void calcularTotales() {
        totalComisionPlataforma = 0.0;
        totalVentasEmpresas = 0.0;
        
        for (ReporteGananciasGlobalesDto empresa : empresas) {
            totalComisionPlataforma += empresa.getComisionPlataforma();
            totalVentasEmpresas += empresa.getTotalVentasEmpresa();
        }
        
        totalIngresoSistema = totalComisionPlataforma + totalVentasEmpresas;
    }

    public List<ReporteGananciasGlobalesDto> getEmpresas() {
        return empresas;
    }

    public Double getTotalIngresoSistema() {
        return totalIngresoSistema;
    }

    public Double getTotalComisionPlataforma() {
        return totalComisionPlataforma;
    }

    public Double getTotalVentasEmpresas() {
        return totalVentasEmpresas;
    }
    
    
}
