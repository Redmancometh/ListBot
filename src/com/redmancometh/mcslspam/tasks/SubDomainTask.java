package com.redmancometh.mcslspam.tasks;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.RandomStringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.redmancometh.mcslspam.util.TorUtil;

public class SubDomainTask implements Callable<String>
{
    private FirefoxDriver driver;
    private String IP;

    public SubDomainTask(String IP, FirefoxDriver driver)
    {
        this.IP = IP;
        TorUtil.torNewIP(9050);
        this.driver = driver;
    }

    @Override
    public String call() throws Exception
    {
        driver.navigate().to("https://freedns.afraid.org/zc.php?from=L3N1YmRvbWFpbi8=");
        driver.findElement(By.name("username")).sendKeys("aaa11284");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("enter11284");
        password.submit();
        driver.navigate().to("http://freedns.afraid.org/domain/registry/");
        List<WebElement> domainList = driver.findElements(By.xpath("html/body/table/tbody/tr/td[2]/center/center/table/tbody/tr/td[1]/a"));
        Collections.shuffle(domainList);
        WebElement domainLink = domainList.get(0);
        String domain = domainLink.getText();
        domainLink.click();
        String subdomain = RandomStringUtils.randomAlphabetic(5);
        driver.findElement(By.name("subdomain")).sendKeys(subdomain);
        WebElement address = driver.findElement(By.name("address"));
        address.clear();
        address.sendKeys(IP);
        address.submit();
        Thread.sleep(8000);
        driver.kill();
        return subdomain + "." + domain;
    }

}
