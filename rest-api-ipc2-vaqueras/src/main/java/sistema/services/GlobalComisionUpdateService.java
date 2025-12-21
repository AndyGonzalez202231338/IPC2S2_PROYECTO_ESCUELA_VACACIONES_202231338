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

            
            List<Empresa> empresas = empresaDB.getAllEmpresas();
            System.out.println("Total de empresas a verificar: " + empresas.size());

            int totalActualizadas = 0;

            // Para cada empresa, verificar su comisión activa
            for (Empresa empresa : empresas) {

                Comision actual = empresaDB.getComisionActualEmpresa(empresa.getId_empresa());

                if (actual == null || !actual.isActiva()) {
                    continue;
                }

                boolean actualizar = false;
                double nuevoValorEmpresa = actual.getPorcentaje();
                String nuevoTipo = actual.getTipo_comision();

                // CASO 1: GLOBAL
                if (actual.getTipo_comision().equals("global")) {
                    actualizar = true;
                    nuevoValorEmpresa = nuevoPorcentaje;
                    nuevoTipo = "global";
                } // CASO 2: ESPECIFICA
                else if (actual.getTipo_comision().equals("especifica")) {

                    if (nuevoPorcentaje < actual.getPorcentaje()) {
                        actualizar = true;
                        nuevoValorEmpresa = nuevoPorcentaje;
                        nuevoTipo = "global";
                    }
                    
                }

                if (actualizar) {

                    Date hoy = new Date(System.currentTimeMillis());

                    empresaDB.finalizarComisionesActivasEmpresa(empresa.getId_empresa(), hoy);

                    Comision nueva = new Comision(
                            empresa.getId_empresa(),
                            nuevoValorEmpresa,
                            hoy,
                            null, 
                            nuevoTipo
                    );

                    empresaDB.crearComision(nueva);
                }
            }

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }
}
