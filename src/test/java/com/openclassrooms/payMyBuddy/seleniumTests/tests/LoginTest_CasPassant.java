package com.openclassrooms.payMyBuddy.seleniumTests.tests;


import com.openclassrooms.payMyBuddy.seleniumTests.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginTest_CasPassant {


    @Test
    void testLogin() {

        WebDriver driver = new FirefoxDriver();
        driver.get("http://localhost:8080/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage
                .entrerMail("michel7@paymybuddy.com")
                .entrerPassword("1234")
                .clickLoginButton();


        // Wait à intégrer le temps de la connexion
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement transfertLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/transfert']"))
        );

        // Vérifier un élément pst sur la page une fois la connexion réussie
        Assertions.assertTrue(transfertLink.isDisplayed());
        Assertions.assertTrue(driver.getTitle().contains("Pay My Buddy - Home"));

        driver.quit();


    }












}
