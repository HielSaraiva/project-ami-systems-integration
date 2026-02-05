package org.eletra.energy.converter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConverterApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void testMainMethod() {
        // Given
        // No specific arrangement needed for this test

        // When & Then
        Assertions.assertDoesNotThrow(() -> {
            ConverterApplication.main(new String[] {});
        });
    }
}
