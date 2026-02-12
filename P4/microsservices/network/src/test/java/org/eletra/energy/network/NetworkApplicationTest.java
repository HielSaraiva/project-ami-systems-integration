package org.eletra.energy.network;

import org.eletra.energy.network.configs.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
class NetworkApplicationTest {

    @Test
    void contextLoads() {
    }
}
