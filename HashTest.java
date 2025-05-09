import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class HashTest {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    public static void main(String[] args) {
        String username = "science1";
        String password = "pass123";
        
        // Passwort-Hashing mit BCrypt
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(12));
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Generated hash: " + hashedPassword);

        // Vergleich mit gespeichertem Hash (Beispiel)
        String storedHash = "$2a$12$XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"; // Beispiel-Hash
        boolean matches = BCrypt.checkpw(password, storedHash);
        System.out.println("\nStored hash matches generated hash: " + matches);
    }
} 