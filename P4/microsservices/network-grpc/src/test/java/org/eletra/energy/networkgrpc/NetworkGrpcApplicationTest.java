package org.eletra.energy.networkgrpc;

import org.eletra.energy.networkgrpc.configs.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class NetworkGrpcApplicationTest {

    @Test
    void contextLoads() {
    }

}
