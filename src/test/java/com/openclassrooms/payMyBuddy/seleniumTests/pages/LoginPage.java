package com.openclassrooms.payMyBuddy.seleniumTests.pages;

import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@Getter
@Setter
public class LoginPage {


    private WebDriver driver;

    // sélecteurs
    private By emailField = By.name("email");
    private By passwordField = By.name("password");
    private By loginButton = By.cssSelector("button[type='submit']");


    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }


    // actions
    public void entrerMail(String email) {
        driver.findElement(emailField).sendKeys(email);
    }


    public void entrerPassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickLoginButton() {
        driver.findElement(loginButton).click();
    }













}
