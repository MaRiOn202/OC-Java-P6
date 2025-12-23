package com.openclassrooms.payMyBuddy.seleniumTests.pages;


import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


@Getter
@Setter
public class HomePage {



    private final WebDriver driver;

    private By transfertLink = By.cssSelector("a[href='/transfert']");


    public HomePage(WebDriver driver) {
        this.driver = driver;
    }


    public boolean userConnected() {
        return !driver.findElements(transfertLink).isEmpty();          // size() > 0
    }


}
