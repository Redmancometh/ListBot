package com.redmancometh.mcslspam.tasks;
import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.redmancometh.mcslspam.AccountInfo;
import com.redmancometh.mcslspam.util.TorUtil;
public class VerifyEmailTask implements Runnable
{
    private AccountInfo account;
    private FirefoxDriver driver;

    public VerifyEmailTask(AccountInfo account, FirefoxDriver driver)
    {
        this.driver=driver;
        TorUtil.torNewIP(9050);
        this.account = account;
    }

    public void recoverEmail()
    {
        driver.navigate().to("http://minecraft-server-list.com/login/passwordrecovery.php");
        WebElement send = driver.findElement(By.name("remail"));
        send.sendKeys(account.getAddress());
        send.submit();
    }

    public boolean tryVerify()
    {
        boolean gotEmail = false;
        for (WebElement element : driver.findElements(By.className("b-messages__subject")))
        {
            if (element.getAttribute("title").contains("Minecraft Server"))
            {
                element.click();
                gotEmail = true;
            }
        }
        if (gotEmail)
        {
            String link = driver.findElement(By.xpath("//tr/td/div/div/div[2]/div/div/p/a[2]")).getText();
            driver.navigate().to(link);
            if (isCloudFlare())
            {
                Random rand = new Random();
                TorUtil.solveCloudFlare(driver);
                driver.navigate().to(link);
                try
                {
                    Thread.sleep(Math.max(500, rand.nextInt(2000)));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                WebElement email = driver.findElement(By.name("vemail"));
                email.sendKeys(account.getAddress());
                try
                {
                    Thread.sleep(Math.max(500, rand.nextInt(2000)));
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                email.submit();
            }
            return true;
        }
        return false;
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
        driver.navigate().to("https://mail.yandex.com/");
        driver.findElement(By.name("passwd")).sendKeys(account.getPassword());
        WebElement login = driver.findElement(By.name("login"));
        login.sendKeys(account.getUsername());
        login.submit();
        int x = 0;
        while (true)
        {
            driver.navigate().refresh();
            if (tryVerify())
            {
                break;
            }
            if (x > 5)
            {
                recoverEmail();
                run();
                break;
            }
            try
            {
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

}
