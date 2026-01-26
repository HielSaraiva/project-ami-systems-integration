package org.eletra.energy.business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BusinessApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testMainMethod() {
        // Given
        // No specific arrangement needed for this test

        // When & Then
        Assertions.assertDoesNotThrow(() -> {
            BusinessApplication.main(new String[]{});
        });
    }
}
