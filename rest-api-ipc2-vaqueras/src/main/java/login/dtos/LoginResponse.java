/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login.dtos;


import user.dtos.UserResponse;
import user.models.Usuario;

/**
 *
 * @author andy
 */
public class LoginResponse {
    private boolean success;
    private UserResponse user;  // Cambiar de User a UserResponse para seguridad
    private String message;

    // Constructores
    public LoginResponse() {}

    public LoginResponse(boolean success, UserResponse user, String message) {
        this.success = success;
        this.user = user;
        this.message = message;
    }

    // Métodos estáticos para crear respuestas fácilmente
    public static LoginResponse success(Usuario user) {
        UserResponse userResponse = new UserResponse(user);
        return new LoginResponse(true, userResponse, "Login exitoso");
    }

    public static LoginResponse error(String message) {
        return new LoginResponse(false, null, message);
    }

    // Getters y Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}