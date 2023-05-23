package core.clients.updater;

import config.TimeConstants;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ClientUpdater
{
    
    private final long processTimeout = 3 * TimeConstants.MILLISECONDS_IN_MINUTE;
    
    private static final AtomicBoolean outOfDate = new AtomicBoolean(true);
    private ClientUpdaterThread updater;
    
    public synchronized void updateClients()
    {
        if(updater == null)
        {
            log.info("Client out of date, launching updater");
            try
            {
                updater = new ClientUpdaterThread();
                updater.start();
            } catch(IOException e)
            {
                updater.stop();
                log.error("Failed to start client updater", e);
                updater = null;
            }
        }
        ClientUpdaterThread.LauncherStatus updateStatus = updater.getStatus();
        if(updateStatus == ClientUpdaterThread.LauncherStatus.UP_TO_DATE)
        {
            outOfDate.set(false);
            updater.stop();
            log.info("Clients Updated");
            updater = null;
            
        }
        else if(updateStatus == ClientUpdaterThread.LauncherStatus.FAILURE)
        {
            updater.stop();
            log.error("Failed to update clients.");
            updater = null;
        }
        else if(updater.getElapsedTime() > processTimeout)
        {
            updater.stop();
            log.error("Updater timed out.");
            updater = null;
        }
    }
    
    public static synchronized boolean isOutOfDate()
    {
        return outOfDate.get();
    }
    
    public static synchronized void setOutOfDate()
    {
        if(!outOfDate.get())
        {
            log.info("Clients are out of date");
        }
        outOfDate.set(true);
    }
    
}
