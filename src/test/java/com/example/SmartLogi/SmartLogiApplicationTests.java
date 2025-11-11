package com.example.SmartLogi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SmartLogiApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("contextLoads");
	}

}
