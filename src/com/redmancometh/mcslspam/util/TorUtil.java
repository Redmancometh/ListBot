package com.redmancometh.mcslspam.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.DeathByCaptcha.Captcha;
import com.redmancometh.mcslspam.SpamClient;

public class TorUtil
{
    public static void torNewIP(int portUsed)
    {
        Socket socket = null;
        Scanner in = null;
        OutputStream out = null;
        try
        {
            System.out.println("Requesting new IP for port:" + portUsed + " on port: " + (portUsed + 1));
            socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", (portUsed + 1)));
            out = socket.getOutputStream();
            in = new Scanner(socket.getInputStream());
            out.write(new String("AUTHENTICATE\r\n").getBytes());
            System.out.println(in.nextLine());
            out.flush();
            out.write(new String("SIGNAL NEWNYM\r\n").getBytes());
            System.out.println(in.nextLine());
            socket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (socket != null && !socket.isClosed())
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public static ByteArrayOutputStream getCaptchaImage(FirefoxDriver driver)
    {
        ByteArrayOutputStream os = null;
        try
        {
            WebElement cap = driver.findElementById("recaptcha_challenge_image");
            String url = cap.getAttribute("src");
            BufferedImage imgCap = ImageIO.read(new URL(url));
            os = new ByteArrayOutputStream();
            ImageIO.write(imgCap, "png", os);
            return os;
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        return null;
    }

    public static void solveCloudFlare(FirefoxDriver driver)
    {
        Captcha solved;
        try
        {
            System.out.println("Solving cloudflare");
            solved = ApacheUtil.getAnswer(getCaptchaImage(driver), SpamClient.getUserName(), SpamClient.getPassword()).get();
            WebElement responseField = driver.findElementByXPath("//input[@name='recaptcha_response_field']");
            responseField.sendKeys(solved.text);
            responseField.submit();
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
    }
}
