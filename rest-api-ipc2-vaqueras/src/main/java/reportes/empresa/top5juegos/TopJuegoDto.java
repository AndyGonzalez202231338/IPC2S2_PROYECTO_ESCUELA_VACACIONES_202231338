/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.empresa.top5juegos;

public class TopJuegoDto {
    private int posicion;
    private String tituloJuego;
    private String empresaDesarrolladora;
    private int cantidadVentas;

    public TopJuegoDto(int posicion, String tituloJuego, String empresaDesarrolladora, int cantidadVentas) {
        this.posicion = posicion;
        this.tituloJuego = tituloJuego;
        this.empresaDesarrolladora = empresaDesarrolladora;
        this.cantidadVentas = cantidadVentas;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getTituloJuego() {
        return tituloJuego;
    }

    public void setTituloJuego(String tituloJuego) {
        this.tituloJuego = tituloJuego;
    }

    public String getEmpresaDesarrolladora() {
        return empresaDesarrolladora;
    }

    public void setEmpresaDesarrolladora(String empresaDesarrolladora) {
        this.empresaDesarrolladora = empresaDesarrolladora;
    }

    public int getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(int cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }
}
