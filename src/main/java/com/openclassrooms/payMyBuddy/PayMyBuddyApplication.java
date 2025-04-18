package com.openclassrooms.payMyBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(scanBasePackages = {"com.openclassrooms.payMyBuddy"})
public class PayMyBuddyApplication {

	public static void main(String[] args) {

		SpringApplication.run(PayMyBuddyApplication.class, args);
	}

}
