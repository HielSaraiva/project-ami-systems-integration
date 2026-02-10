package org.eletra.energy.networkgrpc.services;

import org.eletra.energy.networkgrpc.configs.TestcontainersConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class TransactionGrpcServiceTest {

}
