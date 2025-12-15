/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;

/**
 *
 * @author andy
 */
public class UpdateConfiguracionRequest {
    private String valor;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fecha_final;
    
    
    public UpdateConfiguracionRequest() {}
    
    
    public UpdateConfiguracionRequest(String valor) {
        this.valor = valor;
    }
    
    // Constructor completo
    public UpdateConfiguracionRequest(String valor, Date fecha_final) {
        this.valor = valor;
        this.fecha_final = fecha_final;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Date getFecha_final() {
        return fecha_final;
    }

    public void setFecha_final(Date fecha_final) {
        this.fecha_final = fecha_final;
    }
    
    
}
