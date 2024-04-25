package com.dhn.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DhnClient17Application {

	public static void main(String[] args) {
		SpringApplication.run(DhnClient17Application.class, args);
	}

}
