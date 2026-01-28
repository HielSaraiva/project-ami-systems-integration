package org.eletra.energy.business.configs;

import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;


public class TestcontainersConfig {

    @Container
    public static final ArtemisContainer artemisContainer = new ArtemisContainer("apache/activemq-artemis:latest")
            .withUser("test")
            .withPassword("test");

    @Container
    public static final PostgreSQLContainer postgresContainer = new PostgreSQLContainer("postgres:latest")
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("test");
}
