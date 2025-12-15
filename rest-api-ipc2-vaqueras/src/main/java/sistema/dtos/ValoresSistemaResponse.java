/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author andy
 */
public class ValoresSistemaResponse {
    @JsonProperty("comision_global")
    private double comisionGlobal;
    
    @JsonProperty("edad_minima")
    private int edadMinima;
    
    @JsonProperty("max_miembros_grupo")
    private int maxMiembrosGrupo;
    
    public ValoresSistemaResponse(double comisionGlobal, int edadMinima, int maxMiembrosGrupo) {
        this.comisionGlobal = comisionGlobal;
        this.edadMinima = edadMinima;
        this.maxMiembrosGrupo = maxMiembrosGrupo;
    }
    
     public void setComisionGlobal(double comisionGlobal) {
        this.comisionGlobal = comisionGlobal;
    }
    
    public void setEdadMinima(int edadMinima) {
        this.edadMinima = edadMinima;
    }
    
    public void setMaxMiembrosGrupo(int maxMiembrosGrupo) {
        this.maxMiembrosGrupo = maxMiembrosGrupo;
    }

    public double getComisionGlobal() {
        return comisionGlobal;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    public int getMaxMiembrosGrupo() {
        return maxMiembrosGrupo;
    }
    
    
}
