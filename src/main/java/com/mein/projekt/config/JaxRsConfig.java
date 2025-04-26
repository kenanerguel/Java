package com.mein.projekt.config;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/rest")
public class JaxRsConfig extends Application {
    // Leere Klasse - JAX-RS wird automatisch alle @Path annotierten Klassen finden
} 