package org.eletra.energy.business.configs;

import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;


public class TestcontainersConfig {

    public static final ArtemisContainer artemisContainer = new ArtemisContainer("apache/activemq-artemis:latest")
            .withUser("test")
            .withPassword("test");

    public static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest")
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test");
}
