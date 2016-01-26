package com.redmancometh.mcslspam;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.firefox.FirefoxDriver;

import com.redmancometh.mcslspam.tasks.CheckListingTask;
import com.redmancometh.mcslspam.tasks.VerifyEmailTask;
import com.redmancometh.mcslspam.tasks.EmailRegisterTask;
import com.redmancometh.mcslspam.tasks.MCSLRegisterTask;
import com.redmancometh.mcslspam.tasks.NewListingTask;
import com.redmancometh.mcslspam.tasks.ServerSetupTask;
import com.redmancometh.mcslspam.tasks.SubDomainTask;
import com.redmancometh.mcslspam.tasks.VultrCreationTask;

import javafx.util.Pair;

public class SpamClient
{
    private int subId;
    private ServerInfo info;
    private static String userName;
    private static String password;
    private static ExecutorService execPool = Executors.newFixedThreadPool(5);
    private AccountInfo mailAcct;
    
    public SpamClient(String userName, String password)
    {
        SpamClient.userName = userName;
        SpamClient.password = password;
    }

    public void start()
    {
        try
        {
            this.subId = execPool.submit(new VultrCreationTask()).get();
            CheckListingTask task = new CheckListingTask(subId);
            while (true)
            {
                Pair<Boolean, ServerInfo> checkInfo = execPool.submit(task).get();
                if (checkInfo.getKey())
                {
                    this.info = checkInfo.getValue();
                    break;
                }
                Thread.sleep(5000);
            }
            while (true)
            {
                if (execPool.submit(new ServerSetupTask(info.getAddress(), info.getPassword())).get())
                {
                    break;
                }
                Thread.sleep(5000);
            }
            Pair<AccountInfo, FirefoxDriver> spamInfo = new EmailRegisterTask().call();
            this.mailAcct=spamInfo.getKey();
            mailAcct.saveToFile();
            String domain = new SubDomainTask(info.getAddress(), spamInfo.getValue()).call();
            new MCSLRegisterTask(domain, mailAcct, spamInfo.getValue()).run();
            new VerifyEmailTask(mailAcct, spamInfo.getValue()).run();
            new NewListingTask(mailAcct, info, spamInfo.getValue()).run();
            Thread.sleep(10000);
            spamInfo.getValue().kill();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        System.out.println("Executed Creation Task");
        try
        {
            Thread.sleep(TimeUnit.HOURS.toMillis(2));
            start();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static String getUserName()
    {
        return userName;
    }

    public static String getPassword()
    {
        return password;
    }
}
