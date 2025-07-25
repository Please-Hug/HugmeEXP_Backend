package org.example.hugmeexp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import org.example.hugmeexp.config.MockTestConfiguration;

@SpringBootTest
@Import(MockTestConfiguration.class)
@ActiveProfiles("test")

class HugmeExpApplicationTests {

	@Test
	void contextLoads() {
	}

}
