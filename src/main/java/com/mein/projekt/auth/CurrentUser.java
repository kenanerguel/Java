package com.mein.projekt.auth;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class CurrentUser implements Serializable {

    boolean admin, client;

    public void reset() {
        admin = false; client = false;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isClient() {
        return client;
    }

    public boolean isValid() {
        return isClient() || isAdmin();
    }
}
