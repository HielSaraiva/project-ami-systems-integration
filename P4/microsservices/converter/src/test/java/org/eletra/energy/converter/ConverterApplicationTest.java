package org.eletra.energy.converter;

import org.eletra.energy.converter.configs.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
public class ConverterApplicationTest {

    @Test
    public void contextLoads() {
    }
}
