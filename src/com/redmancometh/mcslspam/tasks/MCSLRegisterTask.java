package com.redmancometh.mcslspam.tasks;
import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.redmancometh.mcslspam.AccountInfo;
import com.redmancometh.mcslspam.util.TorUtil;
public class MCSLRegisterTask implements Runnable
{
    private FirefoxDriver driver;
    private AccountInfo account;

    public MCSLRegisterTask(String domain, AccountInfo info, FirefoxDriver driver)
    {
        this.account = info;
        this.driver=driver;
        TorUtil.torNewIP(9050);
    }

    public boolean isCloudFlare()
    {
        if (driver.getTitle().equalsIgnoreCase("Attention Required! | CloudFlare"))
        {
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        try
        {
            Random rand = new Random();
            driver.navigate().to("http://minecraft-server-list.com/login/signup.php");
            if (isCloudFlare())
            {
                TorUtil.solveCloudFlare(driver);
                driver.navigate().to("http://minecraft-server-list.com/login/signup.php");
            }
            driver.findElement(By.name("form_username")).sendKeys(account.getUsername());
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            driver.findElement(By.name("form_password")).sendKeys(account.getPassword());
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            driver.findElement(By.name("form_confirm_password")).sendKeys(account.getPassword());
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            driver.findElement(By.name("form_email")).sendKeys(account.getAddress());
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            WebElement confirm = driver.findElement(By.name("form_confirm_email"));
            confirm.sendKeys(account.getAddress());
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            driver.findElement(By.name("signup")).click();
            Thread.sleep(10000);
            driver.kill();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
