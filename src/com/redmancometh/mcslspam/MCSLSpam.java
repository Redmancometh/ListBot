package com.redmancometh.mcslspam;

public class MCSLSpam
{

    public static void main(String[] args)
    {
        SpamClient client = new SpamClient(args[0], args[1]);
        client.start();
    }
}
