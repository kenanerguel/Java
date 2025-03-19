package com.mein.projekt.auth;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;
import java.util.GregorianCalendar;
import com.mein.projekt.dao.ArtikelDAO;
import com.mein.projekt.model.Artikel;

@Named
@SessionScoped
public class Shop implements Serializable {

    @Inject
    private ArtikelDAO artikelDAO;

    // Beispielhafte Benutzer für die Authentifizierung im Backend
    private final String[][] users = new String[][]{
            // Hier sind z. B. zwei Test-Accounts definiert:
            new String[]{"science1",
                    "oLBbZ3YF...HashForScience1...", // Ersetze diesen Platzhalter durch den korrekten Hash
                    "admin"},
            new String[]{"science2",
                    "9JXkL2Pf...HashForScience2...", // Ersetze diesen Platzhalter durch den korrekten Hash
                    "client"}
    };

    // Basis-Datensatz: Beispiel-Daten zu CO₂-Emissionen pro Land (für den öffentlichen Bereich)
    public static final List<Artikel> baseSortiment = Arrays.asList(new Artikel[]{
            new Artikel("Germany",
                    "Aktueller CO₂-Ausstoß: 8,5 t pro Kopf (Stand 2023).",
                    "germany.png", (new GregorianCalendar(2023, 0, 1).getTime())),
            new Artikel("USA",
                    "Aktueller CO₂-Ausstoß: 15,2 t pro Kopf (Stand 2023).",
                    "usa.png", (new GregorianCalendar(2023, 0, 1).getTime())),
            new Artikel("France",
                    "Aktueller CO₂-Ausstoß: 5,4 t pro Kopf (Stand 2023).",
                    "france.png", (new GregorianCalendar(2023, 0, 1).getTime()))
            // Weitere Länder können hier hinzugefügt werden
    });

    public Shop() {
    }

    // Passwort-Hashing-Methode mit SHA-512 (unverändert)
    public static String hashPassword(String name, String pass, String salt) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digester.digest((name + pass + salt).getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(hashBytes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*

    // Validierung von Benutzername und Passwort
    public void validateUsernameAndPassword(CurrentUser currentUser, String name, String pass, String salt) {
        String passHash = hashPassword(name, pass, salt);
        currentUser.reset();
        for (String[] user : users) {
            if (user[0].equals(name)) {
                if (user[1].equals(passHash)) {
                    if (user[2].equals("admin")) {
                        currentUser.admin = true;
                        return;
                    } else if (user[2].equals("client")) {
                        currentUser.client = true;
                        return;
                    } else {
                        throw new RuntimeException("Benutzer " + name + " ist falsch angelegt.");
                    }
                }
            }
        }
    }
    */
}

