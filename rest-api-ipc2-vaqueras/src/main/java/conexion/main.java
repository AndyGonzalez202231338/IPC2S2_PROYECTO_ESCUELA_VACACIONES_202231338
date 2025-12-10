/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package conexion;

import java.sql.Connection;

/**
 *
 * @author andy
 */
public class main {
    
    
    public static void main(String[] args) {
        // Obtener la instancia del singleton
        DBConnectionSingleton db = DBConnectionSingleton.getInstance();

        // Intentar obtener conexión
        Connection conn = db.getConnection();

        if (conn != null) {
            System.out.println("Conexión exitosa a MySQL");
        } else {
            System.out.println("No se pudo conectar a MySQL");
        }
        
    }
}
