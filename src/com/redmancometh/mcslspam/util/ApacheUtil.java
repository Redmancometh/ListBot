package com.redmancometh.mcslspam.util;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.imageio.ImageIO;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import com.DeathByCaptcha.Captcha;
import com.DeathByCaptcha.Exception;
import com.DeathByCaptcha.SocketClient;
import javafx.util.Pair;
public class ApacheUtil
{
    public static HttpPost buildPost(String URL, Pair<String, String>... params)
    {
        HttpPost post = new HttpPost(URL);
        List<NameValuePair> paramList = new ArrayList();
        for (Pair<String, String> p : params)
        {
            paramList.add(new BasicNameValuePair(p.getKey(), p.getValue()));
        }
        try
        {
            post.setEntity(new UrlEncodedFormEntity(paramList));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return post;
    }

    public static ByteArrayOutputStream getCaptchaImage(String url)
    {
        ByteArrayOutputStream os = null;
        try
        {
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
    
    public static CompletableFuture<Captcha> getAnswer(ByteArrayOutputStream image, String userName, String password)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            SocketClient client = new SocketClient(userName, password);
            Captcha res;
            try
            {
                res = client.decode(new ByteArrayInputStream(image.toByteArray()));
                return res;
            }
            catch (IOException | Exception | InterruptedException e)
            {
                e.printStackTrace();
                return null;
            }
            finally
            {
                client.close();
            }
        });
    }

}
