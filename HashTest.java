import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class HashTest {
    private static final String salt = "vXsia8c04PhBtnG3isvjlemj7Bm6rAhBR8JRkf2z";

    public static void main(String[] args) {
        String username = "science1";
        String password = "pass123";
        
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((username + password + salt).getBytes(StandardCharsets.UTF_8));
            String hashedPassword = new String(Base64.getEncoder().encode(hashBytes));
            
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
            System.out.println("Generated hash: " + hashedPassword);
            
            // Compare with stored hash
            String storedHash = "KW/Q+quBB936f+vEzM79JDs+4TYDraef7VS8i/vAS8fj6Zr+fvwOIk28l1G7IP0p1JmEeNvJj+BBdFia6EXKUw==";
            System.out.println("\nStored hash matches generated hash: " + storedHash.equals(hashedPassword));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 