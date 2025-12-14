/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.services;

import db.UsersDB;
import exceptions.EntityNotFoundException;
import exceptions.UserDataInvalidException;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import user.dtos.UpdateUserRequest;
import user.models.Usuario;


/**
 *
 * @author andy
 */
public class UsersCrudService {
    public List<Usuario> getAllUsers() {
        UsersDB usersDB = new UsersDB();

        //return eventsDb.getAllEvents();
        return usersDB.getAllUsers();
    }
    
    public List<Usuario> getAllUsersAnunciante() {
        UsersDB usersDB = new UsersDB();
        
        return usersDB.getAllUsersAnunciante();
    }
    
        /**
     * Obtiene todos los administradores de empresa sin empresa asignada
     */
    public List<Usuario> getAdministradoresSinEmpresa() {
        UsersDB usersDB = new UsersDB();
        return usersDB.getAdministradoresSinEmpresa();
    }
    
    public Usuario getUserByEmail(String correo) throws EntityNotFoundException {
        UsersDB usersDB = new UsersDB();
        Optional<Usuario> userOpt = usersDB.getByEmail(correo);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("El usuario con correo %s no existe", correo)
            );
        }
          return userOpt.get();
    }
    
    public Usuario updateUser(String correo, UpdateUserRequest updateUserRequest) throws UserDataInvalidException,
                EntityNotFoundException {
        UsersDB usersDB = new UsersDB();
        
        Usuario user = getUserByEmail(correo);
        
        user.setIdUsuario(updateUserRequest.getIdUsuario());
        user.setRol(updateUserRequest.getRol());
        user.setEmpresa(updateUserRequest.getEmpresa());
        user.setCorreo(correo);
        user.setNombre(updateUserRequest.getNombre());
        user.setPassword(updateUserRequest.getPassword());
        user.setFecha_nacimiento(updateUserRequest.getFecha_nacimiento());
        user.setPais(updateUserRequest.getNombre());
        user.setTelefono(updateUserRequest.getTelefono());
        
        if (!user.isValid()) {
            throw new UserDataInvalidException("Error en los datos enviados");
        }
        
        usersDB.updateUserByEmail(correo, user);
        
        return user;
    }
    
    public void deleteUserByEmail(String email) throws EntityNotFoundException {
        UsersDB usersDB = new UsersDB();
        Optional<Usuario> userOpt = usersDB.getByEmail(email);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("El Usuario con email %s no existe", email)
            );
        }
        // Obtener el ID del usuario encontrado
        Usuario user = userOpt.get();
        int userId = user.getIdUsuario();
        usersDB.deleteUser(userId);
    }
    
    public Usuario getUserById(int idUsuario) throws EntityNotFoundException {
    Optional<Usuario> userOpt = new UsersDB().getById(idUsuario);
    if (userOpt.isEmpty()) {
        throw new EntityNotFoundException("Usuario no encontrado con id " + idUsuario);
    }
    return userOpt.get();
    }

    public boolean asignarEmpresaAUsuario(int idUsuario, int idEmpresa) {
        UsersDB usersDB = new UsersDB();
        return usersDB.asignarEmpresaAUsuario(idUsuario, idEmpresa);
    }
}
