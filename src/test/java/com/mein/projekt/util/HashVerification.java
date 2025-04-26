package com.mein.projekt.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HashVerification {
    
    @Test
    public void testHashesMatch() {
        String username = "testUser";
        String password = "testPassword123";
        
        // Generate hash using PasswordGenerator
        String hash1 = PasswordGenerator.generateHash(username, password);
        
        // Generate hash using FixPasswordHashes
        String hash2 = FixPasswordHashes.hashPassword(username, password);
        
        // Verify both hashes are identical
        assertEquals(hash1, hash2, "Password hashes should match between PasswordGenerator and FixPasswordHashes");
    }
} 