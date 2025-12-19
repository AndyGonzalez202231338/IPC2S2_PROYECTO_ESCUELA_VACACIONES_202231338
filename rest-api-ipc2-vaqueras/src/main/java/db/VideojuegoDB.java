/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package db;

import categoria.models.Categoria;
import conexion.DBConnectionSingleton;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class VideojuegoDB {

    private static final String CREAR_VIDEOJUEGO_QUERY
            = "INSERT INTO videojuego (id_empresa, titulo, descripcion, recursos_minimos, precio, clasificacion_edad, fecha_lanzamiento) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String OBTENER_VIDEOJUEGO_POR_ID_QUERY
            = "SELECT id_videojuego, id_empresa, titulo, descripcion, recursos_minimos, precio, clasificacion_edad, fecha_lanzamiento "
            + "FROM videojuego WHERE id_videojuego = ?";

    private static final String OBTENER_VIDEOJUEGO_POR_TITULO_QUERY
            = "SELECT id_videojuego, id_empresa, titulo, descripcion, recursos_minimos, precio, clasificacion_edad, fecha_lanzamiento "
            + "FROM videojuego WHERE titulo = ?";

    private static final String OBTENER_TODOS_VIDEOJUEGOS_QUERY
            = "SELECT id_videojuego, id_empresa, titulo, descripcion, recursos_minimos, precio, clasificacion_edad, fecha_lanzamiento "
            + "FROM videojuego ORDER BY fecha_lanzamiento DESC";

    private static final String ACTUALIZAR_VIDEOJUEGO_QUERY
            = "UPDATE videojuego SET id_empresa = ?, titulo = ?, descripcion = ?, recursos_minimos = ?, "
            + "precio = ?, clasificacion_edad = ?, fecha_lanzamiento = ? WHERE id_videojuego = ?";

    private static final String ELIMINAR_VIDEOJUEGO_QUERY
            = "DELETE FROM videojuego WHERE id_videojuego = ?";

    private static final String EXISTE_TITULO_POR_EMPRESA_QUERY
            = "SELECT COUNT(*) as count FROM videojuego WHERE titulo = ? AND id_empresa = ?";

    //videojuego_categoria
    private static final String AGREGAR_CATEGORIA_A_VIDEOJUEGO_QUERY
            = "INSERT INTO videojuego_categoria (id_videojuego, id_categoria, estado) VALUES (?, ?, ?)";

    private static final String REMOVER_CATEGORIAS_DE_VIDEOJUEGO_QUERY
            = "DELETE FROM videojuego_categoria WHERE id_videojuego = ?";

    private static final String OBTENER_CATEGORIAS_POR_VIDEOJUEGO_QUERY
            = "SELECT c.id_categoria, c.nombre, c.descripcion, vc.estado "
            + "FROM categoria c "
            + "INNER JOIN videojuego_categoria vc ON c.id_categoria = vc.id_categoria "
            + "WHERE vc.id_videojuego = ?";

    private static final String OBTENER_CATEGORIAS_APROBADAS_POR_VIDEOJUEGO_QUERY
            = "SELECT c.id_categoria, c.nombre, c.descripcion "
            + "FROM categoria c "
            + "INNER JOIN videojuego_categoria vc ON c.id_categoria = vc.id_categoria "
            + "WHERE vc.id_videojuego = ? AND vc.estado = 'APROBADA'";

    private static final String ACTUALIZAR_ESTADO_CATEGORIA_QUERY
            = "UPDATE videojuego_categoria SET estado = ? WHERE id_videojuego = ? AND id_categoria = ?";
    //empresa
    private static final String OBTENER_VIDEOJUEGOS_POR_EMPRESA_QUERY
        = "SELECT id_videojuego, id_empresa, titulo, descripcion, recursos_minimos, precio, clasificacion_edad, fecha_lanzamiento "
        + "FROM videojuego WHERE id_empresa = ? ORDER BY fecha_lanzamiento DESC";
    
    
    /**
     * Crear un nuevo videojuego con sus categorías usando una TRANSACCION con
     * insertvideojuego e insertCategoriasVideojuego
     *
     * @param videojuego
     * @param categoriasIds
     * @return
     */
    public Videojuego createVideojuego(Videojuego videojuego, List<Integer> categoriasIds) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try {
            connection.setAutoCommit(false); // Iniciar transacción

            Videojuego videojuegoCreado = insertVideojuego(connection, videojuego);

            // Insertar categorías asociadas y darle valor pendiente
            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                insertCategoriasVideojuego(connection, videojuegoCreado.getId_videojuego(), categoriasIds, "PENDIENTE");
            }

            connection.commit(); // Confirmar transacción
            return videojuegoCreado;

        } catch (SQLException e) {
            try {
                connection.rollback(); // Revertir en caso de error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Error al crear videojuego: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Videojuego insertVideojuego(Connection connection, Videojuego videojuego) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(CREAR_VIDEOJUEGO_QUERY,
                Statement.RETURN_GENERATED_KEYS)) {

            insert.setInt(1, videojuego.getId_empresa());
            insert.setString(2, videojuego.getTitulo());
            insert.setString(3, videojuego.getDescripcion());
            insert.setString(4, videojuego.getRecursos_minimos());
            insert.setBigDecimal(5, videojuego.getPrecio());
            insert.setString(6, videojuego.getClasificacion_edad());
            insert.setDate(7, new Date(videojuego.getFecha_lanzamiento().getTime()));

            int affectedRows = insert.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        videojuego.setId_videojuego(generatedKeys.getInt(1));
                    }
                }
            }

            return videojuego;
        }
    }

    private void insertCategoriasVideojuego(Connection connection, int idVideojuego,
            List<Integer> categoriasIds, String estado) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(AGREGAR_CATEGORIA_A_VIDEOJUEGO_QUERY)) {

            for (Integer idCategoria : categoriasIds) {
                insert.setInt(1, idVideojuego);
                insert.setInt(2, idCategoria);
                insert.setString(3, estado);
                insert.addBatch();
            }

            insert.executeBatch();
        }
    }

    /**
     * Obtener videojuego por ID con sus categorías
     *
     * @param idVideojuego
     * @param incluirCategorias
     * @return
     */
    public Videojuego getVideojuegoById(int idVideojuego, boolean incluirCategorias) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_VIDEOJUEGO_POR_ID_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                Videojuego videojuego = mapResultSetToVideojuego(resultSet);

                if (incluirCategorias) {
                    List<Categoria> categorias = obtenerCategoriasPorVideojuego(connection, idVideojuego);
                    videojuego.setCategorias(categorias);
                }

                return videojuego;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Videojuego getVideojuegoByTitulo(String titulo, boolean incluirCategorias) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_VIDEOJUEGO_POR_TITULO_QUERY)) {
            query.setString(1, titulo);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                Videojuego videojuego = mapResultSetToVideojuego(resultSet);

                if (incluirCategorias) {
                    List<Categoria> categorias = obtenerCategoriasPorVideojuego(connection, videojuego.getId_videojuego());
                    videojuego.setCategorias(categorias);
                }

                return videojuego;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Obtener todos los videojuegos
     *
     * @param incluirCategorias
     * @return
     */
    public List<Videojuego> getAllVideojuegos(boolean incluirCategorias) {
        List<Videojuego> videojuegos = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_TODOS_VIDEOJUEGOS_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Videojuego videojuego = mapResultSetToVideojuego(resultSet);

                if (incluirCategorias) {
                    List<Categoria> categorias = obtenerCategoriasPorVideojuego(connection, videojuego.getId_videojuego());
                    videojuego.setCategorias(categorias);
                }

                videojuegos.add(videojuego);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error al obtener videojuegos: " + e.getMessage());
        }

        return videojuegos;
    }

    /**
     * Actualizar videojuego
     *
     * @param videojuego
     * @return
     */
    public boolean updateVideojuego(Videojuego videojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_VIDEOJUEGO_QUERY)) {
            update.setInt(1, videojuego.getId_empresa());
            update.setString(2, videojuego.getTitulo());
            update.setString(3, videojuego.getDescripcion());
            update.setString(4, videojuego.getRecursos_minimos());
            update.setBigDecimal(5, videojuego.getPrecio());
            update.setString(6, videojuego.getClasificacion_edad());
            update.setDate(7, new Date(videojuego.getFecha_lanzamiento().getTime()));
            update.setInt(8, videojuego.getId_videojuego());

            int affectedRows = update.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar videojuego: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar videojuego y su relacion con categorias (videojuego_categoria)
     *
     * @param idVideojuego
     * @return
     */
    public boolean deleteVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            //Eliminar relaciones con categorías
            try (PreparedStatement deleteCategorias = connection.prepareStatement(REMOVER_CATEGORIAS_DE_VIDEOJUEGO_QUERY)) {
                deleteCategorias.setInt(1, idVideojuego);
                deleteCategorias.executeUpdate();
            }

            //Eliminar videojuego
            try (PreparedStatement deleteVideojuego = connection.prepareStatement(ELIMINAR_VIDEOJUEGO_QUERY)) {
                deleteVideojuego.setInt(1, idVideojuego);
                int affectedRows = deleteVideojuego.executeUpdate();

                connection.commit();
                return affectedRows > 0;
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar videojuego: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Verificar si ya existe un título para la misma empresa
     *
     * @param titulo
     * @param idEmpresa
     * @return
     */
    public boolean existeTituloPorEmpresa(String titulo, int idEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(EXISTE_TITULO_POR_EMPRESA_QUERY)) {
            query.setString(1, titulo);
            query.setInt(2, idEmpresa);
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
     * Actualizar categorías de un videojuego, cambio de categorias
     *
     * @param idVideojuego
     * @param categoriasIds
     */
    public void actualizarCategoriasVideojuego(int idVideojuego, List<Integer> categoriasIds) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try {
            connection.setAutoCommit(false);

            //Eliminar categorías existentes
            try (PreparedStatement delete = connection.prepareStatement(REMOVER_CATEGORIAS_DE_VIDEOJUEGO_QUERY)) {
                delete.setInt(1, idVideojuego);
                delete.executeUpdate();
            }

            //Insertar nuevas categorías
            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                insertCategoriasVideojuego(connection, idVideojuego, categoriasIds, "PENDIENTE");
            }

            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar categorías: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Obtener categorías de un videojuego
     */
    private List<Categoria> obtenerCategoriasPorVideojuego(Connection connection, int idVideojuego) throws SQLException {
        List<Categoria> categorias = new ArrayList<>();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CATEGORIAS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Categoria categoria = new Categoria(
                        resultSet.getInt("id_categoria"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion")
                );
                categorias.add(categoria);
            }
        }

        return categorias;
    }

    /**
     * Obtener categorías aprobadas de un videojuego
     */
    public List<Categoria> obtenerCategoriasAprobadas(int idVideojuego) {
        List<Categoria> categorias = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(OBTENER_CATEGORIAS_APROBADAS_POR_VIDEOJUEGO_QUERY)) {
            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Categoria categoria = new Categoria(
                        resultSet.getInt("id_categoria"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion")
                );
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categorias;
    }

    /**
     * Actualizar estado de una categoría en videojuego
     */
    public boolean actualizarEstadoCategoria(int idVideojuego, int idCategoria, String estado) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ACTUALIZAR_ESTADO_CATEGORIA_QUERY)) {
            update.setString(1, estado);
            update.setInt(2, idVideojuego);
            update.setInt(3, idCategoria);

            int affectedRows = update.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar estado de categoría: " + e.getMessage(), e);
        }
    }

    /**
     * Mapear ResultSet a Videojuego
     */
    private Videojuego mapResultSetToVideojuego(ResultSet resultSet) throws SQLException {
        Videojuego videojuego = new Videojuego();
        videojuego.setId_videojuego(resultSet.getInt("id_videojuego"));
        videojuego.setId_empresa(resultSet.getInt("id_empresa"));
        videojuego.setTitulo(resultSet.getString("titulo"));
        videojuego.setDescripcion(resultSet.getString("descripcion"));
        videojuego.setRecursos_minimos(resultSet.getString("recursos_minimos"));
        videojuego.setPrecio(resultSet.getBigDecimal("precio"));
        videojuego.setClasificacion_edad(resultSet.getString("clasificacion_edad"));
        videojuego.setFecha_lanzamiento(resultSet.getDate("fecha_lanzamiento"));
        return videojuego;
    }

    public int obtenerIdEmpresaDelVideojuego(int idVideojuego) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(
                "SELECT id_empresa FROM videojuego WHERE id_videojuego = ?")) {

            query.setInt(1, idVideojuego);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id_empresa");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // No encontrado
    }
    
    /**
 * Obtener todos los videojuegos de una empresa
 *
 * @param idEmpresa
 * @param incluirCategorias
 * @return
 */
public List<Videojuego> getVideojuegosByEmpresa(int idEmpresa, boolean incluirCategorias) {
    List<Videojuego> videojuegos = new ArrayList<>();
    Connection connection = DBConnectionSingleton.getInstance().getConnection();

    try (PreparedStatement query = connection.prepareStatement(OBTENER_VIDEOJUEGOS_POR_EMPRESA_QUERY)) {
        query.setInt(1, idEmpresa);
        ResultSet resultSet = query.executeQuery();

        while (resultSet.next()) {
            Videojuego videojuego = mapResultSetToVideojuego(resultSet);

            if (incluirCategorias) {
                List<Categoria> categorias = obtenerCategoriasPorVideojuego(connection, videojuego.getId_videojuego());
                videojuego.setCategorias(categorias);
            }

            videojuegos.add(videojuego);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Error al obtener videojuegos de empresa: " + e.getMessage());
    }

    return videojuegos;
}
}
