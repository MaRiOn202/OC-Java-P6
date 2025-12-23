package com.openclassrooms.payMyBuddy.seleniumTests.pages;


import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Getter
@Setter
public class AddRelationPage {


    private WebDriver driver;

    private By emailField = By.name("email");
    private By addRelationButton = By.cssSelector("button[type='submit']");
    private By successAlert = By.cssSelector(".alert-info");


    public AddRelationPage(WebDriver driver) {
        this.driver = driver;
    }


    public AddRelationPage entrerEmail(String email) {
        driver.findElement(emailField).sendKeys(email);
        return this;

    }


    public void clickAddRelationButton() {
        driver.findElement(addRelationButton).click();
    }


    // Vérif du message success
    public String getSuccessText() {
        return driver.findElement(successAlert).getText();
    }




}
