package com.example.demo;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.services.SQL;

@SpringBootTest
class DemoApplicationTests {

    @Mock
    private SQL sql;

    @Test
    void contextLoads() {
        // Juste vérifier que le contexte démarre 
    }
}
