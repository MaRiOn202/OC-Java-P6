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

public class AddFriendCasNonPassantTest {


    @Test
    void testAddFriendCasNonPassant() throws InterruptedException {

        System.setProperty("webdriver.gecko.driver", "C:\\Program Files\\geckodriver.exe");

        WebDriver driver = new FirefoxDriver();
        // Wait à intégrer le temps de la connexion
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));


        driver.get("http://localhost:8080/home");

        // login
        driver.findElement(By.xpath("//a[@href='/login']")).click();

        // connexion
        driver.findElement(By.name("email")).sendKeys("michel7@paymybuddy.com");
        driver.findElement(By.name("password")).sendKeys("1234");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // attendre que le lien /transfert s'affiche à l'écran
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@href='/transfert']")));

        // ajouter un ami
        driver.findElement(By.cssSelector("a[href='/relation']")).click();                    // cssSelector + fiable
        driver.findElement(By.id("email")).sendKeys("michel5@paymybuddy.com");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        WebElement errorAlert = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".alert.alert-danger")));

        // Vérif
        String errorText = errorAlert.getText();

        Thread.sleep(500);
        Assertions.assertTrue(
                errorText.contains("relation"),
                "La relation existe déjà");

        driver.quit();


    }

}
