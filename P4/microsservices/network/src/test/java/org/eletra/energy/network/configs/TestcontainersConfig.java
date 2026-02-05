package org.eletra.energy.network.configs;

import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration
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

    @Container
    public static final GenericContainer<?> ftpContainer = new FixedHostPortGenericContainer<>("stilliard/pure-ftpd:latest")
            .withExposedPorts(21)
            .withFixedExposedPort(30000, 30000)
            .withFixedExposedPort(30001, 30001)
            .withFixedExposedPort(30002, 30002)
            .withFixedExposedPort(30003, 30003)
            .withFixedExposedPort(30004, 30004)
            .withFixedExposedPort(30005, 30005)
            .withFixedExposedPort(30006, 30006)
            .withFixedExposedPort(30007, 30007)
            .withFixedExposedPort(30008, 30008)
            .withFixedExposedPort(30009, 30009)
            .withEnv("FTP_USER_NAME", "test")
            .withEnv("FTP_USER_PASS", "test")
            .withEnv("FTP_USER_HOME", "/home/ftp_server/data")
            .withEnv("PUBLICHOST", "localhost")
            .withEnv("ADDED_FLAGS", "-p 30000:30009")
            .waitingFor(Wait.forListeningPort().forPorts(21));
}
