package org.eletra.energy.business;

import org.eletra.energy.business.configs.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
class BusinessApplicationTest {

    @Test
    void contextLoads() {

    }
}
