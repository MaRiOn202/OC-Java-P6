package com.openclassrooms.payMyBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//(scanBasePackages = {"controller","service","model", "repository", "config"})
@SpringBootApplication(scanBasePackages = {"com.openclassrooms.payMyBuddy"})
public class PayMyBuddyApplication {

	public static void main(String[] args) {

		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

}
