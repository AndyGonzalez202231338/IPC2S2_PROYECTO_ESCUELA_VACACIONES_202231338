package db;

import conexion.DBConnectionSingleton;
import empresa.models.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import user.models.Rol;
import user.models.Usuario;

public class UsersDB {

    private static final String CREAR_USUARIO_QUERY
            = "INSERT INTO usuario (correo, id_rol, id_empresa, nombre, password, "
            + "fecha_nacimiento, pais, telefono, saldo_cartera, avatar) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String ASIGNAR_EMPRESA_USUARIO_QUERY
            = "UPDATE usuario SET id_empresa = ? WHERE id_usuario = ?";

private static final String ADMINISTRADORES_SIN_EMPRESA_QUERY = 
    "SELECT u.*, " +
    "r.id_rol AS rol_id, " +
    "r.nombre AS rol_nombre, " +
    "r.descripcion AS rol_descripcion, " +
    "e.id_empresa AS empresa_id, " + 
    "e.nombre AS empresa_nombre, " +
    "e.descripcion AS empresa_descripcion " +
    "FROM usuario u " +
    "INNER JOIN rol r ON u.id_rol = r.id_rol " +
    "LEFT JOIN empresa e ON u.id_empresa = e.id_empresa " +
    "WHERE u.id_rol = 2 " +
    "AND (u.id_empresa IS NULL OR u.id_empresa = 0) " +
    "ORDER BY u.nombre ASC";

private static final String ENCONTRAR_USUARIO_POR_ID_QUERY
        = "SELECT u.*, " 
        + "r.id_rol as rol_id, r.nombre as rol_nombre, r.descripcion as rol_descripcion, "
        + "e.id_empresa as empresa_id, e.nombre as empresa_nombre, e.descripcion as empresa_descripcion "
        + "FROM usuario u "
        + "LEFT JOIN rol r ON u.id_rol = r.id_rol "
        + "LEFT JOIN empresa e ON u.id_empresa = e.id_empresa "
        + "WHERE u.id_usuario = ?";

    private static final String ENCONTRAR_USUARIO_POR_EMAIL_QUERY
            = "SELECT u.*, r.id_rol AS rol_id, r.nombre AS rol_nombre, r.descripcion AS rol_descripcion, "
            + "e.id_empresa AS empresa_id, e.nombre AS empresa_nombre, e.descripcion AS empresa_descripcion "
            + "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol "
            + "LEFT JOIN empresa e ON u.id_empresa = e.id_empresa "
            + "WHERE u.correo = ?";

    private static final String TODOS_LOS_USUARIOS_QUERY
            = "SELECT u.*, r.nombre_rol as nombre_rol, r.descripcion as rol_descripcion "
            + "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol";

    private static final String USUARIOS_ANUNCIANTES_QUERY
            = "SELECT u.*, r.nombre_rol AS nombre_rol, r.descripcion AS rol_descripcion "
            + "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol "
            + "WHERE r.nombre_rol = 'ANUNCIANTE'";

    private static final String ACTUALIZAR_USUARIO_POR_ID_QUERY
            = "UPDATE usuario SET correo = ?, id_rol = ?, id_empresa = ?, nombre = ?, password = ?, fecha_nacimiento = ?, pais = ?, telefono = ? saldo_cartera = ? avatar = ?  WHERE id_usuario = ?";

    private static final String ACTUALIZAR_USUARIO_POR_EMAIL_QUERY
            = "UPDATE usuario SET id_rol = ?, id_empresa = ?, nombre = ?, password = ?, fecha_nacimiento = ?, pais = ?, telefono = ? saldo_cartera = ? avatar = ? WHERE correo = ?";

    private static final String ELIMINAR_USUARIO_POR_ID_QUERY
            = "DELETE FROM usuario WHERE id_usuario = ?";

    private static final String EXISTE_USUARIO_POR_ID_QUERY
            = "SELECT 1 FROM usuario WHERE id_usuario = ?";

