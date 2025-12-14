/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.services;

import db.UsersDB;
import exceptions.EntityNotFoundException;
import exceptions.OperationNotAllowedException;
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
    
    public boolean deleteUserByEmail(String email) throws EntityNotFoundException, OperationNotAllowedException {
    UsersDB usersDB = new UsersDB();
    Optional<Usuario> userOpt = usersDB.getByEmail(email);
    
    if (userOpt.isEmpty()) {
        throw new EntityNotFoundException(
                String.format("El Usuario con email %s no existe", email)
        );
    }
    
    Usuario user = userOpt.get();
    
    // Validar si es administrador de empresa (rol 2)
    if (user.getId_rol() == 2 && user.getId_empresa() > 0) {
        int idEmpresa = user.getId_empresa();
        
        // Contar cuántos administradores tiene la empresa
        int cantidadAdministradores = usersDB.countAdministradoresEnEmpresa(idEmpresa);
        
        // Si es el único administrador, no se puede eliminar
        if (cantidadAdministradores <= 1) {
            throw new OperationNotAllowedException(
                    "No se puede eliminar el ÚNICO administrador de la empresa. " +
                    "La empresa ID " + idEmpresa + " se quedaría sin administrador. " +
                    "Primero asigne otro administrador a la empresa."
            );
        }
        
        // Si hay más administradores, SÍ se puede eliminar este
        // Pero necesitas desasignar la empresa primero o eliminar directamente
        // El DELETE en la BD automáticamente quitará la relación
    }
    
    // Validar que no sea el último administrador del sistema (rol 1)
    /*if (user.getId_rol() == 1) {
        int countAdmins = usersDB.countUsersByRole(1);
        if (countAdmins <= 1) {
            throw new OperationNotAllowedException(
                    "No se puede eliminar el único administrador del sistema."
            );
        }
    }*/
    
    return usersDB.deleteUser(user.getIdUsuario());
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
