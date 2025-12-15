/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.dtos;

import sistema.models.ConfiguracionSistema;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author andy
 */
public class ConfiguracionResponse {

    private int id_configuracion;
    private String configuracion;
    private String valor;
    private String descripcion;
    private String fecha_inicio;
    private String fecha_final;

    public ConfiguracionResponse() {
    }

    public ConfiguracionResponse(ConfiguracionSistema config) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        this.id_configuracion = config.getId_configuracion();
        this.configuracion = config.getConfiguracion();
        this.valor = config.getValor();
        this.descripcion = config.getDescripcion();

        if (config.getFecha_inicio() != null) {
            this.fecha_inicio = sdf.format(config.getFecha_inicio());
        }

        if (config.getFecha_final() != null) {
            this.fecha_final = sdf.format(config.getFecha_final());
        }
    }

        // Método para verificar si está activa (campo calculado en respuesta también)
    public boolean isActiva() {
        if (fecha_final == null) {
            return true;
        }
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date fechaFinal = sdf.parse(fecha_final);
            Date ahora = new Date();
            return fechaFinal.after(ahora) || fechaFinal.equals(ahora);
        } catch (Exception e) {
            return true;
        }
    }

    public int getId_configuracion() {
        return id_configuracion;
    }

    public String getConfiguracion() {
        return configuracion;
    }

    public String getValor() {
        return valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha_inicio() {
        return fecha_inicio;
    }

    public String getFecha_final() {
        return fecha_final;
    }
    
    
}
