/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.models;

import java.sql.Date;

/**
 *
 * @author andy
 */
public class ConfiguracionSistema {
    private int id_configuracion;
    private String configuracion;
    private String valor;
    private String descripcion;
    private Date fecha_inicio;
    private Date fecha_final;

    public ConfiguracionSistema() {
    }

    public ConfiguracionSistema(int id_configuracion, String configuracion, String valor, String descripcion, Date fecha_inicio, Date fecha_final) {
        this.id_configuracion = id_configuracion;
        this.configuracion = configuracion;
        this.valor = valor;
        this.descripcion = descripcion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_final = fecha_final;
    }

    public ConfiguracionSistema(int id_configuracion, String configuracion, String valor, String descripcion) {
        this.id_configuracion = id_configuracion;
        this.configuracion = configuracion;
        this.valor = valor;
        this.descripcion = descripcion;
    }
    
    

    public int getId_configuracion() {
        return id_configuracion;
    }

    public void setId_configuracion(int id_configuracion) {
        this.id_configuracion = id_configuracion;
    }

    public String getConfiguracion() {
        return configuracion;
    }

    public void setConfiguracion(String configuracion) {
        this.configuracion = configuracion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
