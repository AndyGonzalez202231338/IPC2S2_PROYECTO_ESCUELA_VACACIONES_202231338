/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user.services;

import db.UsersDB;

import exceptions.EntityAlreadyExistsException;
import exceptions.UserDataInvalidException;
import user.dtos.NewUserRequest;
import user.models.Usuario;


/**
 *
 * @author andy
 */
public class UsersCreator {
    public Usuario createUser(NewUserRequest newUserRequest) throws UserDataInvalidException,
            EntityAlreadyExistsException {
        
        UsersDB usersDB = new UsersDB();
        
        // Verificar si el usuario ya existe
        if (usersDB.existsUserByEmail(newUserRequest.getCorreo())) {
            throw new EntityAlreadyExistsException(
                    String.format("El usuario con correo %s ya existe", newUserRequest.getCorreo()));
        }
        
        // Extraer usuario
        Usuario user = extractUser(newUserRequest);
        
        
        // Crear el usuario
        return usersDB.createUser(user);
    }
    
    private Usuario extractUser(NewUserRequest newUserRequest) throws UserDataInvalidException {
        try {
            // Convertir id_empresa (puede ser null)
            int idEmpresa = (newUserRequest.getId_empresa() != null) ? 
                           newUserRequest.getId_empresa() : 0;
            
            Usuario user = new Usuario(
                    0, // id_usuario será generado
                    newUserRequest.getCorreo(),
                    newUserRequest.getId_rol(),
                    idEmpresa,
                    newUserRequest.getNombre(),
                    newUserRequest.getPassword(),
                    newUserRequest.getFechaNacimientoAsDate(), // Usar el Date convertido (string a date)
                    newUserRequest.getPais(),
                    newUserRequest.getTelefono(),
                    newUserRequest.getSaldo_cartera(),
                    newUserRequest.getAvatar()
            );
            
            if (!user.isValid()) {
                throw new UserDataInvalidException("Error en los datos enviados");
            }
            
            return user;
        } catch (IllegalArgumentException | NullPointerException e) {
            System.out.println("ERROR en extractUser: " + e.getMessage());
            e.printStackTrace();
            throw new UserDataInvalidException("Error en los datos enviados: " + e.getMessage());
        }
    }
}