package com.openclassrooms.payMyBuddy.seleniumTests.tests;


import com.openclassrooms.payMyBuddy.seleniumTests.pages.AddRelationPage;
import com.openclassrooms.payMyBuddy.seleniumTests.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AddRelation_CasPassant {


    @Test
    void testAdRelation() {

        WebDriver driver = new FirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.get("http://localhost:8080/login");

        // Se connecter
        LoginPage loginPage = new LoginPage(driver);
        loginPage
                .entrerMail("michel5@paymybuddy.com")
                .entrerPassword("1234")
                .clickLoginButton();

        // Wait
        wait.until(ExpectedConditions.urlContains("/home"));

        driver.findElement(By.xpath("//a[@href='/relation']")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));


        // Ajouter une relation
        AddRelationPage addRelationPage = new AddRelationPage(driver);
        addRelationPage
                .entrerEmail("michel6@paymybuddy.com")
                .clickAddRelationButton();



        Assertions.assertTrue(driver.getTitle().contains("Pay My Buddy - Relations"));
        Assertions.assertTrue(addRelationPage.getSuccessText().contains("La relation a bien été ajoutée !"));





    }






}
