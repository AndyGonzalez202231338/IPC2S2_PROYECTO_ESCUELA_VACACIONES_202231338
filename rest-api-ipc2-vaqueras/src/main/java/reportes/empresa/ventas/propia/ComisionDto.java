package reportes.empresa.ventas.propia;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ComisionDto {
    private int idComision;
    private int idEmpresa;
    private BigDecimal porcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaFinal;
    private String tipoComision;
    
    // Constructor con java.sql.Date
    public ComisionDto(int idComision, int idEmpresa, BigDecimal porcentaje,
                      java.sql.Date fechaInicio, java.sql.Date fechaFinal, String tipoComision) {
        this.idComision = idComision;
        this.idEmpresa = idEmpresa;
        this.porcentaje = porcentaje;
        this.fechaInicio = fechaInicio != null ? fechaInicio.toLocalDate() : null;
        this.fechaFinal = fechaFinal != null ? fechaFinal.toLocalDate() : null;
        this.tipoComision = tipoComision;
    }
    
    // Getters
    public int getIdComision() { return idComision; }
    public int getIdEmpresa() { return idEmpresa; }
    public BigDecimal getPorcentaje() { return porcentaje; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFinal() { return fechaFinal; }
    public String getTipoComision() { return tipoComision; }
    
    // Método para formatear la fecha para mostrar
    public String getFechaInicioFormateada() {
        return fechaInicio != null ? fechaInicio.toString() : "No especificada";
    }
    
    public String getFechaFinalFormateada() {
        return fechaFinal != null ? fechaFinal.toString() : "Vigente";
    }
}