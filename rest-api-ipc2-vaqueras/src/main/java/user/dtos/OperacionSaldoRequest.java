/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.dtos;

/**
 *
 * @author andy
 */
public class OperacionSaldoRequest {
    private int id_usuario;
    private double monto;

    public OperacionSaldoRequest() {
    }

    public OperacionSaldoRequest(int id_usuario, double monto) {
        this.id_usuario = id_usuario;
        this.monto = monto;
    }

    
    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}