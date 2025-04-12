package com.yatmk;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class ApplicationTests {

	@Test
	void contextLoads() {
		// hi
	}

	@Test
	void testAppContextLoads() {

		log.info("Spring Boot Test is running! hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");

	}

}
