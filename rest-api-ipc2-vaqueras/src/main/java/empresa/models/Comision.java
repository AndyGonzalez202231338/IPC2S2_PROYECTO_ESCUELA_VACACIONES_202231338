package empresa.models;

import java.sql.Date;

public class Comision {
    private int id_comision;
    private int id_empresa;
    private double porcentaje;
    private Date fecha_inicio;
    private Date fecha_final;
    private String tipo_comision;
    
    public Comision() {}

    public Comision(int id_comision, int id_empresa, double porcentaje, Date fecha_inicio, Date fecha_final, String tipo_comision) {
        this.id_comision = id_comision;
        this.id_empresa = id_empresa;
        this.porcentaje = porcentaje;
        this.fecha_inicio = fecha_inicio;
        this.fecha_final = fecha_final;
        this.tipo_comision = tipo_comision;
    }
    
    public Comision(int id_empresa, double porcentaje, Date fecha_inicio, String tipo_comision) {
        this(0, id_empresa, porcentaje, fecha_inicio, null, tipo_comision);
    }

    public Comision(int id_empresa, double porcentaje, Date fecha_inicio, Date fecha_final, String tipo_comision) {
        this.id_empresa = id_empresa;
        this.porcentaje = porcentaje;
        this.fecha_inicio = fecha_inicio;
        this.fecha_final = fecha_final;
        this.tipo_comision = tipo_comision;
    }

    public int getId_comision() {
        return id_comision;
    }

    public void setId_comision(int id_comision) {
        this.id_comision = id_comision;
    }

    public int getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(int id_empresa) {
        this.id_empresa = id_empresa;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_final() {
        return fecha_final;
    }

    public void setFecha_final(Date fecha_final) {
        this.fecha_final = fecha_final;
    }

    public String getTipo_comision() {
        return tipo_comision;
    }

    public void setTipo_comision(String tipo_comision) {
        this.tipo_comision = tipo_comision;
    }
    
    public boolean isActiva() {
        if (fecha_final == null) {
            return true;
        }
        
        long ahoraMillis = System.currentTimeMillis();
        Date ahora = new Date(ahoraMillis);
        return !fecha_final.before(ahora);
    }
}