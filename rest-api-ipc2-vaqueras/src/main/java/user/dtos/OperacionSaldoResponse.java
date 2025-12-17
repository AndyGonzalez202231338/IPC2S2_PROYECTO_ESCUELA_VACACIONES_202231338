/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.dtos;

/**
 *
 * @author andy
 */
public class OperacionSaldoResponse {
    private int id_usuario;
    private double saldo_cartera;

    public OperacionSaldoResponse() {
    }

    public OperacionSaldoResponse(int id_usuario, double saldo_cartera) {
        this.id_usuario = id_usuario;
        this.saldo_cartera = saldo_cartera;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public double getSaldo_cartera() {
        return saldo_cartera;
    }

    public void setSaldo_cartera(double saldo_cartera) {
        this.saldo_cartera = saldo_cartera;
    }
}