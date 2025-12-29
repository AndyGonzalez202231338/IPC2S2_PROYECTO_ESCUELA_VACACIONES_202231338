/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package reportes.sistema.ranking.usuarios;

import java.util.List;

/**
 *
 * @author andy
 */
public class ReporteRankingUsuariosDto {
    private List<UsuarioComprasDto> topUsuariosCompras;
    private List<UsuarioResenasDto> topUsuariosResenas;
    
    public ReporteRankingUsuariosDto(List<UsuarioComprasDto> topUsuariosCompras, List<UsuarioResenasDto> topUsuariosResenas) {
        this.topUsuariosCompras = topUsuariosCompras;
        this.topUsuariosResenas = topUsuariosResenas;
    }

    public List<UsuarioComprasDto> getTopUsuariosCompras() {
        return topUsuariosCompras;
    }

    public List<UsuarioResenasDto> getTopUsuariosResenas() {
        return topUsuariosResenas;
    }
    
}
