package com.openclassrooms.payMyBuddy.seleniumTests.pages;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@Getter
@Setter
public class LoginPage {


    private final WebDriver driver;

    // sélecteurs
    private By emailField = By.name("email");
    private By passwordField = By.name("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By successAlert = By.cssSelector(".alert-info");


    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }


    // actions
    public LoginPage entrerMail(String email) {
        driver.findElement(emailField).sendKeys(email);
        return this;
    }


    public LoginPage entrerPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
        return this;
    }

    public HomePage clickLoginButton() {
        driver.findElement(loginButton).click();
        return new HomePage(driver);
    }
















}