    /**
     * Crea un nuevo usuario en la base de datos
     */
    public Usuario createUser(Usuario newUser) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement insert = connection.prepareStatement(CREAR_USUARIO_QUERY,
                Statement.RETURN_GENERATED_KEYS)) {

            insert.setString(1, newUser.getCorreo());
            insert.setInt(2, newUser.getId_rol());

            //id_empresa - NULL
            if (newUser.getId_empresa() > 0) {
                insert.setInt(3, newUser.getId_empresa());
            } else {
                insert.setNull(3, Types.INTEGER);
            }

            insert.setString(4, newUser.getNombre());
            insert.setString(5, newUser.getPassword());

            // fecha_nacimiento - NULL
            if (newUser.getFecha_nacimiento() != null) {
                insert.setDate(6, newUser.getFecha_nacimiento());
            } else {
                insert.setNull(6, Types.DATE);
            }

            // pais - NULL
            if (newUser.getPais() != null && !newUser.getPais().isEmpty()) {
                insert.setString(7, newUser.getPais());
            } else {
                insert.setNull(7, Types.VARCHAR);
            }

            // telefono - NULL
            if (newUser.getTelefono() != null && !newUser.getTelefono().isEmpty()) {
                insert.setString(8, newUser.getTelefono());
            } else {
                insert.setNull(8, Types.VARCHAR);
            }

            insert.setDouble(9, newUser.getSaldo_cartera());

            // avatar - NULL
            byte[] avatarBytes = convertToPrimitiveBytes(newUser.getAvatar());
            if (avatarBytes != null) {
                insert.setBytes(10, avatarBytes);
            } else {
                insert.setNull(10, Types.BLOB);
            }

            int affectedRows = insert.executeUpdate();
            System.out.println("Filas afectadas: " + affectedRows);

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        newUser.setIdUsuario(newId);
                        System.out.println("ID generado: " + newId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear usuario: " + e.getMessage(), e);
        }

        return newUser;
    }

    public List<Usuario> getAdministradoresSinEmpresa() {
        List<Usuario> administradores = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement query = connection.prepareStatement(ADMINISTRADORES_SIN_EMPRESA_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Usuario admin = mapResultSetToUser(resultSet);
                administradores.add(admin);
            }

            System.out.println("Administradores sin empresa encontrados: " + administradores.size());

        } catch (SQLException e) {
            System.out.println("Error al obtener administradores sin empresa: " + e.getMessage());
            e.printStackTrace();
        }

        return administradores;
    }

    public boolean asignarEmpresaAUsuario(int idUsuario, int idEmpresa) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement update = connection.prepareStatement(ASIGNAR_EMPRESA_USUARIO_QUERY)) {
            update.setInt(1, idEmpresa);
            update.setInt(2, idUsuario);

            int filasAfectadas = update.executeUpdate();
            System.out.println("Filas afectadas al asignar empresa: " + filasAfectadas);

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al asignar empresa a usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public Usuario getAdministradorDeEmpresa(int idEmpresa) {
        String query = "SELECT u.*, r.id_rol, r.nombre as rol_nombre, r.descripcion as rol_descripcion "
                + "FROM usuario u INNER JOIN rol r ON u.id_rol = r.id_rol "
                + "WHERE u.id_empresa = ? AND u.id_rol = 2 "
                + // Solo administradores (rol = 2)
                "LIMIT 1";  // Solo el primero (debería haber solo uno)

        Connection connection = DBConnectionSingleton.getInstance().getConnection();

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idEmpresa);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return mapResultSetToUser(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Verifica si existe un usuario por su ID
     */
    public boolean existsUser(int idUsuario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement(EXISTE_USUARIO_POR_ID_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet result = query.executeQuery();
            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<Usuario> getAllUsers() {
        List<Usuario> users = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement(TODOS_LOS_USUARIOS_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Usuario user = mapResultSetToUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Obtiene todos los usuarios de tipo anunciantes
     */
    public List<Usuario> getAllUsersAnunciante() {
        List<Usuario> users = new ArrayList<>();
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement(USUARIOS_ANUNCIANTES_QUERY)) {
            ResultSet resultSet = query.executeQuery();

            while (resultSet.next()) {
                Usuario user = mapResultSetToUser(resultSet);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Obtiene un usuario por ID
     */
    public Optional<Usuario> getById(int idUsuario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement(ENCONTRAR_USUARIO_POR_ID_QUERY)) {
            query.setInt(1, idUsuario);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                Usuario user = mapResultSetToUser(resultSet);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Obtiene un usuario por email
     */
    public Optional<Usuario> getByEmail(String email) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement query = connection.prepareStatement(ENCONTRAR_USUARIO_POR_EMAIL_QUERY)) {
            query.setString(1, email);
            ResultSet resultSet = query.executeQuery();

            if (resultSet.next()) {
                Usuario user = mapResultSetToUser(resultSet);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Actualiza un usuario por ID
     */
    public Usuario updateUser(Integer idUsuario, Usuario userToUpdate) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement updateStmt = connection.prepareStatement(ACTUALIZAR_USUARIO_POR_ID_QUERY)) {
            updateStmt.setString(1, userToUpdate.getCorreo());
            updateStmt.setInt(2, userToUpdate.getRol().getId_rol());
            updateStmt.setInt(3, userToUpdate.getEmpresa().getId_empresa());
            updateStmt.setString(4, userToUpdate.getNombre());
            updateStmt.setString(5, userToUpdate.getPassword());
            updateStmt.setDate(6, userToUpdate.getFecha_nacimiento());
            updateStmt.setString(7, userToUpdate.getPais());
            updateStmt.setString(8, userToUpdate.getTelefono());
            updateStmt.setDouble(9, userToUpdate.getSaldo_cartera());
            updateStmt.setBytes(10, convertToPrimitiveBytes(userToUpdate.getAvatar()));
            updateStmt.setInt(11, idUsuario);

            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userToUpdate;
    }

    /**
     * Actualiza un usuario por email
     */
    public Usuario updateUserByEmail(String email, Usuario userToUpdate) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement updateStmt = connection.prepareStatement(ACTUALIZAR_USUARIO_POR_EMAIL_QUERY)) {

            updateStmt.setInt(1, userToUpdate.getRol().getId_rol());
            updateStmt.setInt(2, userToUpdate.getEmpresa().getId_empresa());
            updateStmt.setString(3, userToUpdate.getNombre());
            updateStmt.setString(4, userToUpdate.getPassword());
            updateStmt.setDate(5, userToUpdate.getFecha_nacimiento());
            updateStmt.setString(6, userToUpdate.getPais());
            updateStmt.setString(7, userToUpdate.getTelefono());
            updateStmt.setDouble(8, userToUpdate.getSaldo_cartera());
            updateStmt.setBytes(9, convertToPrimitiveBytes(userToUpdate.getAvatar()));
            updateStmt.setString(10, email);

            updateStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userToUpdate;
    }

    /**
     * Elimina un usuario por ID
     */
    public boolean deleteUser(int idUsuario) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement deleteStmt = connection.prepareStatement(ELIMINAR_USUARIO_POR_ID_QUERY)) {

            deleteStmt.setInt(1, idUsuario);
            int affectedRows = deleteStmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Método auxiliar para mapear un ResultSet a un objeto User
     */
    private Usuario mapResultSetToUser(ResultSet resultSet) throws SQLException {

        // Mapear Rol con alias correctos
        Rol role = new Rol(
                resultSet.getInt("rol_id"),
                resultSet.getString("rol_nombre"),
                resultSet.getString("rol_descripcion")
        );

        // Mapear Empresa (puede ser null) - ¡CORRECCIÓN AQUÍ!
        Empresa empresa = null;
        int empresaId = resultSet.getInt("empresa_id");  // Cambia "empesa_id" a "empresa_id"

        // Solo crear empresa si tiene id (no es 0)
        if (empresaId > 0) {
            empresa = new Empresa(
                    empresaId,
                    resultSet.getString("empresa_nombre"),
                    resultSet.getString("empresa_descripcion")
            );
        }

        // Crear el usuario
        Usuario user = new Usuario(
                resultSet.getInt("id_usuario"),
                resultSet.getString("correo"),
                role,
                empresa, // puede ser null
                resultSet.getString("nombre"),
                resultSet.getString("password"),
                resultSet.getDate("fecha_nacimiento"),
                resultSet.getString("pais"),
                resultSet.getString("telefono"),
                resultSet.getDouble("saldo_cartera"),
                convertToByteObjects(resultSet.getBytes("avatar"))
        );

        return user;
    }

    public boolean existsUserByEmail(String correo) {
        Connection connection = DBConnectionSingleton.getInstance().getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT 1 FROM usuario WHERE correo = ?")) {
            statement.setString(1, correo);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private byte[] convertToPrimitiveBytes(Byte[] byteObjects) {
        if (byteObjects == null) {
            return null;
        }

        byte[] bytes = new byte[byteObjects.length];
        for (int i = 0; i < byteObjects.length; i++) {
            bytes[i] = byteObjects[i];
        }
        return bytes;
    }

    private Byte[] convertToByteObjects(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        Byte[] byteObjects = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteObjects[i] = bytes[i];
        }
        return byteObjects;
    }
}
