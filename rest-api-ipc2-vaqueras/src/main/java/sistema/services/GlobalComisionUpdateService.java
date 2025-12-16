package sistema.services;

import db.EmpresaDB;
import db.SistemaDB;
import empresa.models.Comision;
import empresa.models.Empresa;
import java.sql.Date;
import java.util.List;

public class GlobalComisionUpdateService {
    
    private final SistemaDB sistemaDB;
    private final EmpresaDB empresaDB;
    
    public GlobalComisionUpdateService() {
        this.sistemaDB = new SistemaDB();
        this.empresaDB = new EmpresaDB();
    }
    
    /**
     * Actualiza la comisión global y ajusta las comisiones de empresas
     */
    public boolean actualizarComisionGlobal(double nuevoPorcentaje, Date fechaFinal) {
        
        try {
            
            boolean actualizadoSistema = sistemaDB.updateConfiguracion(1, String.valueOf(nuevoPorcentaje), fechaFinal);
            
            if (!actualizadoSistema) {
                System.out.println("No se pudo actualizar la configuración en sistema");
                return false;
            }
            
            System.out.println("Comisión global actualizada en tabla sistema");
            
            // Obtener todas las empresas
            List<Empresa> empresas = empresaDB.getAllEmpresas();
            System.out.println("Total de empresas a verificar: " + empresas.size());
            
            int totalActualizadas = 0;
            
            // Para cada empresa, verificar su comisión activa
            for (Empresa empresa : empresas) {
                int idEmpresa = empresa.getId_empresa();
                
                // Obtener la comisión activa actual de la empresa
                Comision comisionActual = empresaDB.getComisionActualEmpresa(idEmpresa);
                System.out.println("comisision actual de la empresa: "+comisionActual.isActiva());
                
                if (comisionActual != null && comisionActual.isActiva()) {
                    double porcentajeActual = comisionActual.getPorcentaje();
                    String tipoActual = comisionActual.getTipo_comision();
                    
                    System.out.println("\n  Empresa: " + empresa.getNombre() + " (ID: " + idEmpresa + ")");
                    System.out.println("    Comisión actual: " + porcentajeActual + "% [" + tipoActual + "]");
                    System.out.println("    Nuevo global: " + nuevoPorcentaje + "%");
                    
                    boolean necesitaActualizar = false;
                    String razon = "";
                    
                    if (tipoActual.equals("global")) {
                        // Comisión global SIEMPRE se actualiza al nuevo valor
                        necesitaActualizar = true;
                        razon = "Es comisión global, debe actualizarse";
                    } else if (tipoActual.equals("especifica")) {
                        // Comisión específica solo si es MAYOR que el nuevo global
                        if (porcentajeActual > nuevoPorcentaje) {
                            necesitaActualizar = true;
                            razon = "Comisión específica (" + porcentajeActual + "%) > nuevo global (" + nuevoPorcentaje + "%)";
                        } else {
                            razon = "Comisión específica (" + porcentajeActual + "%) <= nuevo global (" + nuevoPorcentaje + "%) - se mantiene";
                        }
                    }
                    
                        
                        
                        // Finalizar la comisión actual (hoy)
                        Date hoy = new Date(System.currentTimeMillis());
                        boolean finalizada = empresaDB.finalizarComision(comisionActual.getId_comision(), hoy);
                        
                        if (finalizada) {
                            // Crear nueva comisión con el nuevo porcentaje global
                            // Mantener el mismo tipo que tenía
                            Comision nuevaComision = new Comision(
                                idEmpresa,
                                nuevoPorcentaje,
                                hoy,  // fecha_inicio = hoy
                                comisionActual.getFecha_final(),  // mantener misma fecha_final
                                tipoActual  // mantener mismo tipo (global o especifica)
                            );
                            
                            empresaDB.crearComision(nuevaComision);
                            totalActualizadas++;
                            
                        }

                }
            }

            
            return true;
            
        } catch (Exception e) {
            
            e.printStackTrace();
            return false;
        }
    }
}