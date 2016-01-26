package com.redmancometh.mcslspam.tasks;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import com.DeathByCaptcha.Captcha;
import com.redmancometh.mcslspam.AccountInfo;
import com.redmancometh.mcslspam.SpamClient;
import com.redmancometh.mcslspam.util.ApacheUtil;
import com.redmancometh.mcslspam.util.TorUtil;

import javafx.util.Pair;

public class EmailRegisterTask implements Callable<Pair<AccountInfo, FirefoxDriver>>
{
    private FirefoxDriver driver;

    public EmailRegisterTask()
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
        driver.navigate().to("https://passport.yandex.com/registration/mail?from=mail&require_hint=1&origin=hostroot_com_l_mobile_left&retpath=https%3A%2F%2Fpassport.yandex.com%2Fpassport%3Fmode%3Dsubscribe%26from%3Dmail%26retpath%3Dhttps%253A%252F%252Fmail.yandex.com");
        String firstName = RandomStringUtils.randomAlphabetic(8);
        String lastName = RandomStringUtils.randomAlphabetic(8);
        String login = RandomStringUtils.randomAlphabetic(3) + UUID.randomUUID().toString().substring(1, 9).replace("-", "");
        String password = RandomStringUtils.randomAlphabetic(3) + UUID.randomUUID().toString().substring(1, 9).replace("-", "");
        AccountInfo account = new AccountInfo(login, password, firstName, lastName);
        driver.findElement(By.id("firstname")).sendKeys(firstName);
        driver.findElement(By.id("lastname")).sendKeys(lastName);
        driver.findElement(By.id("login")).sendKeys(login);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.id("password_confirm")).sendKeys(password);
        driver.findElement(By.className("human-confirmation-via-captcha")).click();
        try
        {
            Thread.sleep(200);
            driver.findElement(By.id("hint_question_id")).click();
            Thread.sleep(200);
            driver.findElementsByClassName("_nb-select-item").get(2).click();
            ;
        }
        catch (InterruptedException e1)
        {
            e1.printStackTrace();
        }
        driver.findElement(By.id("hint_answer")).sendKeys(UUID.randomUUID().toString().replaceAll("-", "").substring(1, 10));
        String url = driver.findElement(By.className("captcha__captcha__text")).getAttribute("src");
        try
        {
            Captcha captcha = ApacheUtil.getAnswer(ApacheUtil.getCaptchaImage(url), SpamClient.getUserName(), SpamClient.getPassword()).get();
            WebElement answer = driver.findElement(By.id("answer"));
            answer.sendKeys(captcha.text);
            Thread.sleep(2000);
            if (!driver.findElement(By.id("eula_accepted")).getAttribute("checked").equalsIgnoreCase("true"))
            {
                driver.findElement(By.id("eula_accepted")).click();
            }
            answer.submit();
            Thread.sleep(10000);
            driver.kill();
            return new Pair(account, driver);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        Thread.sleep(5000);
        return null;
    }

}
