package com.mein.projekt.dao;

import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@Named
@ApplicationScoped
public class UserDAO {

    private final EntityManager entityManager;

    @Inject
    public UserDAO(EntityManagerProvider entityManagerProvider) {
        this.entityManager = entityManagerProvider.getEntityManager();
    }

    /**
     * Speichert einen neuen Benutzer in der Datenbank.
     */
    public void saveUser(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Überprüft, ob ein Benutzer Client (nicht Admin) ist.
     * Beispielhafte Abfrage: SELECT u FROM User u WHERE ...
     * Hier wird angenommen, dass User ein Feld "admin" hat,
     * das angibt, ob er Admin ist oder nicht.
     * Überprüft, ob ein Benutzer Admin oder Client ist.
     */
    public User isAdminOrClient(String username, String password) {
        try {
            User user = entityManager.createQuery(
                            "SELECT u FROM User u WHERE u.username = :uname AND u.password = :pwd",
                            User.class)
                    .setParameter("uname", username)
                    .setParameter("pwd", password)
                    .getSingleResult();

            // Falls user != null, gib zurück, ob er Admin ist.
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Falls kein Nutzer gefunden oder Fehler
        }
    }

    /**
     * Main-Methode für Tests
     */
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);

        // Beispiel-User speichern
        /*
        User adminUser = new User("root", "root", true);
        User clientUser1 = new User("Wissenschaftler_1", "Hallo", false);
        User clientUser2 = new User("Wissenschaftler_2", "Servus", false);


        userDAO.saveUser(adminUser);
        userDAO.saveUser(clientUser1);
        userDAO.saveUser(clientUser2);
        */

        // Test-Logik: Admin/Client prüfen
        System.out.println("root isAdmin? " + userDAO.isAdminOrClient("root", "dXRcRLCMz+kUxl9QORmRxyPfliMK/6hF1zild9sVmuu4BHCemIAfqAH8GXjbomZAFmjdAJ0F6nESJhDjCraIRQ==")); // sollte true sein
        System.out.println("Wissenschaftler_1 isClient? " + userDAO.isAdminOrClient("Wissenschaftler_1", "+sasj0x49+vlfzERhRBCRHazOgEOo2WWljaclLTWv9M7xdYJvX0g0hAxFWhErpEebMCQox83NijXNi4AoC3Jhw==")); // sollte true sein
    }
}

