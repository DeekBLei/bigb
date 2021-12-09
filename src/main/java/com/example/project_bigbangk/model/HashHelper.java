package com.example.project_bigbangk.model;

import org.springframework.security.crypto.bcrypt.BCrypt;

/**
 * created by Kelly Speelman - de Jonge
 */

public class HashHelper {

    public static String hash(String password, String pepper) {
        return BCrypt.hashpw(password, saltCreate(pepper));
    }

    public static String saltCreate(String pepper){
        return BCrypt.gensalt()+pepper;
    }

    public static Boolean hashCheck(String password, String hashedPassword){
        return BCrypt.checkpw(password, hashedPassword);
    }
}
