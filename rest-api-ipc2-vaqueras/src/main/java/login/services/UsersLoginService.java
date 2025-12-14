/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login.services;

import db.UsersDB;
import exceptions.EntityNotFoundException;
import exceptions.UserDataInvalidException;
import java.util.Optional;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class UsersLoginService {
    
    public Usuario login(String email, String password) throws EntityNotFoundException, UserDataInvalidException {
        UsersDB usersDB = new UsersDB();
        
        // Buscar usuario por email
        Optional<Usuario> userOpt = usersDB.getByEmail(email);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("Este usuario no esta registrado");
        }
        
        Usuario user = userOpt.get();
        
        if (!user.getPassword().equals(password)) {
            throw new UserDataInvalidException("Credenciales incorrectas");
        }   
        return user;
    }
}