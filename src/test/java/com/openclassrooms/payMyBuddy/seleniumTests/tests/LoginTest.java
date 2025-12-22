package com.openclassrooms.payMyBuddy.seleniumTests.tests;


import com.openclassrooms.payMyBuddy.seleniumTests.pages.LoginPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginTest {


    @Test
    void testLogin() {

        WebDriver driver = new FirefoxDriver();
        driver.get("http://localhost:8080/login");

        LoginPage loginPage = new LoginPage(driver);
        loginPage.entrerMail("michel7@paymybuddy.com");
        loginPage.entrerPassword("1234");
        loginPage.clickLoginButton();

        Assertions.assertTrue(driver.getTitle().contains("Pay My Buddy"));

        driver.quit();


    }












}
