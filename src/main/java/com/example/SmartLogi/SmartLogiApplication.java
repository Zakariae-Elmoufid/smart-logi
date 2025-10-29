package com.example.SmartLogi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.SmartLogi.entities")
public class SmartLogiApplication {

	public static void main(String[] args){
		SpringApplication.run(SmartLogiApplication.class, args);
	}

}
