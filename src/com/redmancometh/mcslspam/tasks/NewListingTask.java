package com.redmancometh.mcslspam.tasks;
import java.util.Random;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import com.redmancometh.mcslspam.AccountInfo;
import com.redmancometh.mcslspam.ServerInfo;
import com.redmancometh.mcslspam.util.TorUtil;
public class NewListingTask implements Runnable
{
    private AccountInfo info;
    private FirefoxDriver driver;
    private ServerInfo server;

    public NewListingTask(AccountInfo info, ServerInfo server, FirefoxDriver driver)
    {
        TorUtil.torNewIP(9050);
        this.driver=driver;
        this.info = info;
        this.server = server;
    }

    @Override
    public void run()
    {
        try
        {
            Random rand = new Random();
            driver.navigate().to("http://minecraft-server-list.com/login/login.php");
            if (isCloudFlare())
            {
                TorUtil.solveCloudFlare(driver);
                driver.navigate().to("http://minecraft-server-list.com/login/login.php");
            }
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            driver.findElement(By.name("username")).sendKeys(info.getUsername());
            WebElement password = driver.findElement(By.name("password"));
            Thread.sleep(Math.max(500, rand.nextInt(2000)));
            password.sendKeys(info.getPassword());
            Thread.sleep(Math.max(200, rand.nextInt(2000)));
            password.submit();
            driver.navigate().to("http://minecraft-server-list.com/login/dashboard.php?submitnewserver=2");
            driver.findElement(By.name("ip")).sendKeys(server.getAddress());
            driver.findElement(By.name("name")).sendKeys("NAME");
            driver.findElement(By.name("description")).sendKeys("NAME");
            driver.findElement(By.name("button")).click();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public boolean isCloudFlare()
    {
        if (driver.getTitle().equalsIgnoreCase("Attention Required! | CloudFlare"))
        {
            return true;
        }
        return false;
    }
}
