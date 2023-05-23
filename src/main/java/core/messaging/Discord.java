package core.messaging;

import communication.discord.DiscordWebHook;
import config.PrivateSharedConfig;

import java.io.File;
import java.io.IOException;

public class Discord
{
    
    static private final DiscordWebHook webHook = new DiscordWebHook(PrivateSharedConfig.BOT_URL);
    
    static public void postImage(String username, String message, String imagePath) throws IOException
    {
        webHook.setUsername(username);
        webHook.setContent(message);
        webHook.addAttachment(new DiscordWebHook.AttachmentObject(new File(imagePath), "image", imagePath));
        try
        {
            webHook.execute();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    static public void post(String username, String message)
    {
        post(username, message, false);
    }
    
    static public void post(String username, String message, boolean tts)
    {
        webHook.setUsername(username);
        webHook.setContent(message);
        webHook.setTts(tts);
        try
        {
            webHook.execute();
        } catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    

    
    static public void postAsClientManager(String message, boolean tts)
    {
        post("Client Manager", message, tts);
    }
    
    static public void postAsProvisioner(String message, boolean tts)
    {
        post("Account Provisioner", message, tts);
    }
    static public void postAsServer(String message, boolean tts)
    {
        post("Server", message, tts);
    }
    
    static public void postAsGUI(String message, boolean tts)
    {
        post("core/gui", message, tts);
    }
    
/*    static public void postImage(String filePath)
    {
        try
        {
            webHook.sendImg(filePath);
        } catch(IOException e)
        {
            System.out.println("Failed to post image.");
        }
    }*/

    
    
}