package com.redmancometh.mcslspam.tasks;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.Select;

import com.redmancometh.mcslspam.AccountInfo;
import com.redmancometh.mcslspam.util.TorUtil;
import javafx.util.Pair;


//TODO: Finish this.

public class LiveRegisterTask implements Callable<Pair<AccountInfo, FirefoxDriver>>
{
    private FirefoxDriver driver;
    public LiveRegisterTask()
    {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("network.proxy.type", 1);
        profile.setPreference("network.proxy.socks", "127.0.0.1");
        profile.setPreference("network.proxy.socks_port", 9050);
        profile.setPreference("network.proxy.socks_version", 5);
        profile.setPreference("places.history.enabled", false);
        profile.setPreference("privacy.clearOnShutdown.offlineApps", true);
        profile.setPreference("privacy.clearOnShutdown.passwords", true);
        profile.setPreference("privacy.clearOnShutdown.siteSettings", true);
        profile.setPreference("privacy.sanitize.sanitizeOnShutdown", true);
        profile.setPreference("signon.rememberSignons", false);
        profile.setPreference("network.cookie.lifetimePolicy", 2);
        profile.setPreference("network.dns.disablePrefetch", true);
        profile.setPreference("network.http.sendRefererHeader", 0);
        profile.setPreference("network.proxy.socks_remote_dns", true);
        TorUtil.torNewIP(9050);
        this.driver = new FirefoxDriver(profile);
        driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
    }

    @Override
    public Pair<AccountInfo, FirefoxDriver> call() throws Exception
    {
        AccountInfo info = AccountInfo.generateRandom();
        driver.findElement(By.id("FirstName")).sendKeys(info.getFirstName());
        driver.findElement(By.id("LastName")).sendKeys(info.getLastName());
        driver.findElement(By.id("liveEasiSwitch")).click();
        driver.findElement(By.id("MemberName")).sendKeys(info.getUsername());
        driver.findElement(By.id("Password")).sendKeys(info.getPassword());
        driver.findElement(By.id("RetypePassword")).sendKeys(info.getPassword());
        Select month = new Select(driver.findElement(By.id("BirthMonth")));
        Select day = new Select(driver.findElement(By.id("BirthDay")));
        Select year = new Select(driver.findElement(By.id("BirthYear")));
        List<WebElement> monthOptions = month.getOptions();
        List<WebElement> dayOptions = day.getOptions();
        List<WebElement> yearOptions = year.getOptions();
        Collections.shuffle(month.getOptions());
        Collections.shuffle(day.getOptions());
        Collections.shuffle(year.getOptions());
        month.selectByVisibleText(monthOptions.get(0).getText());
        day.selectByVisibleText(dayOptions.get(0).getText());
        year.selectByVisibleText(yearOptions.get(0).getText());
        return null;
    }

}
