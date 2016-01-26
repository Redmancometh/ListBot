package com.redmancometh.mcslspam;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

public class AccountInfo
{

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    public AccountInfo(String username, String password, String firstName, String lastName)
    {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public static AccountInfo generateRandom()
    {
        String firstName = RandomStringUtils.randomAlphabetic(8);
        String lastName = RandomStringUtils.randomAlphabetic(8);
        String login = RandomStringUtils.randomAlphabetic(3) + UUID.randomUUID().toString().substring(1, 9).replace("-", "");
        String password = RandomStringUtils.randomAlphabetic(3) + UUID.randomUUID().toString().substring(1, 9).replace("-", "");
        return new AccountInfo(login, password, firstName, lastName);
    }

    public void saveToFile()
    {
        File f = new File("emails.txt");
        if (!f.exists())
        {
            try
            {
                f.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        FileWriter toFile = null;
        BufferedWriter writer = null;
        try
        {
            toFile = new FileWriter(f, true);
            writer = new BufferedWriter(toFile);
            writer.write("\n" + username + "\n\tPassword: " + password + "\n\tFirstName: " + firstName + "\n\tLastName: " + lastName);
            writer.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            try
            {
                toFile.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public String getAddress()
    {
        return this.username+"@yandex.com";
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

}
