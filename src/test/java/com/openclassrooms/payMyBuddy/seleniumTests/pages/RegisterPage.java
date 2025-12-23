package com.openclassrooms.payMyBuddy.seleniumTests.pages;


import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Getter
@Setter
public class RegisterPage {


    private WebDriver driver;

    private By usernameField = By.name("username");
    private By emailField = By.name("email");
    private By passwordField = By.name("password");
    private By registerButton = By.cssSelector("button[type='submit']");
    private By successAlert = By.cssSelector(".alert-info");


    public RegisterPage(WebDriver driver) {
        this.driver = driver;
    }


    // Actions
    public RegisterPage entrerUsername(String username) {
        driver.findElement(usernameField).sendKeys(username);
        return this;
    }

    public RegisterPage entrerMail(String email) {
        driver.findElement(emailField).sendKeys(email);
        return this;
    }


    public RegisterPage entrerPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
        return this;
    }

    public void clickLoginButton() {
        driver.findElement(registerButton).click();
    }




    // Vérif du message success
    public String getSuccessText() {
        return driver.findElement(successAlert).getText();
    }


}
