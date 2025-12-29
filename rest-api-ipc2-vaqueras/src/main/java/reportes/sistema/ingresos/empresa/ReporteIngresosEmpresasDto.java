/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ingresos.empresa;

import java.util.List;
/**
 *
 * @author andy
 */
public class ReporteIngresosEmpresasDto {
    private List<IngresoEmpresaDto> empresas;
    private Double totalVentasTodasEmpresas;
    private Double totalComisionTodasEmpresas;
    private Double totalIngresoSistema;
    private int totalTransacciones;
    
    public ReporteIngresosEmpresasDto(List<IngresoEmpresaDto> empresas) {
        this.empresas = empresas;
        calcularTotales();
    }
    
    private void calcularTotales() {
        totalVentasTodasEmpresas = 0.0;
        totalComisionTodasEmpresas = 0.0;
        totalTransacciones = 0;
        
        for (IngresoEmpresaDto empresa : empresas) {
            totalVentasTodasEmpresas += empresa.getTotalVentas();
            totalComisionTodasEmpresas += empresa.getTotalComision();
            totalTransacciones += empresa.getTotalTransacciones();
        }
        
        totalIngresoSistema = totalVentasTodasEmpresas + totalComisionTodasEmpresas;
    }

    public List<IngresoEmpresaDto> getEmpresas() {
        return empresas;
    }

    public Double getTotalVentasTodasEmpresas() {
        return totalVentasTodasEmpresas;
    }

    public Double getTotalComisionTodasEmpresas() {
        return totalComisionTodasEmpresas;
    }

    public Double getTotalIngresoSistema() {
        return totalIngresoSistema;
    }

    public int getTotalTransacciones() {
        return totalTransacciones;
    }
    
    
}
