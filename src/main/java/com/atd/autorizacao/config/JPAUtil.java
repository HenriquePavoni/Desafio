package com.atd.autorizacao.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public final class JPAUtil {

    private static final EntityManagerFactory ENTITY_MANAGER_FACTORY =
            Persistence.createEntityManagerFactory("autorizacaoPU", connectionProperties());

    private JPAUtil() {
    }

    public static EntityManager getEntityManager() {
        return ENTITY_MANAGER_FACTORY.createEntityManager();
    }

    public static void close() {
        if (ENTITY_MANAGER_FACTORY.isOpen()) {
            ENTITY_MANAGER_FACTORY.close();
        }
    }

    private static Map<String, String> connectionProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("jakarta.persistence.jdbc.driver", DatabaseConfig.driver());
        properties.put("jakarta.persistence.jdbc.url", DatabaseConfig.url());
        properties.put("jakarta.persistence.jdbc.user", DatabaseConfig.user());
        properties.put("jakarta.persistence.jdbc.password", DatabaseConfig.password());
        return properties;
    }
}
