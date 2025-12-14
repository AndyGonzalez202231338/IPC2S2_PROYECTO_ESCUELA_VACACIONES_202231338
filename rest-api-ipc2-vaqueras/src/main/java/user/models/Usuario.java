/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.models;

import empresa.models.Empresa;
import java.sql.Date;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author andy
 */
import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private String correo;
    private Rol rol;
    private Empresa empresa;
    private String nombre;
    private String password;
    private Date fecha_nacimiento;
    private String pais;
    private String telefono;
    private double saldo_cartera;
    private Byte[] avatar;

    // Constructor con objetos
    public Usuario(int idUsuario, String correo, Rol rol, Empresa empresa, String nombre, 
                   String password, Date fecha_nacimiento, String pais, String telefono, 
                   double saldo_cartera, Byte[] avatar) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        this.rol = rol;
        this.empresa = empresa;
        this.nombre = nombre;
        this.password = password;
        this.fecha_nacimiento = fecha_nacimiento;
        this.pais = pais;
        this.telefono = telefono;
        this.saldo_cartera = saldo_cartera;
        this.avatar = avatar;
    }

    // Constructor con IDs (para creación)
    public Usuario(int idUsuario, String correo, int id_rol, int id_empresa, String nombre, 
                   String password, Date fecha_nacimiento, String pais, String telefono, 
                   double saldo_cartera, Byte[] avatar) {
        this.idUsuario = idUsuario;
        this.correo = correo;
        // Crear objetos básicos con los IDs
        this.rol = new Rol(id_rol, "", "");
        if (id_empresa > 0) {
            this.empresa = new Empresa(id_empresa, "", "");
        }
        this.nombre = nombre;
        this.password = password;
        this.fecha_nacimiento = fecha_nacimiento;
        this.pais = pais;
        this.telefono = telefono;
        this.saldo_cartera = saldo_cartera;
        this.avatar = avatar;
    }

    // GETTERS CRÍTICOS - estos deben funcionar con ambos constructores
    public int getId_rol() {
        if (rol != null) {
            return rol.getId_rol();
        }
        return 0; // O manejar de otra forma
    }
    
    public int getId_empresa() {
        if (empresa != null) {
            return empresa.getId_empresa();
        }
        return 0; // Para usuarios sin empresa
    }

    // Resto de getters...
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public Rol getRol() {
        return rol;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public String getPais() {
        return pais;
    }

    public String getTelefono() {
        return telefono;
    }

    public double getSaldo_cartera() {
        return saldo_cartera;
    }

    public Byte[] getAvatar() {
        return avatar;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSaldo_cartera(double saldo_cartera) {
        this.saldo_cartera = saldo_cartera;
    }

    public void setAvatar(Byte[] avatar) {
        this.avatar = avatar;
    }
    
    

    public boolean isValid() {
        return correo != null && !correo.trim().isEmpty() &&
               nombre != null && !nombre.trim().isEmpty() &&
               password != null && !password.trim().isEmpty() &&
               rol != null && rol.getId_rol() > 0;
    }
}

