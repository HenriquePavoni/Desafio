package com.atd.autorizacao.config;

public final class DatabaseConfig {

    private DatabaseConfig() {
    }

    public static String url() {
        return required("DB_URL");
    }

    public static String user() {
        return required("DB_USER");
    }

    public static String password() {
        return required("DB_PASSWORD");
    }

    public static String driver() {
        return required("DB_DRIVER");
    }

    private static String required(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Variavel de ambiente obrigatoria nao definida: " + key);
        }
        return value;
    }
}
