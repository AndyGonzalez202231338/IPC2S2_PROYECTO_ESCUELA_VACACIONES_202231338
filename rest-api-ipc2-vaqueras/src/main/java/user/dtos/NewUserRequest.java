package user.dtos;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.sql.Date;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author andy
 */
public class NewUserRequest {
    private String correo;
    private int id_rol;
    private Integer id_empresa;
    private String nombre;
    private String password;
    private String fecha_nacimiento;
    
    private String pais;
    private String telefono;
    private double saldo_cartera;
    private Byte[] avatar;

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getId_rol() {
        return id_rol;
    }

    public void setId_rol(int id_rol) {
        this.id_rol = id_rol;
    }

    public Integer getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(Integer id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    // Método para obtener como java.sql.Date
    public Date getFechaNacimientoAsDate() {
        if (fecha_nacimiento == null || fecha_nacimiento.isEmpty() || fecha_nacimiento.equals("")) {
            return null;
        }
        try {
            return Date.valueOf(fecha_nacimiento);
        } catch (IllegalArgumentException e) {
            System.out.println("Fecha inválida: " + fecha_nacimiento);
            return null;
        }
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getSaldo_cartera() {
        return saldo_cartera;
    }

    public void setSaldo_cartera(double saldo_cartera) {
        this.saldo_cartera = saldo_cartera;
    }

    public Byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(Byte[] avatar) {
        this.avatar = avatar;
    }
}