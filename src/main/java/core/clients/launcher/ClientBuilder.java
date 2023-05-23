package core.clients.launcher;

import core.config.Config;
import core.database.tables.Proxy;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.BannedAccount;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import types.World;
import types.accounts.Personality;
import types.accounts.Task;
import types.breaks.Break;
import types.breaks.BreakObject;
import types.errors.Flag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class ClientBuilder
{
    private final List<String> javaArgs = new ArrayList<>();
    private final List<String> dreambotArgs = new ArrayList<>();
    private final List<String> scriptArgs = new ArrayList<>();
    private final Set<Break> breaks = new HashSet<>();
    private String displayName;
    private String username;
    
    public ClientBuilder(boolean disableDefaultArgs)
    {
        if(!disableDefaultArgs)
        {
            defaultArguments();
        }
    }
    
    public ClientBuilder()
    {
        defaultArguments();
    }
    
    public ClientBuilder defaultArguments()
    {
        javaArgument("-Dsun.java2d.uiScale=1.0");
        javaArgument("-Xmx" + Config.MAX_CLIENT_RAM + "M");
        //dreambotLogin(PrivateConfig.DREAMBOT_USERNAME, PrivateConfig.DREAMBOT_PASSWORD);
        covert();
        freshStart();
        debug();
        layout(Layout.RESIZEABLE_CLASSIC);
        destroyOnFailure();
        dimensions(765, 503);
        script(Config.MASTER_SCRIPT_NAME);
        
        return this;
    }
    
    public ClientBuilder dreambotLogin(String dreambotUsername, String dreambotPassword)
    {
        dreambotArgs.add(0, "-username=" + "" + dreambotUsername + "");
        dreambotArgs.add(1, "-password=" + "" + dreambotPassword + "");
        
        return this;
    }

    public ClientBuilder javaArgument(String argument)
    {
        javaArgs.add(argument);
        return this;
    }
    
    public ClientBuilder dreambotArgument(String argument)
    {
        dreambotArgs.add(argument);
        return this;
    }
    
    public ClientBuilder scriptArgument(String argument)
    {
        scriptArgs.add(argument);
        return this;
    }
    
    public ClientBuilder addBreak(Break breakOption)
    {
        breaks.add(breakOption);
        return this;
    }
    
    public ClientBuilder mouseSpeed(int speed)
    {
        if(speed > 100) throw new RuntimeException("Mouse Speed is too great");
        if(speed < 1) throw new RuntimeException("Mouse Speed is too small.");
        dreambotArgument("-mouse-speed=" + speed);
        return this;
    }
    
    public ClientBuilder dimensions(int width, int height)
    {
        dreambotArgument("-width=" + width);
        dreambotArgument("-height=" + height);
        return this;
    }
    
    public ClientBuilder layout(Layout layout)
    {
        dreambotArgument("-layout=" + layout.getArgument());
        return this;
    }
    
    public ClientBuilder render(Paint paint)
    {
        dreambotArgument("-render=" + paint.getArgument());
        return this;
    }

    public ClientBuilder debug()
    {
        dreambotArgument("-debug");
        return this;
    }
    
    public ClientBuilder disableAnimations()
    {
        dreambotArgument("-disableAnimations");
        return this;
    }
    
    public ClientBuilder disableModels()
    {
        dreambotArgument("-disableModels");
        return this;
    }
    
    public ClientBuilder lowDetail()
    {
        dreambotArgument("-lowDetail");
        return this;
    }
    
    public ClientBuilder enableMenuManipulation()
    {
        dreambotArgument("-menuManipulation");
        return this;
    }
    
    public ClientBuilder enableNoClickWalk()
    {
        dreambotArgument("-noClickWalk");
        return this;
    }
    public ClientBuilder destroyOnFailure()
    {
        dreambotArgument("-destroy");
        return this;
    }
    
    public ClientBuilder destroyOnBan()
    {
        dreambotArgument("-destroy-on-ban");
        return this;
    }
    
    public ClientBuilder fps(int fpsCap)
    {
        dreambotArgument("-fps=" + fpsCap);
        return this;
    }
    
    public ClientBuilder covert()
    {
        dreambotArgument("-covert");
        return this;
    }
    
    public ClientBuilder freshStart()
    {
        dreambotArgument("-fresh");
        return this;
    }
    
    public ClientBuilder startMinimized()
    {
        dreambotArgument("-minimized");
        return this;
    }
    
    public ClientBuilder fullAccount(Account account, int world)
    {
        account(account);
        
        if(account instanceof WorkerAccount)
        {
            WorkerAccount worker = (WorkerAccount) account;
            personality(worker.getPersonality());
            
            if(worker.getTask().getWorldType() == World.MEMBERS && worker.getGameTime() == 0)
            {
                log.info("[" + worker.getDisplayName() + "] Worker needs game time, setting world to f2p.");
                world(World.FREE);
            }
            else
            {
                world(world);
            }
            return task(worker.getTask());
        }
        else if(account instanceof MuleAccount)
        {
            MuleAccount mule = (MuleAccount) account;
            //If the account requires membership but it has an issue regarding game time, then launch in f2p.
            if(mule.getGameTime() == 0)
            {
                log.info("[" + mule.getDisplayName() + "] Mule doesn't have game time, setting world to f2p.");
                world(World.FREE);
            }
            else
            {
                world(world);
            }
            return task(Task.MULE);
        }
        else if(account instanceof BannedAccount)
        {
            throw new RuntimeException("Banned Accounts cannot be launched: " + account);
        }
        
        return task(Task.TESTER);
    }
    
    public ClientBuilder fullAccount(Account account)
    {
        account(account);
        
        if(account instanceof WorkerAccount)
        {
            WorkerAccount worker = (WorkerAccount) account;
            personality(worker.getPersonality());
            
            if(worker.getTask().getWorldType() == World.MEMBERS && worker.getGameTime() == 0)
            {
                log.info("[" + worker.getDisplayName() + "] Worker needs game time, setting world to f2p.");
                world(World.FREE);
            }
            else
            {
                world(worker.getTask().getWorldType());
            }
            return task(worker.getTask());
        }
        else if(account instanceof MuleAccount)
        {
            MuleAccount mule = (MuleAccount) account;
            //If the account requires membership but it has an issue regarding game time, then launch in f2p.
            if(mule.getGameTime() == 0)
            {
                log.info("[" + mule.getDisplayName() + "] Mule doesn't have game time, setting world to f2p.");
                world(World.FREE);
            }
            else
            {
                world(World.MEMBERS);
            }
            return task(Task.MULE);
        }
        else if(account instanceof BannedAccount)
        {
            throw new RuntimeException("Banned Accounts cannot be launched: " + account);
        }
        
        return task(Task.TESTER);
    }


    public ClientBuilder account(Account account)
    {
        displayName = account.getDisplayName();
        proxy(account.getProxy());
        
        if(account.hasNote(Flag.UNINITIALIZED))
        {
            scriptArgument("initialize");
        }
        
        if(account.getBankPin() != -1)
        {
            account(account.getEmail(), account.getPassword(), account.getBankPin());
        }else{
            account(account.getEmail(), account.getPassword());
        }
        return this;
    }
    
    public ClientBuilder account(String accountNickname)
    {
        dreambotArgument("-account=" + accountNickname);
        return this;
    }
    
    public ClientBuilder account(String accountUsername, String accountPassword)
    {
        username = accountUsername;
        dreambotArgument("-accountUsername=" + accountUsername);
        dreambotArgument("-accountPassword=" + accountPassword);
        return this;
    }
    
    public ClientBuilder account(String accountUsername, String accountPassword, int bankPin)
    {
        username = accountUsername;
        dreambotArgument("-accountUsername=" + accountUsername);
        dreambotArgument("-accountPassword=" + accountPassword);
        dreambotArgument("-accountPin=" + bankPin);
        return this;
    }
    
    public ClientBuilder script(String script)
    {
        dreambotArgument("-script=" + script);
        return this;
    }
    
    public ClientBuilder proxy(Proxy proxy)
    {
        if(proxy == null) return this;
        
        proxy(proxy.getIpAddress(), proxy.getUsername(), proxy.getPassword(), proxy.getSocksPort());
        return this;
    }
    
    public ClientBuilder proxy(String ipAddress, String username, String password, int port)
    {
        dreambotArgument("-proxyHost=" + ipAddress);
        dreambotArgument("-proxyPort=" + port);
        dreambotArgument("-proxyUser=" + username);
        dreambotArgument("-proxyPass=" + password);
        return this;
    }
    
    public ClientBuilder world(World worldType)
    {
        dreambotArgument("-world=" + worldType.getArgument());
        return this;
    }
    
    public ClientBuilder world(int world)
    {
        if(world < 300) throw new RuntimeException("World cannot be less than 300");
        dreambotArgument("-world=" + world);
        return this;
    }
    
    public ClientBuilder task(Task task)
    {
        scriptArgument("task=" + task.get());
        return this;
    }
    
    public ClientBuilder personality(Personality personality)
    {
        mouseSpeed(personality.getMouseSpeed());
        for(Break pBreak: personality.getBreaks())
        {
            addBreak(pBreak);
        }
        return this;
    }
    
    public ClientBuilder task(String task)
    {
        scriptArgument("task=" + task);
        return this;
    }
    public ClientBuilder task(String task, int endLevel)
    {
        scriptArgument("task=" + task);
        scriptArgument("level=" + endLevel);
        return this;
    }
    
    public Process build() throws IOException
    {
        
        if(!breaks.isEmpty())
        {
            dreambotArgument("-breaks=" + breaks.stream().map(Break::getBreakObject).map(BreakObject::getName).collect(Collectors.joining(",")));
        }
    
        List<String> argumentList = new ArrayList<>();
        argumentList.add(Config.JAVA_PATH);
        argumentList.addAll(javaArgs);
        argumentList.add("-jar");
        argumentList.add(Config.CLIENT_PATH);
        argumentList.addAll(dreambotArgs);
        argumentList.add("-params");
        argumentList.addAll(scriptArgs);
        //System.out.println(argumentList);
        String filename = "" + System.currentTimeMillis();
        if(username != null && username.contains("@"))
        {
            filename = username.split("@")[0] + ".txt";
        }
        
        File logFile = new File(Config.LOGS_DIRECTORY + "\\" + filename);
        FileUtils.deleteQuietly(logFile);
        ProcessBuilder builder = new ProcessBuilder(argumentList.toArray(new String[0]))
                .redirectOutput(logFile)
                ;
        if(displayName != null)
        {
            builder.environment().put("DisplayName", displayName);
        }
        return builder.start();
    }
    
    public static Process launch(Account account)
    {
        try
        {
            return new ClientBuilder()
                    .fullAccount(account)
                    .build();
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static Process launch(Account account, int world)
    {
        try
        {
            return new ClientBuilder()
                    .fullAccount(account, world)
                    .build();
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    
}
