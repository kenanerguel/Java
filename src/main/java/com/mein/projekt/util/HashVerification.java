package com.mein.projekt.util;

public class HashVerification {
    public static void main(String[] args) {
        // Test cases
        String[][] testCases = {
            {"admin", "admin123"},
            {"science1", "science123"}
        };

        System.out.println("=== Hash Verification ===\n");
        
        for (String[] testCase : testCases) {
            String username = testCase[0];
            String password = testCase[1];
            
            String hash1 = PasswordGenerator.generateHash(username, password);
            String hash2 = FixPasswordHashes.hashPassword(username, password);
            
            System.out.println("Testing for user: " + username);
            System.out.println("PasswordGenerator hash: " + hash1);
            System.out.println("FixPasswordHashes hash: " + hash2);
            System.out.println("Hashes match: " + hash1.equals(hash2));
            System.out.println();
        }
    }
} 