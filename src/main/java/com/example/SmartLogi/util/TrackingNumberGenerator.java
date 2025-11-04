package com.example.SmartLogi.util;

import java.security.SecureRandom;

public class TrackingNumberGenerator
{

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom random = new SecureRandom();

    public static String generateShortCode() {
        // 3 lettres + "_" + 2 chiffres
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }
        sb.append("_");
        sb.append(String.format("%02d", random.nextInt(100))); // deux chiffres
        return sb.toString();
    }

}
