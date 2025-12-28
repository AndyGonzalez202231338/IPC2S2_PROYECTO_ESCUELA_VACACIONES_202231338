package db;

import comentario.dto.VideojuegoNotaResponse;
import comentario.model.Calificacion;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CalificacionDB {

    private static final String CREAR_CALIFICACION_QUERY
            = "INSERT INTO calificacion (id_usuario, id_biblioteca, calificacion, fecha_hora) VALUES (?, ?, ?, ?)";

    private static final String OBTENER_CALIFICACION_POR_ID_QUERY
            = "SELECT id_calificacion, id_usuario, id_biblioteca, calificacion, fecha_hora FROM calificacion WHERE id_calificacion = ?";

    private static final String OBTENER_CALIFICACION_POR_USUARIO_Y_BIBLIOTECA_QUERY
            = "SELECT id_calificacion, id_usuario, id_biblioteca, calificacion, fecha_hora FROM calificacion WHERE id_usuario = ? AND id_biblioteca = ?";

    private static final String OBTENER_CALIFICACIONES_POR_BIBLIOTECA_QUERY
            = "SELECT id_calificacion, id_usuario, id_biblioteca, calificacion, fecha_hora FROM calificacion WHERE id_biblioteca = ?";

    private static final String OBTENER_PROMEDIO_CALIFICACIONES_BIBLIOTECA_QUERY
            = "SELECT AVG(calificacion) as promedio FROM calificacion WHERE id_biblioteca = ?";

    private static final String OBTENER_TOTAL_CALIFICACIONES_BIBLIOTECA_QUERY
            = "SELECT COUNT(*) as total FROM calificacion WHERE id_biblioteca = ?";

    private static final String EXISTE_CALIFICACION_POR_USUARIO_Y_BIBLIOTECA_QUERY
            = "SELECT COUNT(*) as count FROM calificacion WHERE id_usuario = ? AND id_biblioteca = ?";

    private static final String OBTENER_CALIFICACIONES_POR_VIDEOJUEGO_QUERY
            = "SELECT c.id_calificacion, c.id_usuario, c.id_biblioteca, c.calificacion, c.fecha_hora "
            + "FROM calificacion c "
            + "JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca "
            + "WHERE bu.id_videojuego = ?";

    /**
     * Crear una nueva calificación
     *
     * @param calificacion
     * @return
     */
    public Calificacion createCalificacion(Calificacion calificacion) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement insert = connection.prepareStatement(CREAR_CALIFICACION_QUERY,
                Statement.RETURN_GENERATED_KEYS)) {

            insert.setInt(1, calificacion.getId_usuario());
            insert.setInt(2, calificacion.getId_biblioteca());
            insert.setInt(3, calificacion.getCalificacion());
            insert.setTimestamp(4, Timestamp.valueOf(calificacion.getFecha_hora()));

            int affectedRows = insert.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        calificacion.setId_calificacion(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear calificación: " + e.getMessage(), e);
        }

        return calificacion;
    }

    public Calificacion getCalificacionById(int idCalificacion) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CALIFICACION_POR_ID_QUERY)) {
            query.setInt(1, idCalificacion);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToCalificacion(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene una calificación específica de un usuario para una biblioteca
     *
     * @param idUsuario
     * @param idBiblioteca
     * @return
     */
    public Calificacion getCalificacionPorUsuarioYBiblioteca(int idUsuario, int idBiblioteca) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CALIFICACION_POR_USUARIO_Y_BIBLIOTECA_QUERY)) {
            query.setInt(1, idUsuario);
            query.setInt(2, idBiblioteca);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToCalificacion(resultSet);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtiene todas las calificaciones de una biblioteca
     *
     * @param idBiblioteca
     * @return
     */
    public List<Calificacion> getCalificacionesPorBiblioteca(int idBiblioteca) {
        List<Calificacion> calificaciones = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CALIFICACIONES_POR_BIBLIOTECA_QUERY)) {
            query.setInt(1, idBiblioteca);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Calificacion calificacion = mapResultSetToCalificacion(resultSet);
                calificaciones.add(calificacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener calificaciones por biblioteca: " + e.getMessage());
        }

        return calificaciones;
    }

    /**
     * Calcula el promedio de calificaciones de una biblioteca
     *
     * @param idBiblioteca
     * @return
     */
    public Double getPromedioCalificacionesPorBiblioteca(int idBiblioteca) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_PROMEDIO_CALIFICACIONES_BIBLIOTECA_QUERY)) {
            query.setInt(1, idBiblioteca);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("promedio");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al calcular promedio de calificaciones: " + e.getMessage());
        }

        return 0.0;
    }

    /**
     * Obtiene el total de calificaciones de una biblioteca
     *
     * @param idBiblioteca
     * @return
     */
    public int getTotalCalificacionesPorBiblioteca(int idBiblioteca) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TOTAL_CALIFICACIONES_BIBLIOTECA_QUERY)) {
            query.setInt(1, idBiblioteca);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("total");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener total de calificaciones: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Verifica si ya existe una calificación de un usuario para una biblioteca
     *
     * @param idUsuario
     * @param idBiblioteca
     * @return
     */
    public boolean existeCalificacionPorUsuarioYBiblioteca(int idUsuario, int idBiblioteca) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(EXISTE_CALIFICACION_POR_USUARIO_Y_BIBLIOTECA_QUERY)) {
            query.setInt(1, idUsuario);
            query.setInt(2, idBiblioteca);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al verificar existencia de calificación: " + e.getMessage());
        }

        return false;
    }

    /**
     * Obtiene todas las calificaciones de un videojuego específico
     *
     * @param idVideojuego
     * @return
     */
    public List<Calificacion> getCalificacionesPorVideojuego(int idVideojuego) {
        List<Calificacion> calificaciones = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CALIFICACIONES_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Calificacion calificacion = mapResultSetToCalificacion(resultSet);
                calificaciones.add(calificacion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener calificaciones por videojuego: " + e.getMessage());
        }

        return calificaciones;
    }

    /**
     * Mapea un ResultSet a un objeto Calificacion
     */
    private Calificacion mapResultSetToCalificacion(ResultSet resultSet) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp("fecha_hora");
        LocalDateTime fechaHora = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();

        return new Calificacion(
                resultSet.getInt("id_usuario"),
                resultSet.getInt("id_biblioteca"),
                resultSet.getInt("calificacion"),
                fechaHora
        );
    }

    public Double getPromedioCalificacionesPorVideojuego(int idVideojuego) {
        String sql = """
        SELECT AVG(c.calificacion) AS promedio
        FROM calificacion c
        JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca
        WHERE bu.id_videojuego = ?
    """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVideojuego);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getDouble("promedio") : 0.0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public int getTotalCalificacionesPorVideojuego(int idVideojuego) {
        String sql = """
        SELECT COUNT(*) AS total
        FROM calificacion c
        JOIN biblioteca_usuario bu ON c.id_biblioteca = bu.id_biblioteca
        WHERE bu.id_videojuego = ?
    """;

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVideojuego);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("total") : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static final String VIDEOJUEGOS_CON_NOTA_QUERY
            = "SELECT v.id_videojuego, v.nombre, "
            + "AVG(c.calificacion) AS nota, COUNT(c.id_calificacion) AS total "
            + "FROM videojuego v "
            + "LEFT JOIN biblioteca_usuario bu ON v.id_videojuego = bu.id_videojuego "
            + "LEFT JOIN calificacion c ON bu.id_biblioteca = c.id_biblioteca "
            + "GROUP BY v.id_videojuego, v.nombre";

    public List<VideojuegoNotaResponse> getVideojuegosConNotaFinal() {
        List<VideojuegoNotaResponse> lista = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(VIDEOJUEGOS_CON_NOTA_QUERY)) {
            ResultSet rs = query.executeQuery();

            while (rs.next()) {
                lista.add(new VideojuegoNotaResponse(
                        rs.getInt("id_videojuego"),
                        rs.getString("nombre"),
                        rs.getDouble("nota"),
                        rs.getInt("total")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}
