package com.atd.autorizacao.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

@WebListener
public class LiquibaseInitializer implements ServletContextListener {

    private static final String CHANGELOG = "db/changelog/db.changelog-master.xml";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try (Connection connection = DriverManager.getConnection(
                DatabaseConfig.url(), DatabaseConfig.user(), DatabaseConfig.password())) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase(CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao executar as migracoes do Liquibase", e);
        }
    }
}
