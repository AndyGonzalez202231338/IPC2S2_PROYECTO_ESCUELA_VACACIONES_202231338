/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compra.services;

import biblioteca.models.Biblioteca;
import compra.models.Compra;
import videojuego.models.Videojuego;
import conexion.DBConnectionSingleton;
import db.BibliotecaDB;
import db.CompraDB;
import db.SistemaDB;
import db.TransaccionDB;
import db.UsersDB;
import db.VideojuegoDB;
import exceptions.ComisionNoEncontradaException;
import exceptions.CompraDataInvalidException;
import exceptions.EdadNoValidaException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.SaldoInsuficienteException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;

import java.util.Optional;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class TransaccionService {

    private final TransaccionDB transaccionDB;
    private final CompraDB compraDB;
    private final UsersDB usuarioDB;
    private final VideojuegoDB videojuegoDB;
    private final CalculadoraComisionService calculadoraComision;
    private final SistemaDB sistemaDB;
    private final BibliotecaDB bibliotecaDB;

    public TransaccionService() {
        this.transaccionDB = new TransaccionDB();
        this.compraDB = new CompraDB();
        this.usuarioDB = new UsersDB();
        this.videojuegoDB = new VideojuegoDB();
        this.calculadoraComision = new CalculadoraComisionService();
        this.sistemaDB = new SistemaDB();
        this.bibliotecaDB = new BibliotecaDB();
    }

    /**
     * Procesar compra completa con comisión automática
     *
     * @param idUsuario
     * @param idVideojuego
     * @param fechaCompra
     * @return
     * @throws EntityNotFoundException
     * @throws SaldoInsuficienteException
     * @throws EntityAlreadyExistsException
     * @throws CompraDataInvalidException
     * @throws ComisionNoEncontradaException
     * @throws SQLException
     */
    public Compra procesarCompraConTransaccion(int idUsuario, int idVideojuego, Date fechaCompra)
            throws EntityNotFoundException, SaldoInsuficienteException,
            EntityAlreadyExistsException, CompraDataInvalidException,
            ComisionNoEncontradaException, EdadNoValidaException, SQLException {

        Connection connection = null;

        try {
            connection = DBConnectionSingleton.getInstance().getConnection();
            connection.setAutoCommit(false);

            
            validarPreTransaccion(idUsuario, idVideojuego, fechaCompra);

            Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
            Usuario usuario = optionalUsuario.orElse(null);

            Videojuego videojuego = videojuegoDB.getVideojuegoById(idVideojuego, false);
            double precio = videojuego.getPrecio().doubleValue();

            // Calcular comisión 
            double montoComision = calculadoraComision.calcularMontoComision(
                    idVideojuego, precio, fechaCompra
            );

            if (!transaccionDB.verificarSaldoSuficiente(connection, idUsuario, precio)) {
                throw new SaldoInsuficienteException(
                        String.format("Saldo insuficiente. Monto requerido: $%.2f",
                                videojuego.getPrecio())
                );
            }

            Compra compra = new Compra();
            compra.setId_usuario(idUsuario);
            compra.setId_videojuego(idVideojuego);
            compra.setMonto_pago(precio);
            compra.setFecha_compra(fechaCompra);
            compra.setComision_aplicada(montoComision);

            int idCompra = transaccionDB.insertarCompra(connection, compra);
            compra.setId_compra(idCompra);

            // Crear registro de biblioteca
            Biblioteca biblioteca = new Biblioteca(
                    idUsuario,
                    idVideojuego,
                    idCompra,
                    "COMPRA"
            );

            bibliotecaDB.insertBiblioteca(connection, biblioteca);

            double nuevoSaldo = usuario.getSaldo_cartera() - precio;
            transaccionDB.actualizarSaldoUsuario(connection, idUsuario, nuevoSaldo);

            connection.commit();
            compra.setUsuario(usuario);
            compra.setVideojuego(videojuego);

            return compra;

        } catch (SQLException | EdadNoValidaException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Valida la edad del usuario para comprar un videojuego segun su
     * clasificacion y la edad adolecente del sistema
     *
     * @param idUsuario
     * @param idVideojuego
     * @throws EntityNotFoundException
     * @throws EdadNoValidaException
     * @throws SQLException
     */
    private void validarEdadParaCompra(int idUsuario, int idVideojuego)
            throws EntityNotFoundException, EdadNoValidaException, SQLException {

        Optional<Usuario> optionalUsuario = usuarioDB.getById(idUsuario);
        if (!optionalUsuario.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }

        Usuario usuario = optionalUsuario.get();
        Date fechaNacimiento = usuario.getFecha_nacimiento();

        if (fechaNacimiento == null) {
            throw new EdadNoValidaException("El usuario no tiene fecha de nacimiento registrada");
        }

        // Obtener videojuego y su clasificación
        Videojuego videojuego = videojuegoDB.getVideojuegoById(idVideojuego, false);
        String clasificacion = videojuego.getClasificacion_edad();

        // Obtener configuración de edad para adolescentes
        String edadAdolescentesStr = sistemaDB.getValorConfiguracion("EDAD_ADOLESCENTES");
        int edadAdolescentes = 16;
        System.out.println("edad del sistema" + edadAdolescentesStr);

        if (edadAdolescentesStr != null && !edadAdolescentesStr.isEmpty()) {
            try {
                edadAdolescentes = Integer.parseInt(edadAdolescentesStr);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear EDAD_ADOLESCENTES, usando valor por defecto: " + e.getMessage());
            }
        }

        int edadUsuario = calcularEdad(fechaNacimiento);

        switch (clasificacion) {
            case "E":
                // No hay restricción de edad
                break;

            case "T":
                if (edadUsuario < edadAdolescentes) {
                    throw new EdadNoValidaException(
                            String.format("El usuario tiene %d años. Para juegos clasificación T (Adolescentes) se requiere tener al menos %d años.",
                                    edadUsuario, edadAdolescentes)
                    );
                }
                break;

            case "M":
                int edadAdultos = 18; // Edad fija para adultos
                if (edadUsuario < edadAdultos) {
                    throw new EdadNoValidaException(
                            String.format("El usuario tiene %d años. Para juegos clasificación M (Adultos) se requiere tener al menos 18 años.",
                                    edadUsuario)
                    );
                }
                break;

            default:
                throw new EdadNoValidaException("Clasificación de edad no válida: " + clasificacion);
        }
    }

    /**
     * Calcula la edad basada en la fecha de nacimiento
     *
     * @param fechaNacimiento
     * @return
     */
    private int calcularEdad(Date fechaNacimiento) {
        Calendar fechaNac = Calendar.getInstance();
        fechaNac.setTime(fechaNacimiento);

        Calendar hoy = Calendar.getInstance();

        int edad = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
        if (hoy.get(Calendar.MONTH) < fechaNac.get(Calendar.MONTH)) {
            edad--;
        } else if (hoy.get(Calendar.MONTH) == fechaNac.get(Calendar.MONTH)
                && hoy.get(Calendar.DAY_OF_MONTH) < fechaNac.get(Calendar.DAY_OF_MONTH)) {
            edad--;
        }

        System.out.println("Edad del usuario: " + edad);

        return edad;
    }

    /**
     * Todas las validaciones necessarias antes de registrar una compra
     *
     * @param idUsuario
     * @param idVideojuego
     * @param fechaCompra
     * @throws EntityNotFoundException
     * @throws CompraDataInvalidException
     * @throws EdadNoValidaException
     * @throws SQLException
     */
    private void validarPreTransaccion(int idUsuario, int idVideojuego, Date fechaCompra)
            throws EntityNotFoundException, CompraDataInvalidException,
            EdadNoValidaException, SQLException {

        if (fechaCompra == null) {
            throw new CompraDataInvalidException("La fecha de compra no puede ser nula");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaCompra);
        Calendar hoy = Calendar.getInstance();

        if (cal.after(hoy)) {
            throw new CompraDataInvalidException("La fecha de compra no puede ser futura");
        }

        Optional<Usuario> usuario = usuarioDB.getById(idUsuario);
        if (!usuario.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }

        videojuegoDB.getVideojuegoById(idVideojuego, false);

        validarEdadParaCompra(idUsuario, idVideojuego);
    }

}
