/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.dtos;

import java.sql.Date;

/**
 *
 * @author andy
 */
public class NuevaComisionRequest {
    private int id_empresa;
    private double porcentaje;
    private Date fecha_inicio;
    private Date fecha_final;
    private String tipo_comision;
    
    public NuevaComisionRequest() {}

    public NuevaComisionRequest(int id_empresa, double porcentaje, Date fecha_inicio, Date fecha_final, String tipo_comision) {
        this.id_empresa = id_empresa;
        this.porcentaje = porcentaje;
        this.fecha_inicio = fecha_inicio;
        this.fecha_final = fecha_final;
        this.tipo_comision = tipo_comision;
    }
    
    public String getTipo_comision() {
        return tipo_comision;
    }

    public void setTipo_comision(String tipo_comision) {
        this.tipo_comision = tipo_comision;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_final() {
        return fecha_final;
    }

    public void setFecha_final(Date fecha_final) {
        this.fecha_final = fecha_final;
    }
    
    
}
