package core.clients.mule;

import core.clients.ClientManager;
import core.clients.mule.job.MuleJob;
import core.clients.mule.job.MuleToMuleJob;
import core.clients.mule.job.WorkerToMuleJob;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import core.database.tables.items.TradeItem;
import core.database.tables.reports.MuleReport;
import core.exceptions.MuleFailedException;
import lombok.extern.log4j.Log4j2;
import types.game.Item;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
public class Muler
{
    private final Set<MuleJob> openJobs = new HashSet<>();
    
    ClientManager clientManager;
    
    public Muler(ClientManager clientManager)
    {
        log.info("Initializing...");
        this.clientManager = clientManager;
    }
    
    
    public void processOpenJobs()
    {
        Iterator<MuleJob> jobsIter = openJobs.iterator();
        while(jobsIter.hasNext())
        {
            MuleJob job = jobsIter.next();
            if(job.needsMule())
            {
                setMule(job);
            }
            
            try
            {
                if(job.mule())
                {
                    finishJob(job);
                    jobsIter.remove();
                }
            } catch(MuleFailedException e)
            {
                log.warn(e);
                finishJob(job);
                jobsIter.remove();
            }
        }
    }
    
    private void setMule(MuleJob job)
    {
        Optional<MuleAccount> mule;
        if(job instanceof WorkerToMuleJob)
        {
            mule = clientManager.getDatabase().getAccountProvisioner().getMuleForWorker(job.getPrimaryAccount().getId());
        }
        else
        {
            mule = clientManager.getDatabase().getAccountProvisioner().getMuleForMule(job.getPrimaryAccount().getId());
        }
        if(mule.isEmpty())
        {
            log.warn("Couldn't find mule for " + job.getPrimaryAccount() + "");
            return;
        }
        if(!hasOpenJob(mule.get().getId()))
        {
            job.setMule(mule.get());
            log.warn("Mule set for job with " + job.getPrimaryAccount());
        }
    }
    
    
    
    private synchronized void finishJob(MuleJob muleJob)
    {
        if(muleJob.isComplete())
        {
            MuleReport report = new MuleReport();
            report.setPrimary(muleJob.getPrimaryAccount());
            report.setMule(muleJob.getMuleAccount());
            report.setStartTime(muleJob.getStartTime());
            report.setEndTime(ZonedDateTime.now());
    
            clientManager.getDatabase().getReports().save(report);
            
            for(Item item : muleJob.getPrimaryTradedItems())
            {
                if(item.quantity > 0)
                {
                    clientManager.getDatabase().getItems().add(new TradeItem(report, clientManager.getDatabase().getItems()
                            .addOrGet(item.itemID, item.itemName), item.quantity));
                }
            }
            for(Item item : muleJob.getMuleTradedItems())
            {
                if(item.quantity > 0)
                {
                    //We inverse the mules items since it traded away those items.
                    clientManager.getDatabase().getItems().add(new TradeItem(report, clientManager.getDatabase().getItems()
                            .addOrGet(item.itemID, item.itemName), item.quantity * -1));
                }
            }
            
            log.info("Mule job complete:  " + report);
        }
        else{
            log.info("Mule job failed, " + muleJob);
        }
    }
    
    
    public synchronized boolean hasJobs()
    {
        return !openJobs.isEmpty();
    }
    
    public synchronized boolean hasOpenJob(int accountID)
    {
        for(MuleJob muleJob : openJobs)
        {
            if(muleJob.isBusy(accountID))
            {
                return true;
            }
        }
        return false;
    }
    
    public synchronized void registerJob(int accountID, boolean isMule, List<Item> imports, List<Item> exports)
    {
        MuleJob muleJob;
        if(isMule)
        {
            Optional<MuleAccount> primary = clientManager.getDatabase().getAccountProvisioner().getMule(accountID);
            if(primary.isPresent())
            {
                muleJob = new MuleToMuleJob(clientManager, primary.get(), imports, exports);
            }
            else
            {
                log.warn("Could not register new mule job. Primary Mule could not be found.");
                return;
            }
        }
        else
        {
            if(hasOpenJob(accountID))
            {
                log.info(accountID + " is trying to start a job it's already part of, canceling old job.");
                jobFailed(accountID);
            }
            
            Optional<WorkerAccount> primary = clientManager.getDatabase().getAccountProvisioner().getWorker(accountID);
            if(primary.isPresent())
            {
                muleJob = new WorkerToMuleJob(clientManager, primary.get(), imports, exports);
            }
            else
            {
                log.warn("Could not register new mule job. Primary Worker could not be found.");
                return;
            }
        }
        openJobs.add(muleJob);
        log.info("Registered new MuleJob: " + muleJob);
        
    }
    
    public synchronized void jobFailed(int accountID)
    {
        for(MuleJob muleJob : openJobs)
        {
            if(muleJob.isBusy(accountID))
            {
                muleJob.failed();
            }
        }
    }
    
    
    public synchronized void jobComplete(int accountID, List<Item> tradedItems)
    {
        for(MuleJob muleJob : openJobs)
        {
            if(muleJob.isBusy(accountID))
            {
                muleJob.setCompleteResult(accountID, tradedItems);
            }
        }
/*        Optional<MuleJob> jobOptional = openJobs.stream().filter(muleJob -> muleJob.getPrimaryAccount() == accountID ||
                (muleJob.hasMule() && muleJob.getMuleID() == accountID)).findFirst();
        jobOptional.ifPresent(muleJob -> muleJob.setCompleteResult(result));*/
    }

}
