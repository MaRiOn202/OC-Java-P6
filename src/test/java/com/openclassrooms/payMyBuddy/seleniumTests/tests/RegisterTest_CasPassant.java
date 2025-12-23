package com.openclassrooms.payMyBuddy.seleniumTests.tests;


import com.openclassrooms.payMyBuddy.seleniumTests.pages.RegisterPage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class RegisterTest_CasPassant {


   @Test
   void testRegister() {

       WebDriver driver = new FirefoxDriver();
       driver.get("http://localhost:8080/register");

       RegisterPage registerPage = new RegisterPage(driver);
       registerPage
                .entrerUsername("Michel9")
                .entrerMail("michel9@paymybuddy.com")
                .entrerPassword("1234")
                .clickLoginButton();

       Assertions.assertTrue(driver.getTitle().contains("Pay My Buddy"));
       Assertions.assertTrue(registerPage.getSuccessText().contains("Vous vous êtes bien enregistré - Veuillez vous logger à présent"));

       driver.quit();

   }



}
