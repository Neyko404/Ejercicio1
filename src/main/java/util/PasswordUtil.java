/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashea la contraseña
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verifica si la contraseña ingresada coincide con el hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
    
    public static void main(String[] args) {
        String original = "la fe de cuto123";
        String hashed = PasswordUtil.hashPassword(original);

        System.out.println("Contraseña hasheada: " + hashed);

        boolean esCorrecta = PasswordUtil.verifyPassword("la fe de cuto123", hashed);
        System.out.println("¿Es correcta? " + esCorrecta);
    }
}