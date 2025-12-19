package db;

import conexion.DBConnectionSingleton;
import instalacion.dtos.JuegoPrestable;
import instalacion.models.InstalacionJuego;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstalacionJuegoDB {

    public List<InstalacionJuego> getAllInstalaciones() {
        List<InstalacionJuego> instalaciones = new ArrayList<>();
        String sql = "SELECT * FROM instalacion_juego";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                InstalacionJuego instalacion = new InstalacionJuego(
                        rs.getInt("id_instalacion"),
                        rs.getInt("id_biblioteca"),
                        rs.getInt("id_videojuego"),
                        rs.getString("estado"),
                        rs.getString("tipo_adquisicion")
                );
                instalaciones.add(instalacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instalaciones;
    }

    public InstalacionJuego getInstalacionById(int id) {
        String sql = "SELECT * FROM instalacion_juego WHERE id_instalacion = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new InstalacionJuego(
                        rs.getInt("id_instalacion"),
                        rs.getInt("id_biblioteca"),
                        rs.getInt("id_videojuego"),
                        rs.getString("estado"),
                        rs.getString("tipo_adquisicion")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public InstalacionJuego createInstalacion(InstalacionJuego instalacion) {
        String sql = "INSERT INTO instalacion_juego (id_biblioteca, id_videojuego, estado, tipo_adquisicion) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, instalacion.getId_biblioteca());
            pstmt.setInt(2, instalacion.getId_videojuego());
            pstmt.setString(3, instalacion.getEstado());
            pstmt.setString(4, instalacion.getTipo_adquisicion());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        instalacion.setId_instalacion(generatedKeys.getInt(1));
                        return instalacion;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateInstalacion(InstalacionJuego instalacion) {
        String sql = "UPDATE instalacion_juego SET id_biblioteca = ?, id_videojuego = ?, "
                + "estado = ?, tipo_adquisicion = ? WHERE id_instalacion = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, instalacion.getId_biblioteca());
            pstmt.setInt(2, instalacion.getId_videojuego());
            pstmt.setString(3, instalacion.getEstado());
            pstmt.setString(4, instalacion.getTipo_adquisicion());
            pstmt.setInt(5, instalacion.getId_instalacion());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteInstalacion(int id) {
        String sql = "DELETE FROM instalacion_juego WHERE id_instalacion = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getUsuarioDeBiblioteca(int id_biblioteca) {
        String sql = "SELECT id_usuario FROM biblioteca_usuario WHERE id_biblioteca = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_biblioteca);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_usuario");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getTipoAdquisicionDeBiblioteca(int id_biblioteca) {
        String sql = "SELECT tipo_adquisicion FROM biblioteca_usuario WHERE id_biblioteca = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_biblioteca);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("tipo_adquisicion");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean tieneInstalacionPrestadaActiva(int id_usuario) {
        String sql = "SELECT COUNT(*) FROM instalacion_juego ij "
                + "JOIN biblioteca_usuario bu ON ij.id_biblioteca = bu.id_biblioteca "
                + "WHERE bu.id_usuario = ? AND ij.tipo_adquisicion = 'PRESTAMO' AND ij.estado = 'INSTALADO'";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existeBibliotecaParaUsuarioYJuego(int id_usuario, int id_videojuego, String tipo_adquisicion) {
        String sql = "SELECT COUNT(*) FROM biblioteca_usuario "
                + "WHERE id_usuario = ? AND id_videojuego = ? AND tipo_adquisicion = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario);
            pstmt.setInt(2, id_videojuego);
            pstmt.setString(3, tipo_adquisicion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getIdBibliotecaParaUsuarioYJuego(int id_usuario, int id_videojuego, String tipo_adquisicion) {
        String sql = "SELECT id_biblioteca FROM biblioteca_usuario "
                + "WHERE id_usuario = ? AND id_videojuego = ? AND tipo_adquisicion = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario);
            pstmt.setInt(2, id_videojuego);
            pstmt.setString(3, tipo_adquisicion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_biblioteca");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<InstalacionJuego> getInstalacionesByUsuario(int id_usuario) {
        List<InstalacionJuego> instalaciones = new ArrayList<>();
        String sql = "SELECT ij.* FROM instalacion_juego ij "
                + "JOIN biblioteca_usuario bu ON ij.id_biblioteca = bu.id_biblioteca "
                + "WHERE bu.id_usuario = ?";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                InstalacionJuego instalacion = new InstalacionJuego(
                        rs.getInt("id_instalacion"),
                        rs.getInt("id_biblioteca"),
                        rs.getInt("id_videojuego"),
                        rs.getString("estado"),
                        rs.getString("tipo_adquisicion")
                );
                instalaciones.add(instalacion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instalaciones;
    }

    public List<JuegoPrestable> getJuegosPrestablesPorGrupo(int id_usuario_solicitante) {
        List<JuegoPrestable> juegosPrestables = new ArrayList<>();


        String sql = "SELECT "
                + "v.id_videojuego, "
                + "v.titulo, "
                + "v.descripcion, "
                + "v.clasificacion_edad, "
                + "u.id_usuario as id_propietario, "
                + "u.nombre as nombre_propietario, "
                + "g.id_grupo, "
                + "g.nombre as nombre_grupo, "
                + "0 as ya_prestado "
                + // Por defecto false (0)
                "FROM grupo_usuario gu_solicitante "
                + "INNER JOIN grupo g ON gu_solicitante.id_grupo = g.id_grupo "
                + "INNER JOIN grupo_usuario gu_propietario ON g.id_grupo = gu_propietario.id_grupo "
                + "INNER JOIN usuario u ON gu_propietario.id_usuario = u.id_usuario "
                + "INNER JOIN biblioteca_usuario bu ON u.id_usuario = bu.id_usuario "
                + "INNER JOIN videojuego v ON bu.id_videojuego = v.id_videojuego "
                + "WHERE gu_solicitante.id_usuario = ? "
                + "AND gu_propietario.id_usuario != ? "
                + // Excluir al propio usuario
                "AND bu.tipo_adquisicion = 'COMPRA' "
                + // Solo juegos comprados
                "AND NOT EXISTS ( "
                + // El usuario no tiene este juego comprado
                "    SELECT 1 FROM biblioteca_usuario bu2 "
                + "    WHERE bu2.id_usuario = ? "
                + "    AND bu2.id_videojuego = v.id_videojuego "
                + "    AND bu2.tipo_adquisicion = 'COMPRA' "
                + ") "
                + "ORDER BY g.nombre, u.nombre, v.titulo";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario_solicitante);
            pstmt.setInt(2, id_usuario_solicitante);
            pstmt.setInt(3, id_usuario_solicitante);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                JuegoPrestable juego = new JuegoPrestable(
                        rs.getInt("id_videojuego"),
                        rs.getString("titulo"),
                        rs.getInt("id_propietario"),
                        rs.getString("nombre_propietario"),
                        rs.getInt("id_grupo"),
                        rs.getString("nombre_grupo"),
                        rs.getString("clasificacion_edad"),
                        rs.getString("descripcion"),
                        rs.getBoolean("ya_prestado")
                );
                juegosPrestables.add(juego);
            }

            System.out.println("Encontrados " + juegosPrestables.size() + " juegos prestables para usuario " + id_usuario_solicitante);

        } catch (SQLException e) {
            System.err.println("Error en getJuegosPrestablesPorGrupo:");
            e.printStackTrace();
            throw new RuntimeException("Error de base de datos: " + e.getMessage(), e);
        }
        return juegosPrestables;
    }

    /**
     * Obtener juegos prestables agrupados por grupo
     * @param id_usuario_solicitante
     * @return 
     */
    public List<JuegoPrestable> getJuegosPrestablesAgrupados(int id_usuario_solicitante) {
        return getJuegosPrestablesPorGrupo(id_usuario_solicitante);
    }

    /**
     * Método para crear un préstamo en la biblioteca
     * @param id_usuario_prestatario
     * @param id_videojuego
     * @param id_usuario_propietario
     * @return 
     */
    public boolean crearPrestamo(int id_usuario_prestatario, int id_videojuego, int id_usuario_propietario) {
        String sql = "INSERT INTO biblioteca_usuario (id_usuario, id_videojuego, id_compra, tipo_adquisicion) "
                + "VALUES (?, ?, 0, 'PRESTAMO')";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario_prestatario);
            pstmt.setInt(2, id_videojuego);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

// Verificar si un usuario puede prestar un juego específico
    public boolean puedePrestarJuego(int id_usuario_solicitante, int id_videojuego, int id_usuario_propietario) {
        String sql = "SELECT COUNT(*) FROM grupo_usuario gu1 "
                + "JOIN grupo_usuario gu2 ON gu1.id_grupo = gu2.id_grupo "
                + "JOIN biblioteca_usuario bu ON gu2.id_usuario = bu.id_usuario "
                + "WHERE gu1.id_usuario = ? "
                + "AND gu2.id_usuario = ? "
                + "AND bu.id_videojuego = ? "
                + "AND bu.tipo_adquisicion = 'COMPRA' "
                + "AND NOT EXISTS ( "
                + "    SELECT 1 FROM biblioteca_usuario bu_existente "
                + "    WHERE bu_existente.id_usuario = ? "
                + "    AND bu_existente.id_videojuego = ? "
                + "    AND bu_existente.tipo_adquisicion = 'PRESTAMO'"
                + ")";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario_solicitante);
            pstmt.setInt(2, id_usuario_propietario);
            pstmt.setInt(3, id_videojuego);
            pstmt.setInt(4, id_usuario_solicitante);
            pstmt.setInt(5, id_videojuego);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer getIdBibliotecaPrestamo(int id_usuario, int id_videojuego) {
        String sql = "SELECT id_biblioteca FROM biblioteca_usuario "
                + "WHERE id_usuario = ? AND id_videojuego = ? AND tipo_adquisicion = 'PRESTAMO' "
                + "ORDER BY id_biblioteca DESC LIMIT 1";

        try (Connection conn = DBConnectionSingleton.getInstance().getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id_usuario);
            pstmt.setInt(2, id_videojuego);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id_biblioteca");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verificarConexion() {
        try (Connection conn = DBConnectionSingleton.getInstance().getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
