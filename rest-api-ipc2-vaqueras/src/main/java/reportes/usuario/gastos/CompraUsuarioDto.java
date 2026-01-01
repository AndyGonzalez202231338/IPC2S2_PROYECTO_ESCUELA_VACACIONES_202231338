/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.usuario.gastos;

import java.time.LocalDate;
/**
 *
 * @author andy
 */
public class CompraUsuarioDto {

    private LocalDate fechaCompra;
    private String tituloJuego;
    private double montoPagado;

    public CompraUsuarioDto(LocalDate fechaCompra, String tituloJuego, double montoPagado) {
        this.fechaCompra = fechaCompra;
        this.tituloJuego = tituloJuego;
        this.montoPagado = montoPagado;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public double getMontoPagado() {
        return montoPagado;
    }
}

