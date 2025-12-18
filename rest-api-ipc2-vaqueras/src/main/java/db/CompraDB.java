/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import compra.models.Compra;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import user.models.Usuario;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class CompraDB {

    private static final String CREAR_COMPRA_QUERY
            = "INSERT INTO compra (id_usuario, id_videojuego, monto_pago, fecha_compra, comision_aplicada) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String OBTENER_COMPRA_POR_ID_QUERY
            = "SELECT c.id_compra, c.id_usuario, c.id_videojuego, c.monto_pago, "
            + "c.fecha_compra, c.comision_aplicada, "
            + "v.id_videojuego as v_id, v.titulo as v_titulo, v.precio as v_precio, "
            + "v.fecha_lanzamiento as v_fecha_lanzamiento "
            + "FROM compra c "
            + "LEFT JOIN videojuego v ON c.id_videojuego = v.id_videojuego "
            + "WHERE c.id_compra = ?";

    private static final String OBTENER_COMPRAS_POR_USUARIO_QUERY
            = "SELECT c.id_compra, c.id_usuario, c.id_videojuego, c.monto_pago, "
            + "c.fecha_compra, c.comision_aplicada, "
            + "v.id_videojuego as v_id, v.id_empresa as v_id_empresa, "
            + "v.titulo as v_titulo, v.descripcion as v_descripcion, "
            + "v.recursos_minimos as v_recursos_minimos, v.precio as v_precio, "
            + "v.clasificacion_edad as v_clasificacion_edad, "
            + "v.fecha_lanzamiento as v_fecha_lanzamiento "
            + "FROM compra c "
            + "LEFT JOIN videojuego v ON c.id_videojuego = v.id_videojuego "
            + "WHERE c.id_usuario = ? "
            + "ORDER BY c.fecha_compra DESC";

    private static final String OBTENER_COMPRAS_POR_VIDEOJUEGO_QUERY
            = "SELECT c.*, "
            + "u.id_usuario as u_id, u.correo as u_correo, u.nombre as u_nombre "
            + "FROM compra c "
            + "LEFT JOIN usuario u ON c.id_usuario = u.id_usuario "
            + "WHERE c.id_videojuego = ? "
            + "ORDER BY c.fecha_compra DESC";

    private static final String OBTENER_TODAS_COMPRAS_QUERY
            = "SELECT c.*, "
            + "u.id_usuario as u_id, u.correo as u_correo, u.nombre as u_nombre, "
            + "v.id_videojuego as v_id, v.titulo as v_titulo "
            + "FROM compra c "
            + "LEFT JOIN usuario u ON c.id_usuario = u.id_usuario "
            + "LEFT JOIN videojuego v ON c.id_videojuego = v.id_videojuego "
            + "ORDER BY c.fecha_compra DESC";

    private static final String ACTUALIZAR_COMPRA_QUERY
            = "UPDATE compra SET id_usuario = ?, id_videojuego = ?, monto_pago = ?, "
            + "fecha_compra = ?, comision_aplicada = ? WHERE id_compra = ?";

    private static final String ELIMINAR_COMPRA_QUERY
            = "DELETE FROM compra WHERE id_compra = ?";

    private static final String ELIMINAR_COMPRAS_POR_USUARIO_QUERY
            = "DELETE FROM compra WHERE id_usuario = ?";

    private static final String ELIMINAR_COMPRAS_POR_VIDEOJUEGO_QUERY
            = "DELETE FROM compra WHERE id_videojuego = ?";

    private static final String VERIFICAR_COMPRA_EXISTENTE_QUERY
            = "SELECT COUNT(*) as count FROM compra WHERE id_usuario = ? AND id_videojuego = ?";

    private static final String OBTENER_TOTAL_GASTADO_POR_USUARIO_QUERY
            = "SELECT SUM(monto_pago + comision_aplicada) as total_gastado FROM compra WHERE id_usuario = ?";

    private static final String OBTENER_INGRESOS_POR_VIDEOJUEGO_QUERY
            = "SELECT SUM(monto_pago) as total_ingresos FROM compra WHERE id_videojuego = ?";

    private static final String OBTENER_ESTADISTICAS_COMPRAS_QUERY
            = "SELECT COUNT(*) as total_compras, SUM(monto_pago) as total_monto, "
            + "SUM(comision_aplicada) as total_comision, AVG(monto_pago) as promedio_compra "
            + "FROM compra";

    /**
     * Registrar una compra de un videojuego
     *
     * @param compra
     * @return
     */
    public Compra createCompra(Compra compra) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement insert = connection.prepareStatement(CREAR_COMPRA_QUERY,
                Statement.RETURN_GENERATED_KEYS)) {

            insert.setInt(1, compra.getId_usuario());
            insert.setInt(2, compra.getId_videojuego());
            insert.setDouble(3, compra.getMonto_pago());
            insert.setDate(4, new Date(compra.getFecha_compra().getTime()));
            insert.setDouble(5, compra.getComision_aplicada());

            int affectedRows = insert.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        compra.setId_compra(generatedKeys.getInt(1));
                    }
                }
            }

            return compra;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear compra: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener una compra por id
     *
     * @param idCompra
     * @return
     */
    public Compra getCompraById(int idCompra) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMPRA_POR_ID_QUERY)) {
            query.setInt(1, idCompra);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToCompraConRelaciones(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtener compras por usuario con información del videojuego
     *
     * @param idUsuario
     * @return
     */
    public List<Compra> getComprasByUsuario(int idUsuario) {
        List<Compra> compras = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMPRAS_POR_USUARIO_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Compra compra = mapResultSetToCompraConVideojuego(resultSet);
                compras.add(compra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compras;
    }

    /**
     * Obtener compras por videojuego
     *
     * @param idVideojuego
     * @return
     */
    public List<Compra> getComprasByVideojuego(int idVideojuego) {
        List<Compra> compras = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_COMPRAS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Compra compra = mapResultSetToCompraConUsuario(resultSet);
                compras.add(compra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compras;
    }

    /**
     * Obtener todas las compras con información adicional
     *
     * @return
     */
    public List<Compra> getAllCompras() {
        List<Compra> compras = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODAS_COMPRAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Compra compra = mapResultSetToCompraCompleta(resultSet);
                compras.add(compra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compras;
    }

    /**
     * Actualizar compra
     *
     * @param compra
     * @return
     */
    public boolean updateCompra(Compra compra) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_COMPRA_QUERY)) {
            update.setInt(1, compra.getId_usuario());
            update.setInt(2, compra.getId_videojuego());
            update.setDouble(3, compra.getMonto_pago());
            update.setDate(4, new Date(compra.getFecha_compra().getTime()));
            update.setDouble(5, compra.getComision_aplicada());
            update.setInt(6, compra.getId_compra());

            int affectedRows = update.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar compra: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar compra por ID
     *
     * @param idCompra
     * @return
     */
    public boolean deleteCompra(int idCompra) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_COMPRA_QUERY)) {
            delete.setInt(1, idCompra);

            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar compra: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar todas las compras de un usuario
     *
     * @param idUsuario
     * @return
     */
    public boolean deleteComprasByUsuario(int idUsuario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_COMPRAS_POR_USUARIO_QUERY)) {
            delete.setInt(1, idUsuario);

            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar compras por usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar todas las compras de un videojuego
     *
     * @param idVideojuego
     * @return
     */
    public boolean deleteComprasByVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement delete = connection.prepareStatement(ELIMINAR_COMPRAS_POR_VIDEOJUEGO_QUERY)) {
            delete.setInt(1, idVideojuego);

            int affectedRows = delete.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar compras por videojuego: " + e.getMessage(), e);
        }
    }

    /**
     * Verificar si ya existe una compra de este usuario para este videojuego
     *
     * @param idUsuario
     * @param idVideojuego
     * @return
     */
    public boolean existeCompraUsuarioVideojuego(int idUsuario, int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(VERIFICAR_COMPRA_EXISTENTE_QUERY)) {
            query.setInt(1, idUsuario);
            query.setInt(2, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Obtener total gastado por usuario
     *
     * @param idUsuario
     * @return
     */
    public double getTotalGastadoPorUsuario(int idUsuario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TOTAL_GASTADO_POR_USUARIO_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                double total = resultSet.getDouble("total_gastado");
                return resultSet.wasNull() ? 0.0 : total;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Obtener ingresos por videojuego
     *
     * @param idVideojuego
     * @return
     */
    public double getIngresosPorVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_INGRESOS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                double ingresos = resultSet.getDouble("total_ingresos");
                return resultSet.wasNull() ? 0.0 : ingresos;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Obtener estadísticas generales de compras
     */
    public Map<String, Object> getEstadisticasCompras() {
        Map<String, Object> estadisticas = new HashMap<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_ESTADISTICAS_COMPRAS_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                estadisticas.put("total_compras", resultSet.getInt("total_compras"));

                double totalMonto = resultSet.getDouble("total_monto");
                estadisticas.put("total_monto", resultSet.wasNull() ? 0.0 : totalMonto);

                double totalComision = resultSet.getDouble("total_comision");
                estadisticas.put("total_comision", resultSet.wasNull() ? 0.0 : totalComision);

                double promedioCompra = resultSet.getDouble("promedio_compra");
                estadisticas.put("promedio_compra", resultSet.wasNull() ? 0.0 : promedioCompra);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return estadisticas;
    }

    /**
     * Mapear ResultSet a Compra con relaciones completas
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Compra mapResultSetToCompraConRelaciones(ResultSet resultSet) throws SQLException {
        Compra compra = mapResultSetToCompraBasica(resultSet);

        // Mapear Usuario si existe en el ResultSet
        try {
            if (resultSet.getObject("u_id") != null) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("u_id"));
                usuario.setCorreo(resultSet.getString("u_correo"));
                usuario.setNombre(resultSet.getString("u_nombre"));
                usuario.setFecha_nacimiento(resultSet.getDate("u_fecha_nacimiento"));
                usuario.setPais(resultSet.getString("u_pais"));
                usuario.setTelefono(resultSet.getString("u_telefono"));
                usuario.setSaldo_cartera(resultSet.getDouble("u_saldo_cartera"));
                compra.setUsuario(usuario);
            }
        } catch (SQLException e) {
            // Si no existen las columnas de usuario, simplemente no se mapean
        }

        // Mapear Videojuego si existe en el ResultSet
        try {
            if (resultSet.getObject("v_id") != null) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId_videojuego(resultSet.getInt("v_id"));
                videojuego.setTitulo(resultSet.getString("v_titulo"));
                videojuego.setDescripcion(resultSet.getString("v_descripcion"));
                videojuego.setPrecio(resultSet.getBigDecimal("v_precio"));
                videojuego.setFecha_lanzamiento(resultSet.getDate("v_fecha_lanzamiento"));
                compra.setVideojuego(videojuego);
            }
        } catch (SQLException e) {
            // Si no existen las columnas de videojuego, simplemente no se mapean
        }

        return compra;
    }

    /**
     * Mapear ResultSet a Compra básica
     */
    private Compra mapResultSetToCompraBasica(ResultSet resultSet) throws SQLException {
        Compra compra = new Compra();
        compra.setId_compra(resultSet.getInt("id_compra"));
        compra.setId_usuario(resultSet.getInt("id_usuario"));
        compra.setId_videojuego(resultSet.getInt("id_videojuego"));
        compra.setMonto_pago(resultSet.getDouble("monto_pago"));
        compra.setFecha_compra(resultSet.getDate("fecha_compra"));
        compra.setComision_aplicada(resultSet.getDouble("comision_aplicada"));
        return compra;
    }

    /**
     * Mapear ResultSet a Compra con videojuego
     *
     * @param resultSet
     * @return
     * @throws SQLException
     */
    private Compra mapResultSetToCompraConVideojuego(ResultSet resultSet) throws SQLException {
        Compra compra = mapResultSetToCompraBasica(resultSet);

        try {
            if (resultSet.getObject("v_id") != null) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId_videojuego(resultSet.getInt("v_id"));
                videojuego.setId_empresa(resultSet.getInt("v_id_empresa")); 
                videojuego.setTitulo(resultSet.getString("v_titulo"));
                videojuego.setDescripcion(resultSet.getString("v_descripcion"));
                videojuego.setRecursos_minimos(resultSet.getString("v_recursos_minimos")); 
                videojuego.setPrecio(resultSet.getBigDecimal("v_precio"));
                videojuego.setClasificacion_edad(resultSet.getString("v_clasificacion_edad")); 
                videojuego.setFecha_lanzamiento(resultSet.getDate("v_fecha_lanzamiento"));
                compra.setVideojuego(videojuego);
            }
        } catch (SQLException e) {
            System.err.println("Error mapeando videojuego: " + e.getMessage());
            
        }

        return compra;
    }

    /**
     * Mapear ResultSet a Compra con usuario
     */
    private Compra mapResultSetToCompraConUsuario(ResultSet resultSet) throws SQLException {
        Compra compra = mapResultSetToCompraBasica(resultSet);

        try {
            if (resultSet.getObject("u_id") != null) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("u_id"));
                usuario.setCorreo(resultSet.getString("u_correo"));
                usuario.setNombre(resultSet.getString("u_nombre"));
                compra.setUsuario(usuario);
            }
        } catch (SQLException e) {
            // Ignorar si no existen las columnas
        }

        return compra;
    }

    /**
     * Mapear ResultSet a Compra completa
     */
    private Compra mapResultSetToCompraCompleta(ResultSet resultSet) throws SQLException {
        Compra compra = mapResultSetToCompraBasica(resultSet);

        try {
            if (resultSet.getObject("u_id") != null) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(resultSet.getInt("u_id"));
                usuario.setCorreo(resultSet.getString("u_correo"));
                usuario.setNombre(resultSet.getString("u_nombre"));
                compra.setUsuario(usuario);
            }

            if (resultSet.getObject("v_id") != null) {
                Videojuego videojuego = new Videojuego();
                videojuego.setId_videojuego(resultSet.getInt("v_id"));
                videojuego.setTitulo(resultSet.getString("v_titulo"));
                compra.setVideojuego(videojuego);
            }
        } catch (SQLException e) {

        }

        return compra;
    }
}
