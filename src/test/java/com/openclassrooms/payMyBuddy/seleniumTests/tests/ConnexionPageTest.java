package com.openclassrooms.payMyBuddy.seleniumTests.tests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class ConnexionPageTest {

        @Test
        void testConnexionPage() {

                System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver.exe");

                WebDriver driver = new FirefoxDriver();

                driver.get("http://localhost:8080/home");

                driver.findElement(By.xpath("//a[@href='/login']")).click();

                driver.findElement(By.name("email")).sendKeys("michel1@paymybuddy.com");
                driver.findElement(By.name("password")).sendKeys("1234");

                driver.findElement(By.cssSelector("button[type='submit']")).click();

                // Wait à intégrer le temps de la connexion
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
                WebElement transfertLink = wait.until(
                        ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/transfert']"))
                );

                // Vérifier un élément pst sur la page une fois la connexion réussie
                Assertions.assertTrue(transfertLink.isDisplayed());

                driver.quit();


        }



}
