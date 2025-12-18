/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.dtos;

import compra.models.Compra;
import java.sql.Date;

/**
 *
 * @author andy
 */
public class CompraMiniResponse {
    private int id_compra;
    private double monto_pago;
    private Date fecha_compra;
    private double comision_aplicada;
    private int id_usuario;
    
    public CompraMiniResponse() {}
    
    public CompraMiniResponse(Compra compra) {
        this.id_compra = compra.getId_compra();
        this.monto_pago = compra.getMonto_pago();
        this.fecha_compra = (Date) compra.getFecha_compra();
        this.comision_aplicada = compra.getComision_aplicada();
        this.id_usuario = compra.getId_usuario();
    }  

    public int getId_compra() {
        return id_compra;
    }

    public void setId_compra(int id_compra) {
        this.id_compra = id_compra;
    }

    public double getMonto_pago() {
        return monto_pago;
    }

    public void setMonto_pago(double monto_pago) {
        this.monto_pago = monto_pago;
    }

    public Date getFecha_compra() {
        return fecha_compra;
    }

    public void setFecha_compra(Date fecha_compra) {
        this.fecha_compra = fecha_compra;
    }

    public double getComision_aplicada() {
        return comision_aplicada;
    }

    public void setComision_aplicada(double comision_aplicada) {
        this.comision_aplicada = comision_aplicada;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }
}
