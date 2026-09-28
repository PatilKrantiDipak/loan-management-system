package com.example.loanmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
public class LoanmanagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoanmanagementApplication.class, args);
		System.out.println("Welcome");
	}

}
