package com.redmancometh.mcslspam.tasks;

import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class ServerSetupTask implements Callable<Boolean>
{
    private String password;
    private String address;

    public ServerSetupTask(String address, String password)
    {
        this.password = password;
        this.address = address;
    }

    @Override
    public Boolean call()
    {
        PrintWriter out = null;
        Scanner in = null;
        Session sshSession;
        Channel channel;
        try
        {
            JSch jsch = new JSch();
            sshSession = jsch.getSession("root", address, 22);
            sshSession.setConfig("StrictHostKeyChecking", "no");
            sshSession.setPassword(password);
            sshSession.connect();
            channel = sshSession.openChannel("shell");
            channel.connect();
            System.out.println("channel connected");
            in = new Scanner(channel.getInputStream());
            out = new PrintWriter(channel.getOutputStream());
            out.write("sysctl net.ipv4.ip_forward=1\r\n");
            out.flush();
            System.out.println("Ran IP Forward");
            out.write("iptables -t nat -A PREROUTING -p tcp --dport 25565 --source 104.200.31.26 -j DNAT --to-destination 23.254.0.26:25567\r\n");
            out.flush();
            out.write("iptables -t nat -A PREROUTING -p tcp --dport 25565 -j DNAT --to-destination 23.254.0.26:25565\r\n");
            out.flush();
            System.out.println("Ran redirect command to 25565: ");
            out.write("iptables -t nat -A POSTROUTING -j MASQUERADE\r\n");
            out.flush();
            System.out.println("Ran Masq Command: " + in.next());
            return true;
        }
        catch (Throwable e)
        {
            System.out.println("Failed SSH Connection, trying again in 5s");
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
            if (out != null)
            {
                out.close();
            }
        }
        return false;
    }

}
