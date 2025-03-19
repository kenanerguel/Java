package com.mein.projekt.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

import com.mein.projekt.model.User;
import com.mein.projekt.util.EntityManagerProvider;

@Named
@ApplicationScoped
public class UserDAO {

    private final EntityManager entityManager;

    @Inject
    public UserDAO(EntityManagerProvider entityManagerProvider) {
        this.entityManager = entityManagerProvider.getEntityManager();
    }

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

    public Boolean isAdmin(String username, String password) {
        try {
            return entityManager.createQuery(
                            "SELECT u.isAdmin FROM User u WHERE u.username = :username AND u.password = :password", Boolean.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Falls kein Nutzer gefunden wurde oder Fehler auftritt
        }
    }


    public String findUserByUsername(String username, String password) {
        try {
            return entityManager.createQuery(
                            "SELECT u.username FROM User u WHERE u.username = :username AND u.password = :password", String.class)
                    .setParameter("username", username)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Falls kein Nutzer gefunden wurde oder Fehler auftritt
        }
    }



    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        UserDAO userDAO = new UserDAO(entityManagerProvider);

        // Beispiel-User speichern
        User adminUser = new User("root", "root", true);
        User clientUser1 = new User("Wissenschaftler_1", "Hallo", false);
        User clientUser2 = new User("Wissenschaftler_2", "Servus", false);
        userDAO.saveUser(adminUser);
        userDAO.saveUser(clientUser1);
        userDAO.saveUser(clientUser2);


    }
}
