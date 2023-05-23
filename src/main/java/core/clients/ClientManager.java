package core.clients;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import core.clients.launcher.ClientBuilder;
import core.clients.mule.Muler;
import core.clients.sockets.Session;
import core.clients.updater.ClientUpdater;
import core.config.Config;
import core.database.Database;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.WorkerAccount;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import tools.JsonTools;
import types.accounts.Task;
import types.breaks.Break;
import types.breaks.BreakObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ClientManager
{

    private final ZonedDateTime startTime;
    

    
    private static final AtomicBoolean autoLaunchClients = new AtomicBoolean(true);
    
    private static final List<Session> unpairedSessions = new ArrayList<>();
    private static final Map<Integer, Process> unpairedProcesses = new ConcurrentHashMap<>();
    private static final Map<Integer, Session> sessions = new ConcurrentHashMap<>();
    
    private final Muler muler;
    private final ClientUpdater updater;
    
    private final Database database;
    
    public ClientManager(Database database, boolean autoLaunchClients)
    {
        log.info("Starting...");
        this.database = database;
        muler = new Muler(this);
        ClientManager.autoLaunchClients.set(autoLaunchClients);
        this.updater = new ClientUpdater();
        startTime = ZonedDateTime.now().plusSeconds(Config.CONNECTION_DELAY_SECONDS);
        loadSettings();
        log.info("Started");
    }
    
    public void tick()
    {
        if(ClientUpdater.isOutOfDate())
        {
            if(getMuler().hasJobs())
            {
                getMuler().processOpenJobs();
            }
            if(shutdownGently())
            {
                updater.updateClients();
            }
        }
        else
        {
            if(getMuler().hasJobs())
            {
                getMuler().processOpenJobs();
            }
            if(unpairedProcesses.size() < Config.MAX_LAUNCHING_CLIENTS && getAutoLaunchClients())
            {
                manageWorkers();
            }
        }
        
        //Can't forget to remove the entry from the map.
        sessions.entrySet().removeIf(sessionEntry -> !sessionEntry.getValue().validate());
        unpairedProcesses.entrySet().removeIf(processEntry -> !processEntry.getValue().isAlive());
    }

    private void manageWorkers()
    {
        ZonedDateTime now = ZonedDateTime.now();
        
        //Not enough time has passed.
        if(now.isBefore(startTime))
        {
            return;
        }
        
        int currentHour = now.getHour();
        
        for(WorkerAccount worker : database.getAccountProvisioner().getWorkers())
        {
            if(worker.getTask() == Task.TESTER || worker.getTask() == Task.MULE) continue;
    
            ZonedDateTime startTime = worker.getStartTime();
            //Check if the next start time has passed, if not continue;
            if(startTime != null && now.isBefore(startTime) || !worker.getShift().isWithinShift(currentHour))
            {
                continue;
            }
            //Check if we're in shift and not already launched.
            if(getConnectionStatus(worker.getId()) == ConnectionStatus.DISCONNECTED)
            {
                launch(worker);
                return; //We return after each one just to be sure we don't go over the limit.
            }
        }
    }
    
    public static synchronized int activeConnections()
    {
        return getSessions().size();
    }
    
    private static synchronized Map<Integer, Session> getSessions()
    {
        return sessions;
    }
    
    public boolean shutdownGently()
    {
        //log.info("Shutting down all sessions. " +  activeConnections() + " remaining.");
        for(Map.Entry<Integer, Session> entry :
                getSessions().entrySet())
        {
            if(!entry.getValue().isShuttingDown() && !muler.hasOpenJob(entry.getValue().getAccountID()))
            {
                entry.getValue().shutdown();
            }
        }
        
        if(sessions.size() > 0) return false;
    
    
        return closeAllClients();
    }
    
    public void shutdownForce()
    {
        closeAllClients();
    }
    
    public synchronized boolean closeAllClients()
    {
        String clientName = "client.jar";
        Set<Integer> clientIDs = new HashSet<>();
        String process;
        Process p = null;
        try
        {
            p = Runtime.getRuntime().exec("jps");
        } catch(IOException e)
        {
            log.error("Failed to execute 'jps' command", e);
            throw new RuntimeException(e);
        }
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try
        {
            while((process = input.readLine()) != null)
            {
                if(process.endsWith(clientName))
                {
                    clientIDs.add(Integer.parseInt(process.split(" ")[0]));
                }
            }
        } catch(IOException e)
        {
            log.error("Failed to read the process stream", e);
            throw new RuntimeException(e);
        }
        if(clientIDs.size() == 0)
        {
            return true;
        }
        
        try
        {
            log.info("Attempting to force close " + clientIDs.size() + " dreambot clients...");
            for(int clientID : clientIDs)
            {
                Runtime.getRuntime().exec("taskkill /F /IM " + clientID);
            }
            input.close();
        } catch(IOException e)
        {
            log.error("Failed to execute 'taskkill' command on " + clientIDs, e);
            throw new RuntimeException(e);
        }
        log.info("Closed " + clientIDs.size() + " clients");
        return true;
    }
    
    public void addSession(Session session)
    {
        unpairedSessions.add(session);
    }
    
    public synchronized Process pairSession(int accountID, Session session)
    {
        log.info("Pairing account(" + accountID + ") session with process information.");
        Process process = null;
        if(unpairedSessions.contains(session))
        {
            log.info("Account(" + accountID + ") paired with session.");
            unpairedSessions.remove(session);
            sessions.put(accountID, session);
        }
        else log.error("Account(" + accountID + ") could not be found in unpaired sessions.");
        if(unpairedProcesses.containsKey(accountID))
        {
            process = unpairedProcesses.get(accountID);
            log.info("Account(" + accountID + ") paired with process.");
            unpairedProcesses.remove(accountID);
        }
        else log.error("Process(" + accountID + ") could not be found in unpaired processes.");
        
        
        return process;
    }
    
    public Database getDatabase()
    {
        return database;
    }
    
    public ClientUpdater getClientUpdater()
    {
        return updater;
    }
    

    public Muler getMuler()
    {
        return muler;
    }


    
    public synchronized void launch(Account account)
    {
        if(account == null)
        {
            log.error("Worker must not be null.");
        }
        else
        {
            Process process = ClientBuilder.launch(account);
            unpairedProcesses.put(account.getId(), process);
        }
    }
    
    public synchronized void launch(Account account, int world)
    {
        if(account == null)
        {
            log.error("Worker must not be null.");
        }
        else
        {
            Process process = ClientBuilder.launch(account, world);
            unpairedProcesses.put(account.getId(), process);
        }
    }
    //--------------------Static Functions------------------------//
    public static synchronized void stopAccount(int accountID)
    {
        Optional<Session> session = getSession(accountID);
        if(session.isPresent() && session.get().isConnected())
        {
            session.get().shutdown();
        }
    }

    public static synchronized Optional<Session> getSession(int accountID)
    {
        return Optional.ofNullable(getSessions().get(accountID));
    }
    
    public static synchronized boolean isConnected(int accountID)
    {
        Optional<Session> session = getSession(accountID);
        return session.isPresent() && session.get().isConnected();
    }
    
    public static synchronized boolean isLaunching(int accountID)
    {
        return unpairedProcesses.containsKey(accountID);
    }
    
    public static synchronized void setAutoLaunchClients(boolean autoLaunch)
    {
        autoLaunchClients.set(autoLaunch);
    }
    
    public static synchronized boolean getAutoLaunchClients()
    {
        return autoLaunchClients.get();
    }
    
    public synchronized ConnectionStatus getConnectionStatus(int accountID)
    {
        if(isConnected(accountID)) return ConnectionStatus.CONNECTED;
        if(isLaunching(accountID)) return ConnectionStatus.CONNECTING;
        return ConnectionStatus.DISCONNECTED;
    }
    
    private void loadSettings()
    {
        Gson gson = new Gson();
        List<BreakObject> breaks = new ArrayList<>();
        for(Break breakI : Break.values())
        {
            breaks.add(breakI.getBreakObject());
        }
        JsonArray json = JsonParser.parseString(gson.toJson(breaks)).getAsJsonArray();
        JsonObject settings = new JsonObject();
        settings.add("breaks", json);
        settings.addProperty("developerMode", true);
        settings.addProperty("cpuSaver", true);
        settings.addProperty("drawScriptPaint", false);
        settings.addProperty("dismissSolversActive", false);
        settings.addProperty("covertMode", true);
        
        FileUtils.deleteQuietly(new File(Config.DREAMBOT_DIRECTORY + "\\BotData\\settings.json"));
        try
        {
            Files.writeString(Path.of(Config.DREAMBOT_DIRECTORY + "\\BotData\\settings.json"), JsonTools.toString(settings), StandardCharsets.UTF_8);
            log.info("Dreambot settings loaded.");
        } catch(IOException e)
        {
            log.error("Failed to save settings file", e);
            throw new RuntimeException(e);
        }
    }
}
