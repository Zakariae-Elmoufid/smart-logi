package com.example.SmartLogi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = "com.example.SmartLogi.entities")
@EnableScheduling
public class SmartLogiApplication {

	public static void main(String[] args){
		SpringApplication.run(SmartLogiApplication.class, args);
	}

}
