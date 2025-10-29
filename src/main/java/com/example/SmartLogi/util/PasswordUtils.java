package com.example.SmartLogi.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtils {

    private static final PasswordEncoder encoder = new BCryptPasswordEncoder(10); // cost 10

    public static String hash(String raw) {
        return encoder.encode(raw); // returns salt+hash in one string
    }

    public static boolean verify(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }
}
