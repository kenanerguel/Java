package com.mein.projekt.auth;

import com.mein.projekt.dao.UserDAO;
import com.mein.projekt.util.EntityManagerProvider;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.w3c.dom.UserDataHandler;

import java.io.Serializable;

@Named
@SessionScoped
public class CurrentUser implements Serializable {

    private boolean isAdmin;
    private String username = "";
    private UserDAO userDAO = null;

    public CurrentUser() {
        EntityManagerProvider entityManagerProvider = new EntityManagerProvider();
        this.userDAO = new UserDAO(entityManagerProvider);
    }

    public void handleUser(String username, String password) {
        this.isAdmin = userDAO.isAdmin(username, password);
        this.username = userDAO.findUserByUsername(username, password);
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public String getUsername() {
        return username;
    }
}
