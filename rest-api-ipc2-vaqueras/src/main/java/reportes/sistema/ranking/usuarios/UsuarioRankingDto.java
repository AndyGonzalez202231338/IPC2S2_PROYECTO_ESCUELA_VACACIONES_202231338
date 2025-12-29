package reportes.sistema.ranking.usuarios;

public class UsuarioRankingDto {
    private String nombreUsuario;
    private Integer totalJuegosComprados;
    private Double totalGastado;
    private String pais;
    private Integer totalCalificaciones;  // Cambiado de totalResenasEscritas
    private String tipo; // "COMPRAS" o "CALIFICACIONES"  // Cambiado
    
    // Constructor para usuarios de COMPRAS
    public UsuarioRankingDto(String nombreUsuario, int totalJuegosComprados, 
                            Double totalGastado, String pais) {
        this.nombreUsuario = nombreUsuario;
        this.totalJuegosComprados = totalJuegosComprados;
        this.totalGastado = totalGastado;
        this.pais = pais;
        this.totalCalificaciones = 0;
        this.tipo = "COMPRAS";
    }
    
    // Constructor para usuarios de CALIFICACIONES
    public UsuarioRankingDto(String nombreUsuario, int totalCalificaciones, String pais) {
        this.nombreUsuario = nombreUsuario;
        this.totalCalificaciones = totalCalificaciones;
        this.pais = pais;
        this.totalJuegosComprados = 0;
        this.totalGastado = 0.0;
        this.tipo = "CALIFICACIONES";  // Cambiado
    }

    // Getters... (actualiza el getter para totalCalificaciones)
        public Integer getTotalResenasEscritas() {
        return totalCalificaciones;
    }


    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Integer getTotalJuegosComprados() {
        return totalJuegosComprados;
    }

    public Double getTotalGastado() {
        return totalGastado;
    }

    public String getPais() {
        return pais;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getTotalCalificaciones() {
        return totalCalificaciones;
    }
    
    
    
    
}