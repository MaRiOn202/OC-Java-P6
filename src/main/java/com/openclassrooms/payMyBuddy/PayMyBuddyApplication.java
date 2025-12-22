package com.openclassrooms.payMyBuddy;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication(scanBasePackages = {"com.openclassrooms.payMyBuddy"})
public class PayMyBuddyApplication {

	public static void main(String[] args) {

		SpringApplication.run(PayMyBuddyApplication.class, args);


	}

}
