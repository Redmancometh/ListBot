package com.redmancometh.mcslspam.tasks;

import java.util.Map.Entry;
import java.util.concurrent.Callable;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.redmancometh.mcslspam.ServerInfo;

import javafx.util.Pair;

public class CheckListingTask implements Callable<Pair<Boolean,ServerInfo>>
{
    private CloseableHttpClient client;
    private int subID;

    public CheckListingTask(int subID)
    {
        this.client = HttpClients.createDefault();
        this.subID = subID;
    }

    @Override
    public Pair<Boolean, ServerInfo> call() throws Exception
    {
        CloseableHttpResponse response = null;
        try
        {
            HttpGet get = new HttpGet("https://api.vultr.com/v1/server/list?api_key=M4vDC0L43RCbkuloi4nwmu");
            response = client.execute(get);
            JsonObject responseData = new JsonParser().parse((EntityUtils.toString(response.getEntity()))).getAsJsonObject();
            for(Entry<String, JsonElement> e : responseData.entrySet())
            {
                if (Integer.parseInt(e.getKey()) == subID)
                {
                    JsonObject instanceInfo = e.getValue().getAsJsonObject();
                    if (getValComp("server_state", instanceInfo, "ok")&&getValComp("power_status", instanceInfo, "running")&&getValComp("status", instanceInfo, "active"))
                    {
                        client.close();
                        return new Pair(true, new ServerInfo(instanceInfo.get("main_ip").getAsString(),instanceInfo.get("default_password").getAsString()));
                    }
                    else
                    {
                        System.out.println("Server Not Yet Running...checking again in 5s");
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            response.close();
        }
        return new Pair(false, null);
    }
    
    public boolean getValComp(String key, JsonObject obj, String equals)
    {
        return obj.get(key).getAsString().equalsIgnoreCase(equals);
    }
}
