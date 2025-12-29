/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ventas.calidad;

import java.util.List;

/**
 *
 * @author andy
 */
public class ReporteTopVentasCalidadDto {

    private List<VideojuegoVentasDto> topVentas;
    private List<VideojuegoCalificacionDto> topCalificaciones;
    private String clasificacionFiltro;
    private String categoriaFiltro;

    public ReporteTopVentasCalidadDto(List<VideojuegoVentasDto> topVentas, List<VideojuegoCalificacionDto> topCalificaciones,String clasificacionFiltro, String categoriaFiltro) {
        this.topVentas = topVentas;
        this.topCalificaciones = topCalificaciones;
        this.clasificacionFiltro = clasificacionFiltro;
        this.categoriaFiltro = categoriaFiltro;
    }

    public List<VideojuegoVentasDto> getTopVentas() {
        return topVentas;
    }

    public List<VideojuegoCalificacionDto> getTopCalificaciones() {
        return topCalificaciones;
    }

    public String getClasificacionFiltro() {
        return clasificacionFiltro;
    }

    public String getCategoriaFiltro() {
        return categoriaFiltro;
    }
    
    
}
