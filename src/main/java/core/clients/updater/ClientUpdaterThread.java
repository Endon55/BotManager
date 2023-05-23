package core.clients.updater;

import core.config.Config;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
@Log4j2
public class ClientUpdaterThread implements Runnable
{
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread thread;
    private final long startTime;
    
    private Process launcher;
    private volatile LauncherStatus status = LauncherStatus.UPDATING;
    
    
    
    
    public ClientUpdaterThread() throws IOException
    {
        log.info("Opening Dreambot launcher...");
        launcher = startLauncher();
        log.info("Launcher opened");
        startTime = System.currentTimeMillis();
        
    }
    
    private Process startLauncher() throws IOException
    {
        return new ProcessBuilder(Config.JAVA_PATH, "-jar", Config.LAUNCHER_PATH).start();
    }
    
    public void start()
    {
        running.set(true);
        thread = new Thread(this);
        thread.start();
    }
    @Override
    public void run()
    {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(launcher.getInputStream()));
        
        //status doesn't equal failure or up to date
        while(running.get() && !isFinished() && launcher.isAlive())
        {
            try
            {
                String line = bufferedReader.readLine();
                
                if(line.startsWith("Downloading"))
                {
                    status = LauncherStatus.UPDATING;
                }
                else if(line.startsWith("Downloaded") || line.startsWith("Client is up to date"))
                {
                    status = LauncherStatus.UP_TO_DATE;
                    log.info("Successfully updated clients");
                    killLauncher();
                }
                else
                {
                    status = LauncherStatus.FAILURE;
                    log.error("Failed to update clients");
                    killLauncher();
                }
                
            } catch(IOException e)
            {
                status = LauncherStatus.FAILURE;
                log.error("Failed to read launcher stream", e);
            }
        }
    }
    public void stop()
    {
        killLauncher();
        running.set(false);
        thread.interrupt();
        log.info("Finished");
    }
    
    public synchronized void killLauncher()
    {
        if(launcher.isAlive())
        {
            launcher.destroy();
            log.info("Launcher destroyed");
        }
        
    }
    
    
    public synchronized long getElapsedTime()
    {
        return System.currentTimeMillis() - startTime;
    }
    
    public synchronized boolean isFinished()
    {
        return status == LauncherStatus.FAILURE || status == LauncherStatus.UP_TO_DATE;
    }
    public synchronized LauncherStatus getStatus()
    {
        return status;
    }
    
    public enum LauncherStatus
    {
        UPDATING,
        UP_TO_DATE,
        FAILURE
    }
    
    
}
