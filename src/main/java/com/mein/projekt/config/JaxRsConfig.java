package com.mein.projekt.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import com.mein.projekt.controller.ArtikelController;
import com.mein.projekt.auth.LoginDiagnostic;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/rest")
public class JaxRsConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(ArtikelController.class);
        classes.add(LoginDiagnostic.class);
        return classes;
    }
} 