package db;

import biblioteca.dtos.BibliotecaResponse;
import biblioteca.dtos.CompraMiniResponse;
import biblioteca.dtos.VideojuegoMiniResponse;
import biblioteca.models.Biblioteca;
import conexion.DBConnectionSingleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BibliotecaDB {

    private static final String CREAR_BIBLIOTECA_QUERY
            = "INSERT INTO biblioteca_usuario (id_usuario, id_videojuego, id_compra, tipo_adquisicion) "
            + "VALUES (?, ?, ?, ?)";

    private static final String OBTENER_BIBLIOTECA_POR_ID_QUERY
            = "SELECT * FROM biblioteca_usuario WHERE id_biblioteca = ?";

    private static final String OBTENER_BIBLIOTECA_DE_USUARIO_QUERY
            = "SELECT * FROM biblioteca_usuario WHERE id_usuario = ?";

    private static final String ELIMINAR_BIBLIOTECA_QUERY
            = "DELETE FROM biblioteca_usuario WHERE id_biblioteca = ?";

    private static final String VERIFICAR_USUARIO_TIENE_VIDEOJUEGO_QUERY
            = "SELECT COUNT(*) FROM biblioteca_usuario WHERE id_usuario = ? AND id_videojuego = ?";

    private static final String OBTENER_BIBLIOTECA_CON_DETALLES_COMPRAVIDEOJUEGO_QUERY
            = "SELECT "
            + "    bu.id_biblioteca, "
            + "    bu.id_usuario, "
            + "    bu.tipo_adquisicion, "
            + "    c.id_compra, "
            + "    c.monto_pago, "
            + "    c.fecha_compra, "
            + "    c.comision_aplicada, "
            + "    c.id_usuario as compra_id_usuario, "
            + "    v.id_videojuego, "
            + "    v.id_empresa, "
            + "    v.titulo, "
            + "    v.precio, "
            + "    v.clasificacion_edad, "
            + "    v.fecha_lanzamiento "
            + "FROM biblioteca_usuario bu "
            + "JOIN compra c ON bu.id_compra = c.id_compra "
            + "JOIN videojuego v ON bu.id_videojuego = v.id_videojuego "
            + "WHERE bu.id_usuario = ? "
            + "ORDER BY c.fecha_compra DESC";  

    private static final String SELECT_DETAIL_BY_ID_SIMPLE_QUERY
            = "SELECT "
            + "    bu.id_biblioteca, "
            + "    bu.id_usuario, "
            + "    bu.tipo_adquisicion, "
            + "    c.id_compra, "
            + "    c.monto_pago, "
            + "    c.fecha_compra, "
            + "    c.comision_aplicada, "
            + "    c.id_usuario as compra_id_usuario, "
            + "    v.id_videojuego, "
            + "    v.id_empresa, "
            + "    v.titulo, "
            + "    v.precio, "
            + "    v.clasificacion_edad, "
            + "    v.fecha_lanzamiento "
            + "FROM biblioteca_usuario bu "
            + "JOIN compra c ON bu.id_compra = c.id_compra "
            + "JOIN videojuego v ON bu.id_videojuego = v.id_videojuego "
            + "WHERE bu.id_biblioteca = ?";

    // Query para búsqueda por título
    private static final String SEARCH_BY_TITLE_QUERY
            = "SELECT "
            + "    bu.id_biblioteca, "
            + "    bu.id_usuario, "
            + "    bu.tipo_adquisicion, "
            + "    c.id_compra, "
            + "    c.monto_pago, "
            + "    c.fecha_compra, "
            + "    c.comision_aplicada, "
            + "    c.id_usuario as compra_id_usuario, "
            + "    v.id_videojuego, "
            + "    v.id_empresa, "
            + "    v.titulo, "
            + "    v.precio, "
            + "    v.clasificacion_edad, "
            + "    v.fecha_lanzamiento "
            + "FROM biblioteca_usuario bu "
            + "JOIN compra c ON bu.id_compra = c.id_compra "
            + "JOIN videojuego v ON bu.id_videojuego = v.id_videojuego "
            + "WHERE bu.id_usuario = ? AND LOWER(v.titulo) LIKE LOWER(?) "
            + "ORDER BY c.fecha_compra DESC";

    // filtrar por tipo de adquisición
    private static final String SELECT_BY_ACQUISITION_TYPE_QUERY
            = "SELECT "
            + "    bu.id_biblioteca, "
            + "    bu.id_usuario, "
            + "    bu.tipo_adquisicion, "
            + "    c.id_compra, "
            + "    c.monto_pago, "
            + "    c.fecha_compra, "
            + "    c.comision_aplicada, "
            + "    c.id_usuario as compra_id_usuario, "
            + "    v.id_videojuego, "
            + "    v.id_empresa, "
            + "    v.titulo, "
            + "    v.precio, "
            + "    v.clasificacion_edad, "
            + "    v.fecha_lanzamiento "
            + "FROM biblioteca_usuario bu "
            + "JOIN compra c ON bu.id_compra = c.id_compra "
            + "JOIN videojuego v ON bu.id_videojuego = v.id_videojuego "
            + "WHERE bu.id_usuario = ? AND bu.tipo_adquisicion = ? "
            + "ORDER BY c.fecha_compra DESC";


    /**
     * Crear un registro de biblioteca con transaccion
     * @param connection
     * @param biblioteca
     * @return
     * @throws SQLException 
     */
    public Biblioteca insertBiblioteca(Connection connection, Biblioteca biblioteca)
            throws SQLException {

        try (PreparedStatement ps = connection.prepareStatement(
                CREAR_BIBLIOTECA_QUERY, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, biblioteca.getId_usuario());
            ps.setInt(2, biblioteca.getId_videojuego());
            ps.setInt(3, biblioteca.getId_compra());
            ps.setString(4, biblioteca.getTipo_adquisicion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el registro en la biblioteca");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    biblioteca.setId_biblioteca(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID generado");
                }
            }

            return biblioteca;
        }
    }

    /**
     * Inserta un nuevo registro en la biblioteca sin transaccion
     * @param biblioteca
     * @return
     * @throws SQLException 
     */
    public Biblioteca insertBibliotecaManual(Biblioteca biblioteca) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(CREAR_BIBLIOTECA_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, biblioteca.getId_usuario());
            ps.setInt(2, biblioteca.getId_videojuego());
            ps.setInt(3, biblioteca.getId_compra());
            ps.setString(4, biblioteca.getTipo_adquisicion());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo insertar el registro en la biblioteca");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    biblioteca.setId_biblioteca(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("No se pudo obtener el ID generado");
                }
            }

            return biblioteca;
        }
    }

    /**
     * Obtiene un registro de biblioteca por ID
     * @param idBiblioteca
     * @return
     * @throws SQLException 
     */
    public Biblioteca getBibliotecaById(int idBiblioteca) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(OBTENER_BIBLIOTECA_POR_ID_QUERY)) {
            ps.setInt(1, idBiblioteca);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBiblioteca(rs);
                }
                return null;
            }
        }
    }

    /**
     * Obtiene todos los registros de biblioteca de un usuario
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public List<Biblioteca> getBibliotecasByUserId(int idUsuario) throws SQLException {
        List<Biblioteca> bibliotecas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(OBTENER_BIBLIOTECA_DE_USUARIO_QUERY)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bibliotecas.add(mapResultSetToBiblioteca(rs));
                }
            }
        }

        return bibliotecas;
    }

    /**
     * Elimina un registro de la biblioteca
     * @param idBiblioteca
     * @return
     * @throws SQLException 
     */
    public boolean deleteBiblioteca(int idBiblioteca) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(ELIMINAR_BIBLIOTECA_QUERY)) {
            ps.setInt(1, idBiblioteca);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Verifica si un usuario ya tiene un juego en su biblioteca
     * @param idUsuario
     * @param idVideojuego
     * @return
     * @throws SQLException 
     */
    public boolean existsByUserAndGame(int idUsuario, int idVideojuego) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(VERIFICAR_USUARIO_TIENE_VIDEOJUEGO_QUERY)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idVideojuego);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }

    /**
     * Obtiene la biblioteca de un usuario con detalles (DTOs CompraMiniResponse y VideoJuegominiResponse)
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public List<BibliotecaResponse> getBibliotecaWithDetails(int idUsuario) throws SQLException {
        List<BibliotecaResponse> bibliotecas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(OBTENER_BIBLIOTECA_CON_DETALLES_COMPRAVIDEOJUEGO_QUERY)) {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bibliotecas.add(mapResultSetToBibliotecaResponse(rs));
                }
            }
        }

        return bibliotecas;
    }

    /**
     * Obtiene un registro específico con detalles (DTO)
     * @param idBiblioteca
     * @return
     * @throws SQLException 
     */
    public BibliotecaResponse getBibliotecaDetailById(int idBiblioteca) throws SQLException {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_DETAIL_BY_ID_SIMPLE_QUERY)) {
            ps.setInt(1, idBiblioteca);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBibliotecaResponse(rs);
                }
                return null;
            }
        }
    }

    /**
     * Busca juegos en la biblioteca por título
     * @param idUsuario
     * @param searchTerm
     * @return
     * @throws SQLException 
     */
    public List<BibliotecaResponse> searchByTitle(int idUsuario, String searchTerm) throws SQLException {
        List<BibliotecaResponse> bibliotecas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(SEARCH_BY_TITLE_QUERY)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, "%" + searchTerm + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bibliotecas.add(mapResultSetToBibliotecaResponse(rs));
                }
            }
        }

        return bibliotecas;
    }

    /**
     * Obtiene juegos filtrados por tipo de adquisición
     * @param idUsuario
     * @param tipoAdquisicion
     * @return
     * @throws SQLException 
     */
    public List<BibliotecaResponse> getByAcquisitionType(int idUsuario, String tipoAdquisicion) throws SQLException {
        List<BibliotecaResponse> bibliotecas = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement ps = connection.prepareStatement(SELECT_BY_ACQUISITION_TYPE_QUERY)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, tipoAdquisicion);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bibliotecas.add(mapResultSetToBibliotecaResponse(rs));
                }
            }
        }

        return bibliotecas;
    }



    /**
     * Mapea ResultSet a Biblioteca
     */
    private Biblioteca mapResultSetToBiblioteca(ResultSet rs) throws SQLException {
        Biblioteca biblioteca = new Biblioteca();
        biblioteca.setId_biblioteca(rs.getInt("id_biblioteca"));
        biblioteca.setId_usuario(rs.getInt("id_usuario"));
        biblioteca.setId_videojuego(rs.getInt("id_videojuego"));
        biblioteca.setId_compra(rs.getInt("id_compra"));
        biblioteca.setTipo_adquisicion(rs.getString("tipo_adquisicion"));

        return biblioteca;
    }

    /**
     * Mapea ResultSet a BibliotecaResponse (DTOs)
     */
    private BibliotecaResponse mapResultSetToBibliotecaResponse(ResultSet rs) throws SQLException {
        BibliotecaResponse response = new BibliotecaResponse();

        response.setId_biblioteca(rs.getInt("id_biblioteca"));
        response.setId_usuario(rs.getInt("id_usuario"));
        response.setTipo_adquisicion(rs.getString("tipo_adquisicion"));

        // Crear CompraMiniResponse
        CompraMiniResponse compraMini = new CompraMiniResponse();
        compraMini.setId_compra(rs.getInt("id_compra"));
        compraMini.setMonto_pago(rs.getDouble("monto_pago"));
        compraMini.setFecha_compra(rs.getDate("fecha_compra"));
        compraMini.setComision_aplicada(rs.getDouble("comision_aplicada"));
        compraMini.setId_usuario(rs.getInt("compra_id_usuario"));
        response.setCompra(compraMini);

        // Crear VideojuegoMiniResponse
        VideojuegoMiniResponse videojuegoMini = new VideojuegoMiniResponse();
        videojuegoMini.setId_videojuego(rs.getInt("id_videojuego"));
        videojuegoMini.setId_empresa(rs.getInt("id_empresa"));
        videojuegoMini.setTitulo(rs.getString("titulo"));
        videojuegoMini.setPrecio(rs.getBigDecimal("precio"));
        videojuegoMini.setClasificacion_edad(rs.getString("clasificacion_edad"));
        videojuegoMini.setFecha_lanzamiento(rs.getDate("fecha_lanzamiento"));
        response.setVideojuego(videojuegoMini);

        return response;
    }


}
