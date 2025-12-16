package db;

import conexion.DBConnectionSingleton;
import empresa.models.Comision;
import empresa.models.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDB {

    private static final String CREAR_EMPRESA_CON_COMISION_QUERY
            = "INSERT INTO empresa (nombre, descripcion) VALUES (?, ?)";

    private static final String CREAR_COMISION_INICIAL_QUERY
            = "INSERT INTO comision (id_empresa, porcentaje, fecha_inicio, tipo_comision) "
            + "VALUES (?, (SELECT CAST(valor AS DECIMAL(10,2)) FROM sistema "
            + "           WHERE configuracion = 'COMISION_GLOBAL' "
            + "           AND (fecha_final IS NULL OR fecha_final >= CURDATE()) "
            + "           LIMIT 1), ?, 'global')";

    private static final String OBTENER_EMPRESA_POR_ID_QUERY
            = "SELECT id_empresa, nombre, descripcion FROM empresa WHERE id_empresa = ?";

    private static final String OBTENER_TODAS_EMPRESAS_QUERY
            = "SELECT id_empresa, nombre, descripcion FROM empresa ORDER BY nombre";

    private static final String EXISTE_EMPRESA_POR_NOMBRE_QUERY
            = "SELECT COUNT(*) as count FROM empresa WHERE nombre = ?";

    private static final String ACTUALIZAR_EMPRESA_QUERY
            = "UPDATE empresa SET nombre = ?, descripcion = ? WHERE id_empresa = ?";

    private static final String EXISTE_EMPRESA_POR_NOMBRE_EXCLUYENDO_ID_QUERY
            = "SELECT COUNT(*) as count FROM empresa WHERE nombre = ? AND id_empresa != ?";

    //SECCION PARA COMISIONES
    private static final String CREAR_COMISION_QUERY
            = "INSERT INTO comision (id_empresa, porcentaje, fecha_inicio, fecha_final, tipo_comision) VALUES (?, ?, ?, ?, ?)";

    private static final String OBTENER_COMISION_ACTUAL_EMPRESA_QUERY
            = "SELECT id_comision, id_empresa, porcentaje, fecha_inicio, fecha_final, tipo_comision "
            + "FROM comision WHERE id_empresa = ? AND "
            + "(fecha_final IS NULL OR fecha_final >= CURDATE()) "
            + "ORDER BY fecha_inicio DESC LIMIT 1";

    private static final String OBTENER_TODAS_COMISIONES_EMPRESA_QUERY
            = "SELECT id_comision, id_empresa, porcentaje, fecha_inicio, fecha_final, tipo_comision "
            + "FROM comision WHERE id_empresa = ? ORDER BY fecha_inicio DESC";

    private static final String ACTUALIZAR_COMISION_GLOBAL_QUERY
            = "UPDATE comision SET porcentaje = ? "
            + "WHERE id_empresa = ? AND porcentaje > ? "
            + "AND (fecha_final IS NULL OR fecha_final >= CURDATE())";

    private static final String CREAR_COMISION_CON_FECHA_QUERY
            = "INSERT INTO comision (id_empresa, porcentaje, fecha_inicio, fecha_final) "
            + "VALUES (?, ?, ?, ?)";

    private static final String FINALIZAR_COMISION_QUERY
            = "UPDATE comision SET fecha_final = ? WHERE id_comision = ?";

    private Empresa mapResultSetToEmpresa(ResultSet resultSet) throws SQLException {
        return new Empresa(
                resultSet.getInt("id_empresa"),
                resultSet.getString("nombre"),
                resultSet.getString("descripcion")
        );
    }

    public Empresa createEmpresa(Empresa empresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try {
            //Crear empresa
            int empresaId = 0;
            try (PreparedStatement insertEmpresa = connection.prepareStatement(
                    CREAR_EMPRESA_CON_COMISION_QUERY, Statement.RETURN_GENERATED_KEYS)) {

                insertEmpresa.setString(1, empresa.getNombre());
                insertEmpresa.setString(2, empresa.getDescripcion());
                insertEmpresa.executeUpdate();

                try (ResultSet generatedKeys = insertEmpresa.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        empresaId = generatedKeys.getInt(1);
                        empresa.setId_empresa(empresaId);
                    }
                }
            }

            //Crear comisión usando el valor global con tipo "global" al crear empresa
            try (PreparedStatement insertComision = connection.prepareStatement(
                    CREAR_COMISION_INICIAL_QUERY)) {

                insertComision.setInt(1, empresaId);
                insertComision.setDate(2, new java.sql.Date(new java.util.Date().getTime()));
                insertComision.executeUpdate();
            }

            System.out.println("Empresa " + empresaId + " creada con comisión global (tipo: global)");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear empresa: " + e.getMessage(), e);
        }

        return empresa;
    }

    public Empresa getEmpresaById(int idEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_EMPRESA_POR_ID_QUERY)) {
            query.setInt(1, idEmpresa);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToEmpresa(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene todas las empresas de la base de datos
     *
     * @return Lista de todas las empresas
     */
    public List<Empresa> getAllEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_EMPRESAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Empresa empresa = mapResultSetToEmpresa(resultSet);
                empresas.add(empresa);
            }

            System.out.println("Empresas obtenidas de la BD: " + empresas.size());

        } catch (SQLException e) {
            System.out.println("Error al obtener todas las empresas: " + e.getMessage());
            e.printStackTrace();
        }

        return empresas;
    }

    /**
     * Verifica si ya existe una empresa con el mismo nombre
     *
     * @param nombreEmpresa Nombre a verificar
     * @return true si ya existe, false si no existe
     */
    public boolean existeEmpresaPorNombre(String nombreEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(EXISTE_EMPRESA_POR_NOMBRE_QUERY)) {
            query.setString(1, nombreEmpresa);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar nombre de empresa: " + e.getMessage());
        }

        return false;
    }

    /**
     * actualizar una empresa en la base de datos
     *
     * @param empresa
     * @return
     */
    public boolean updateEmpresa(Empresa empresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_EMPRESA_QUERY)) {

            update.setString(1, empresa.getNombre());
            update.setString(2, empresa.getDescripcion());
            update.setInt(3, empresa.getId_empresa());

            int affectedRows = update.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar empresa: " + e.getMessage(), e);
        }
    }

    /**
     * Verifica si ya existe una empresa con el mismo nombre, excluyendo una
     * empresa específica (Para validaciones en update)
     *
     * @param nombreEmpresa
     * @param idEmpresaExcluir
     * @return
     */
    public boolean existeEmpresaPorNombreExcluyendoId(String nombreEmpresa, int idEmpresaExcluir) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(EXISTE_EMPRESA_POR_NOMBRE_EXCLUYENDO_ID_QUERY)) {
            query.setString(1, nombreEmpresa);
            query.setInt(2, idEmpresaExcluir);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar nombre de empresa (excluyendo ID): " + e.getMessage());
        }

        return false;
    }

    //comisiones
    public Comision crearComision(Comision comision) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement insert = connection.prepareStatement(CREAR_COMISION_QUERY,
                Statement.RETURN_GENERATED_KEYS)) {

            insert.setInt(1, comision.getId_empresa());
            insert.setDouble(2, comision.getPorcentaje());
            insert.setDate(3, new java.sql.Date(comision.getFecha_inicio().getTime()));

            if (comision.getFecha_final() != null) {
                insert.setDate(4, new java.sql.Date(comision.getFecha_final().getTime()));
            } else {
                insert.setNull(4, Types.DATE);
            }

            insert.setString(5, comision.getTipo_comision());

            int affectedRows = insert.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comision.setId_comision(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear comisión: " + e.getMessage(), e);
        }

        return comision;
    }

    public Comision getComisionActualEmpresa(int idEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMISION_ACTUAL_EMPRESA_QUERY)) {
            query.setInt(1, idEmpresa);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToComision(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Comision> getTodasComisionesEmpresa(int idEmpresa) {
        List<Comision> comisiones = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_COMISIONES_EMPRESA_QUERY)) {
            query.setInt(1, idEmpresa);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Comision comision = mapResultSetToComision(resultSet);
                comisiones.add(comision);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return comisiones;
    }

    public int actualizarComisionesMayoresQueGlobal(int idEmpresa, double nuevoPorcentajeGlobal) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_COMISION_GLOBAL_QUERY)) {
            update.setDouble(1, nuevoPorcentajeGlobal);
            update.setInt(2, idEmpresa);
            update.setDouble(3, nuevoPorcentajeGlobal);

            int affectedRows = update.executeUpdate();
            return affectedRows;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar comisiones: " + e.getMessage(), e);
        }
    }

    private Comision mapResultSetToComision(ResultSet resultSet) throws SQLException {
        return new Comision(
                resultSet.getInt("id_comision"),
                resultSet.getInt("id_empresa"),
                resultSet.getDouble("porcentaje"),
                resultSet.getDate("fecha_inicio"),
                resultSet.getDate("fecha_final"),
                resultSet.getString("tipo_comision")
        );
    }

    /**
     * Crear comision con fecha
     *
     * @param idEmpresa
     * @param porcentaje
     * @param fechaInicio
     * @param fechaFinal
     * @return
     */
    public Comision crearComisionConFecha(int idEmpresa, double porcentaje, Date fechaInicio, Date fechaFinal, String tipo_comision) {
        Comision comision = new Comision(idEmpresa, porcentaje, fechaInicio, tipo_comision);
        comision.setFecha_final(fechaFinal);
        return crearComision(comision);
    }

    public boolean finalizarComision(int idComision, Date fechaFinal) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(FINALIZAR_COMISION_QUERY)) {
            update.setDate(1, new java.sql.Date(fechaFinal.getTime()));
            update.setInt(2, idComision);

            int affectedRows = update.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
