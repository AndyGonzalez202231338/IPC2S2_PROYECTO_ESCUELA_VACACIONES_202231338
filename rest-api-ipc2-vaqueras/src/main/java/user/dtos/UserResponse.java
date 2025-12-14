/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import empresa.models.Empresa;
import java.sql.Date;
import java.time.LocalDateTime;
import user.models.Rol;
import user.models.Usuario;


/**
 *
 * @author andy
 */
public class UserResponse {
    private int idUsuario;
    private String correo;
    private Rol rol;
    private Empresa empresa;
    private String nombre;
    private String password;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fecha_nacimiento;
    private String pais;
    private String telefono;
    private double saldo_cartera;
    private Byte avatar;
    
    
    public UserResponse(Usuario user){
        this.idUsuario = user.getIdUsuario();
        this.correo = user.getCorreo();
        this.rol = user.getRol();
        this.empresa = user.getEmpresa();
        this.nombre = user.getNombre();
        this.password = user.getPassword();
        this.fecha_nacimiento = user.getFecha_nacimiento();
        this.pais = user.getPais();
        this.telefono = user.getTelefono();
        this.saldo_cartera = user.getSaldo_cartera();
        
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
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

    public Date getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(Date fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
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

    public Byte getAvatar() {
        return avatar;
    }

    public void setAvatar(Byte avatar) {
        this.avatar = avatar;
    }
    
}
