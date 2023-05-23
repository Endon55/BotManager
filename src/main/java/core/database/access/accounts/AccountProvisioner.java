package core.database.access.accounts;

import com.google.gson.JsonParser;
import config.TimeConstants;
import core.config.Config;
import core.database.access.Notes;
import core.database.access.Proxies;
import core.database.access.reports.Reports;
import core.database.tables.Note;
import core.database.tables.Proxy;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.BannedAccount;
import core.database.tables.accounts.FreshAccount;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import core.messaging.Discord;
import core.types.Shift;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.hibernate.SessionFactory;
import tools.JsonTools;
import types.SelfExpiringHashMap;
import types.accounts.MuleType;
import types.accounts.Personality;
import types.accounts.Task;
import types.errors.Flag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class AccountProvisioner
{
    private final SelfExpiringHashMap<Integer, Integer> ledger;
    private final Accounts accounts;
    private final WorkerAccounts workerAccs;
    private final MuleAccounts muleAccs;
    private final FreshAccounts freshAccs;
    private final BannedAccounts bannedAccs;
    private final Notes notes;
    private final Reports reports;
    private final Proxies proxies;
    private long lastProvision = 0;
    private long lastProfitSend;
    
    public AccountProvisioner(SessionFactory sessionFactory, Reports reports, Proxies proxies)
    {
        accounts = new Accounts(sessionFactory);
        workerAccs = new WorkerAccounts(sessionFactory);
        muleAccs = new MuleAccounts(sessionFactory);
        freshAccs = new FreshAccounts(sessionFactory);
        bannedAccs = new BannedAccounts(sessionFactory);
        notes = new Notes(sessionFactory);
        this.reports = reports;
        this.proxies = proxies;
        ledger = new SelfExpiringHashMap<>(TimeConstants.MILLISECONDS_IN_DAY);
        lastProfitSend = System.currentTimeMillis();
    }
    
    public void provision()
    {
        //6 hours have passed.
        if(System.currentTimeMillis() - lastProfitSend > 6 * TimeConstants.MILLISECONDS_IN_HOUR)
        {
            Discord.postAsProvisioner("Today's Profit: " + getTodaysProfit(), false);
            lastProfitSend = System.currentTimeMillis();
        }
        //An hour has passed.
        if(System.currentTimeMillis() - lastProvision > TimeConstants.MILLISECONDS_IN_HOUR)
        {
            if(getWorkers().size() < Config.MAX_WORKER_ACCOUNTS)
            {
                List<FreshAccount> freshAccounts = new ArrayList<>(freshAccs.getAgedAccounts());
                int accountsToProvision = freshAccounts.size() - Config.MIN_FRESH_ACCOUNTS;
                while(accountsToProvision > 0)
                {
                    Optional<WorkerAccount> worker = createWorker();
                    if(worker.isPresent())
                    {
                        accountsToProvision--;
                    }
                    else //If for whatever reason we fail to create the worker we break out of the while loop.
                    {
                        break;
                    }
                }
            }
            lastProvision = System.currentTimeMillis();
        }
    }
    
    public synchronized void banned(int accountID)
    {
        Optional<Account> accountOpt = accounts.get(accountID);
        if(accountOpt.isEmpty())
        {
            log.error("Could not find account that needs banning.");
            return;
        }
        Account account = accountOpt.get();
        
        //Already has a temp ban, we assume it's now perma banned.
        if(account.hasNote(Flag.TEMPORARY_BAN))
        {
            log.info("Permanently decommissioning account with prior temp ban. " + account);
            if(account instanceof MuleAccount)
            {
                banAccount(accountID, Task.MULE, new Date(), reports.getJobsCompleted(accountID));
            }
            else if(account instanceof WorkerAccount)
            {
                banAccount(accountID, ((WorkerAccount) account).getTask(), new Date(), reports.getRuntime(accountID));
                replaceWorker((WorkerAccount) account);
            }
            else
            {
                log.info("Temp Banned account is unrecognized: " + account);
            }
        }
        else if(account instanceof MuleAccount)
        {
            log.info("Unassigning mule first temp ban. " + account);
            addNote(account, Flag.TEMPORARY_BAN, ZonedDateTime.now().toString());
            accounts.setStartTime(account, ZonedDateTime.now().plusDays(Config.TEMP_BAN_DAYS));
            
            reassignMule((MuleAccount) account);
        }
        else if(account instanceof WorkerAccount)
        {
            ZonedDateTime nextStart = ZonedDateTime.now().plusDays(Config.TEMP_BAN_DAYS);
            log.info("Temporarily banning " + account + ", will check ban status again in " + Config.TEMP_BAN_DAYS + " days.");
            addNote(account, Flag.TEMPORARY_BAN, nextStart.toString());
            accounts.setStartTime(account, nextStart);
        }
        else
        {
            log.info("Banned account is unrecognized: " + account);
        }
    }
    
    private synchronized void replaceWorker(WorkerAccount worker)
    {
        if(freshAccs.getRemainingAgedAccounts() > 0)
        {
            createWorker(worker.getTask(), worker.getShift(), worker.getPersonality(), muleAccs.getFreeMule()
                    .orElse(null));
        }
        else log.info("No remaining fresh accounts to replace worker with.");
    }
    private synchronized void reassignMule(MuleAccount mule)
    {
        for(WorkerAccount worker : mule.getWorkers())
        {
            //Replace mule with another free mule.
            //If there isn't another free mule, reallocate a fresh mule.
            Optional<MuleAccount> freeMule = muleAccs.getFreeMule();
            if(freeMule.isPresent())
            {
                workerAccs.setMule(worker, freeMule.get());
            }
            else{
                workerAccs.setMule(worker, null);
            }
        }
        
        if(freshAccs.getRemainingAgedAccounts() > 0)
        {
            createMule(MuleType.STANDARD);
        }
    }

    public synchronized void banAccount(int accountID, Task task, Date date, long runtime)
    {
        accounts.convertToBanned(accountID);
        Optional<BannedAccount> bannedOpt = getBanned(accountID);
        
        if(bannedOpt.isEmpty())
        {
            log.error("Banned account could not be found after conversion from worker.");
            return;
        }
        bannedOpt.get().setTask(task);
        bannedOpt.get().setBanDate(date);
        bannedOpt.get().setRuntime(runtime);
        
        bannedAccs.update(bannedOpt.get());
    }
    
    private synchronized Optional<MuleAccount> createMule(MuleType muleType)
    {
        Optional<FreshAccount> freshOpt = freshAccs.getAgedAccount();
        if(freshOpt.isEmpty())
        {
            Discord.postAsProvisioner("No more fresh accounts, please add more soon.", false);
            return Optional.empty();
        }
        int freshID = freshOpt.get().getId();
        accounts.convertToMule(freshID);
        Optional<MuleAccount> muleOpt = getMule(freshID);
        
        if(muleOpt.isEmpty())
        {
            log.error("Worker account could not be found after conversion from fresh.");
            return Optional.empty();
        }
        else
        {
            log.info(muleOpt.get().getDisplayName() + " Turned into Mule");
        }
        
        MuleAccount mule = muleOpt.get();
        
        mule.setCash(0);
        mule.setType(muleType);
        addNote(mule, Flag.UNINITIALIZED, "");
        muleAccs.update(mule);
        
        return getMule(freshID);
    }
    
    private synchronized Optional<WorkerAccount> createWorker()
    {
        Task task = (getTrekkerSanfewRatio() > Config.TREKKER_SANFEW_RATIO ? Task.SANFEW_PRODUCTION : Task.TEMPLE_TREKKING);
        
        return createWorker(task, getWeakestShift(), Personality.getRandom(), null);
    }
    private synchronized Optional<WorkerAccount> createWorker(Task task, Shift shift, Personality personality, MuleAccount mule)
    {
        if(task == Task.MULE) throw new RuntimeException("Task cannot be of type: MULE");
        Optional<FreshAccount> freshOpt = freshAccs.getAgedAccount();
        if(freshOpt.isEmpty())
        {
            Discord.postAsProvisioner("No more fresh accounts, please add more soon.", false);
            return Optional.empty();
        }
        int freshID = freshOpt.get().getId();
        
        accounts.convertToWorker(freshID);
        
        Optional<WorkerAccount> workerOpt = getWorker(freshID);
        
        if(workerOpt.isEmpty())
        {
            log.error("Worker account could not be found after conversion from fresh.");
            return Optional.empty();
        }
        else{
            log.info(workerOpt.get().getDisplayName() + " Turned into Worker");
        }
        WorkerAccount worker = workerOpt.get();
    
        worker.setTask(task);
        worker.setShift(shift);
        worker.setPersonality(personality);
        worker.setMule(mule);
        log.info(freshOpt.get() + " turned into Worker with Task: " + task + ", Shift: " + shift + ", and Personality: " + personality);
        workerAccs.update(worker);
        addNote(worker, Flag.UNINITIALIZED, "");
        return getWorker(freshID);
    }
    
    public synchronized Optional<MuleAccount> getMuleForWorker(int workerID)
    {
        Optional<WorkerAccount> workerOpt = getWorker(workerID);
        
        if(workerOpt.isPresent())
        {
            MuleAccount mule = workerOpt.get().getMule();
            if(mule != null) return Optional.of(mule);
            else
            {
                WorkerAccount worker = workerOpt.get();
                Optional<MuleAccount> freeMule = muleAccs.getFreeMule();
                if(freeMule.isEmpty())
                {
                    log.error("No mules available");
                    return Optional.empty();
                }
                workerAccs.setMule(worker, freeMule.get());
                return Optional.ofNullable(worker.getMule());
            }
        }
        else return Optional.empty();
    }
    
    public synchronized Optional<MuleAccount> getMuleForMule(int muleID)
    {
        //Check for free mules first.
        for(MuleAccount mule : muleAccs.getFreeMules())
        {
            if(mule.getId() != muleID)
            {
                return Optional.of(mule);
            }
        }
        log.info("No free mules available for mule(" + muleID + "). Checking all mules.");
        for(MuleAccount mule : muleAccs.getAll())
        {
            if(mule.getId() != muleID)
            {
                return Optional.of(mule);
            }
        }
        
        return Optional.empty();
    }
    
    public synchronized void addFreshAccount(FreshAccount freshAccount)
    {
        freshAccs.save(freshAccount);
    }
    
    public synchronized void addWorkerAccount(WorkerAccount workerAccount)
    {
        workerAccs.save(workerAccount);
    }
    
    public synchronized void addMuleAccount(MuleAccount muleAccount)
    {
        muleAccs.save(muleAccount);
    }
    
    public synchronized void setMule(WorkerAccount worker, MuleAccount mule)
    {
        worker.setMule(mule);
        workerAccs.update(worker);
        mule.addWorker(worker);
        muleAccs.update(mule);
    }
    
    
    private double getTrekkerSanfewRatio()
    {
        int trekkers = 0;
        int sanfews = 0;
        for(WorkerAccount worker :
                workerAccs.getAll())
        {
            if(worker.getTask() == Task.TEMPLE_TREKKING) trekkers++;
            else if(worker.getTask() == Task.SANFEW_PRODUCTION) sanfews++;
        }
        if(trekkers == 0) return 0;
        if(sanfews == 0) return trekkers / .1;
        return trekkers / (double) sanfews;
    }
     public Shift getWeakestShift()
     {
         int[] workersOnShift = new int[Shift.values().length];
         Arrays.fill(workersOnShift, 0);
         for(WorkerAccount worker : getWorkers())
         {
            workersOnShift[worker.getShift().ordinal()]++;
         }
         System.out.println("Workers On Shift: " + Arrays.toString(workersOnShift));
         int lowest = 0;
         for(int i = 1; i < workersOnShift.length; i++)
         {
             if(workersOnShift[lowest] > workersOnShift[i])
             {
                 lowest = i;
             }
         }
         return Shift.values()[lowest];
     }
    
    
    public synchronized void setCash(int accountID, int cash)
    {
        int cashDif = accounts.setCash(accountID, cash);
        if(cashDif != 0)
        {
            ledger.put(accountID, cashDif);
        }
    }
    
    public synchronized int getTodaysProfit()
    {
        int profit = 0;
        for(Map.Entry<Integer, Integer> record : ledger.entrySet())
        {
            profit += record.getValue();
        }
        return profit;
    }
    
    public synchronized void setStartTime(int accountID, ZonedDateTime startTime)
    {
        accounts.setStartTime(accountID, startTime);
    }

    public synchronized void addNote(int accountID, Flag flag, String details)
    {
        Optional<Account> accountOpt = accounts.get(accountID);
        if(accountOpt.isEmpty())
        {
            log.error("Could not find account to add note.");
            return;
        }
        
        addNote(accountOpt.get(), flag, details);
    }
    
    public synchronized void addNote(Account account, Flag flag, String details)
    {
        Note note = new Note(account, flag, details);
        notes.save(note);
        log.info("Note Saved. " + account + " " + note);
    }
    
    public synchronized void removeNote(Note note)
    {
        notes.delete(note);
        log.info("Note Removed. " + note.getAccount() + " " + note);
    }
    public synchronized void removeNote(int accountID, Flag flag)
    {
        Optional<Account> accountOpt = accounts.get(accountID);
        if(accountOpt.isEmpty())
        {
            log.error("Could not find account to remove note.");
            return;
        }
        for(Note note : accountOpt.get().getNotes())
        {
            if(note.getFlag() == flag)
            {
                removeNote(note);
            }
        }
    }
    
    public synchronized void setGameTime(int accountID, int gameTime)
    {
        accounts.setGameTime(accountID, gameTime);
    }
    
    public synchronized Optional<Account> getAccount(int accountID)
    {
        return accounts.get(accountID);
    }
    
    public synchronized Optional<Account> getAccount(String displayName)
    {
        return accounts.get(displayName);
    }
    
    public synchronized Optional<WorkerAccount> getWorkerAccount(String displayName)
    {
        return workerAccs.get(displayName);
    }
    
    public synchronized Optional<WorkerAccount> getWorker(int workerID)
    {
        return workerAccs.get(workerID);
    }
    
    public synchronized Optional<MuleAccount> getMule(int muleID)
    {
        return muleAccs.get(muleID);
    }
    
    public synchronized Optional<MuleAccount> getMule(String displayName)
    {
        return muleAccs.get(displayName);
    }
    
    public synchronized Optional<BannedAccount> getBanned(int bannedID)
    {
        return bannedAccs.get(bannedID);
    }
    
    public synchronized List<MuleAccount> getAllMules()
    {
        return muleAccs.getAll();
    }
    
    public synchronized List<WorkerAccount> getWorkers()
    {
        return workerAccs.getAll();
    }
    
    public synchronized List<FreshAccount> getFresh()
    {
        return freshAccs.getAll();
    }
    
    public synchronized List<BannedAccount> getBanned()
    {
        return bannedAccs.getAll();
    }
    
    public synchronized void exportFreshAccounts(boolean wipeExportedAccounts)
    {
        List<FreshAccount> accounts = getFresh();
        log.info("Converting fresh accounts list to json. " + accounts);
        try
        {
            FileWriter writer = new FileWriter(Config.ACCOUNTS_FILE);
            writer.write(JsonTools.toString(JsonTools.listToJson(accounts)));
            writer.close();
            
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    
        log.info("Fresh accounts written to " + Config.ACCOUNTS_FILE);
        
        if(wipeExportedAccounts)
        {
            log.info("Deleting exported accounts from local database.");
            for(FreshAccount account :
                    accounts)
            {
                freshAccs.delete(account);
            }
        }
    }
    
    public synchronized void importFreshAccounts()
    {
        File accounts = new File(Config.ACCOUNTS_FILE);
        if(!accounts.exists())
        {
            log.info("Accounts file does not exist.");
            return;
        }
        log.info("Reading accounts file.");
        StringBuilder resultStringBuilder = new StringBuilder();
        try(BufferedReader br  = new BufferedReader(new InputStreamReader(new FileInputStream(accounts))))
        {
            String line;
            while((line = br.readLine()) != null)
            {
                resultStringBuilder.append(line).append("\n");
            }
        } catch(IOException e)
        {
            throw new RuntimeException(e);
        }
        for(FreshAccount imported :
                JsonTools.jsonToList(JsonParser.parseString(resultStringBuilder.toString()).getAsJsonArray(), FreshAccount.class))
        {
            log.info("Account Import: " + imported);
            if(this.accounts.get(imported.getDisplayName()).isPresent())
            {
                log.info("Skipping account that already exists." + imported);
                continue;
            }
            
            FreshAccount fresh = new FreshAccount(imported.getDisplayName(), imported.getEmail(), imported.getPassword(),
                    imported.getBankPin(), imported.getGameTime(), imported.getCash(), imported.getCreationDate(),
                    imported.getBirthday(), imported.getStartTime(), imported.getProxy(), imported.getNotes());
            
            Optional<Proxy> proxyOpt = proxies.get(imported.getProxy().getIpAddress());
            if(proxyOpt.isPresent())
            {
                fresh.setProxy(proxyOpt.get());
            }
            else
            {
                Proxy proxy = new Proxy(imported.getProxy().getProvider(), imported.getProxy().getIpAddress(),
                        imported.getProxy().getUsername(), imported.getProxy().getPassword(), imported.getProxy().getSocksPort());
    
                log.info("Saving new proxy to database. " + proxy);
                proxies.save(proxy);
                fresh.setProxy(proxy);
            }
            log.info("Account imported.");
            freshAccs.save(fresh);
        }
        log.info("All accounts imported successfully.");
        
        FileUtils.deleteQuietly(accounts);
        log.info("Deleting account file.");
    }
    
    
    public Accounts getAccounts()
    {
        return accounts;
    }
    
    public FreshAccounts getFreshAccs()
    {
        return freshAccs;
    }
    
    public WorkerAccounts getWorkerAccs()
    {
        return workerAccs;
    }
    
    public MuleAccounts getMuleAccs()
    {
        return muleAccs;
    }
    
    public void close()
    {
        accounts.close();
        workerAccs.close();
        muleAccs.close();
        freshAccs.close();
        bannedAccs.close();
    }
    
}
