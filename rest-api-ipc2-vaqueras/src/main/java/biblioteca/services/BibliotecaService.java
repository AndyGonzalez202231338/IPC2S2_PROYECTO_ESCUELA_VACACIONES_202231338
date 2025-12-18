/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package biblioteca.services;

import biblioteca.dtos.BibliotecaResponse;
import biblioteca.dtos.NewBibliotecaRequest;
import biblioteca.models.Biblioteca;
import compra.models.Compra;
import db.BibliotecaDB;
import db.CompraDB;
import db.UsersDB;
import db.VideojuegoDB;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.IllegalRequestException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class BibliotecaService {
    
    private final BibliotecaDB bibliotecaDB;
    private final UsersDB usersDB;
    private final VideojuegoDB videojuegoDB;
    private final CompraDB compraDB;
    
    public BibliotecaService() {
        this.bibliotecaDB = new BibliotecaDB();
        this.usersDB = new UsersDB();
        this.videojuegoDB = new VideojuegoDB();
        this.compraDB = new CompraDB();
    }
    
    /**
     * Agrega un juego a la biblioteca de un usuario de manera manual
     * @param request
     * @return
     * @throws EntityNotFoundException
     * @throws EntityAlreadyExistsException
     * @throws IllegalRequestException
     * @throws SQLException 
     */
    public BibliotecaResponse agregarABiblioteca(NewBibliotecaRequest request) 
            throws EntityNotFoundException, EntityAlreadyExistsException, IllegalRequestException, SQLException {
        
        if (request.getId_usuario() <= 0 || request.getId_videojuego() <= 0 || 
            request.getId_compra() <= 0) {
            throw new IllegalRequestException("Todos los IDs deben ser válidos");
        }
        
        if (request.getTipo_adquisicion() == null || 
            (!request.getTipo_adquisicion().equals("COMPRA") && 
             !request.getTipo_adquisicion().equals("PRESTAMO"))) {
            throw new IllegalRequestException("Tipo de adquisición debe ser 'COMPRA' o 'PRESTAMO'");
        }
        
        Optional<Usuario> usuarioOpt = usersDB.getById(request.getId_usuario());
        if (!usuarioOpt.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + request.getId_usuario());
        }
        
        videojuegoDB.getVideojuegoById(request.getId_videojuego(), false);
        
        Compra compraOpt = compraDB.getCompraById(request.getId_compra());
        if (compraOpt == null) {
            throw new EntityNotFoundException("Compra no encontrada con ID: " + request.getId_compra());
        }
        
        boolean juegoYaEnBiblioteca = bibliotecaDB.existsByUserAndGame(
            request.getId_usuario(), 
            request.getId_videojuego()
        );
        
        if (juegoYaEnBiblioteca) {
            throw new EntityAlreadyExistsException(
                "El usuario ya tiene este juego en su biblioteca"
            );
        }
        
        
        // Crear registro de biblioteca
        Biblioteca biblioteca = new Biblioteca(
            request.getId_usuario(),
            request.getId_videojuego(),
            request.getId_compra(),
            request.getTipo_adquisicion()
            
        );
        
        Biblioteca bibliotecaCreada = bibliotecaDB.insertBibliotecaManual(biblioteca);
        
        return bibliotecaDB.getBibliotecaDetailById(bibliotecaCreada.getId_biblioteca());
    }
    
    
    /**
     * Obtiene la biblioteca completa de un usuario
     * @param idUsuario
     * @return
     * @throws EntityNotFoundException
     * @throws SQLException 
     */
    public List<BibliotecaResponse> obtenerBibliotecaUsuario(int idUsuario) 
            throws EntityNotFoundException, SQLException {
        
        Optional<Usuario> usuarioOpt = usersDB.getById(idUsuario);
        if (!usuarioOpt.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        return bibliotecaDB.getBibliotecaWithDetails(idUsuario);
    }
    
    /**
     * Obtiene un registro específico de biblioteca
     * @param idBiblioteca
     * @return
     * @throws EntityNotFoundException
     * @throws SQLException 
     */
    public BibliotecaResponse obtenerRegistroBiblioteca(int idBiblioteca) 
            throws EntityNotFoundException, SQLException {
        
        BibliotecaResponse registro = bibliotecaDB.getBibliotecaDetailById(idBiblioteca);
        
        if (registro == null) {
            throw new EntityNotFoundException("Registro de biblioteca no encontrado con ID: " + idBiblioteca);
        }
        
        return registro;
    }
    
    /**
     * Verifica si un usuario tiene un juego específico en su biblioteca
     * @param idUsuario
     * @param idVideojuego
     * @return
     * @throws SQLException 
     */
    public boolean usuarioTieneJuego(int idUsuario, int idVideojuego) throws SQLException {
        return bibliotecaDB.existsByUserAndGame(idUsuario, idVideojuego);
    }
    
    /**
     * Elimina un juego de la biblioteca de un usuario
     * @param idBiblioteca
     * @return
     * @throws EntityNotFoundException
     * @throws SQLException 
     */
    public boolean eliminarDeBiblioteca(int idBiblioteca) 
            throws EntityNotFoundException, SQLException {
        
        // Verificar que el registro existe
        BibliotecaResponse registro = bibliotecaDB.getBibliotecaDetailById(idBiblioteca);
        if (registro == null) {
            throw new EntityNotFoundException("Registro de biblioteca no encontrado con ID: " + idBiblioteca);
        }
        
        
        return bibliotecaDB.deleteBiblioteca(idBiblioteca);
    }
    
    /**
     * Obtiene los juegos prestados de un usuario
     * @param idUsuario
     * @return
     * @throws EntityNotFoundException
     * @throws SQLException 
     */
    public List<BibliotecaResponse> obtenerJuegosPrestados(int idUsuario) 
            throws EntityNotFoundException, SQLException {
        
        
        Optional<Usuario> usuarioOpt = usersDB.getById(idUsuario);
        if (!usuarioOpt.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        List<BibliotecaResponse> bibliotecaCompleta = bibliotecaDB.getBibliotecaWithDetails(idUsuario);
        
        return bibliotecaCompleta.stream()
                .filter(item -> "PRESTAMO".equals(item.getTipo_adquisicion()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Obtiene los juegos comprados de un usuario
     * @param idUsuario
     * @return
     * @throws EntityNotFoundException
     * @throws SQLException 
     */
    public List<BibliotecaResponse> obtenerJuegosComprados(int idUsuario) 
            throws EntityNotFoundException, SQLException {
        
        Optional<Usuario> usuarioOpt = usersDB.getById(idUsuario);
        if (!usuarioOpt.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        List<BibliotecaResponse> bibliotecaCompleta = bibliotecaDB.getBibliotecaWithDetails(idUsuario);
        
        return bibliotecaCompleta.stream()
                .filter(item -> "COMPRA".equals(item.getTipo_adquisicion()))
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * Busca juegos en la biblioteca por título
     */
    public List<BibliotecaResponse> buscarEnBibliotecaPorTitulo(int idUsuario, String titulo) 
            throws EntityNotFoundException, SQLException {
        
        // Validar que el usuario existe
        Optional<Usuario> usuarioOpt = usersDB.getById(idUsuario);
        if (!usuarioOpt.isPresent()) {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + idUsuario);
        }
        
        List<BibliotecaResponse> bibliotecaCompleta = bibliotecaDB.getBibliotecaWithDetails(idUsuario);
        
        // Filtrar por título (búsqueda insensible a mayúsculas)
        String tituloLower = titulo.toLowerCase();
        return bibliotecaCompleta.stream()
                .filter(item -> item.getVideojuego().getTitulo().toLowerCase().contains(tituloLower))
                .collect(java.util.stream.Collectors.toList());
    }
    

}
