package com.redmancometh.mcslspam.tasks;

import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.redmancometh.mcslspam.util.ApacheUtil;

import javafx.util.Pair;

public class VultrCreationTask implements Callable<Integer>
{
    private CloseableHttpClient client;

    public VultrCreationTask()
    {
        this.client = HttpClients.createDefault();
    }

    public Integer createInstance()
    {
        CloseableHttpResponse response = null;
        try
        {
            response = client.execute(ApacheUtil.buildPost("https://api.vultr.com/v1/server/create?api_key=M4vDC0L43RCbkuloi4nwmu", new Pair("DCID", "24"), new Pair("VPSPLANID", "29"), new Pair("OSID", "161")));
            HttpEntity entity = response.getEntity();
            int subID = Integer.parseInt(EntityUtils.toString(entity).split(":")[1].replace("\"", "").replace("}", ""));
            EntityUtils.consume(entity);
            return subID;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (response != null)
            {
                try
                {
                    response.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setupInstance()
    {

    }

    @Override
    public Integer call() throws Exception
    {
        return createInstance();
    }

}
