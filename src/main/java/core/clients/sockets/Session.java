package core.clients.sockets;

import communication.sockets.AbstractClientSocket;
import communication.sockets.properties.JsonProperty;
import communication.sockets.properties.Property;
import config.SharedConfig;
import config.TimeConstants;
import core.clients.ClientManager;
import core.clients.Schedule;
import core.clients.updater.ClientUpdater;
import core.database.Database;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import core.database.tables.reports.TrekkingReport;
import core.database.tables.reports.WorkerReport;
import core.messaging.Discord;
import core.utilities.Utils;
import data.TrekkingData;
import lombok.extern.log4j.Log4j2;
import types.errors.Error;
import types.errors.Flag;
import types.game.Item;
import types.game.RsLoginResponse;
import types.muling.MuleRequest;
import types.muling.MuleTarget;

import java.net.Socket;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class Session extends AbstractClientSocket
{
    
    private Process process;
    
    private boolean isMule;
    private int accountID = -1;
    
    
    private volatile boolean shutdown = false;
    private volatile boolean endOfShift = false;
    private boolean sentShutdown = false;
    
    private ZonedDateTime endTime;
    private ZonedDateTime nextStartTime;
    
    private volatile int world = 0;
    public int profit = 0;
    public int expenses = 0;
    
    ClientManager clientManager;
    Database database;
    
    public Session(Socket socket, ClientManager clientManager)
    {
        super(socket, "", SharedConfig.SERVER_UPDATE_INTERVAL * 10);
        this.clientManager = clientManager;
        this.database = clientManager.getDatabase();
    }
    
    @Override
    protected void tick()
    {
        if(!isConnected() || !isUsernameSet()){ return; }
        
        if(accountID == -1)
        {
            Optional<Account> account = clientManager.getDatabase().getAccountProvisioner().getAccount(getUsername());
            if(account.isPresent())
            {
                accountID = account.get().getId();
                process = clientManager.pairSession(accountID, this);
                setRuntime(account.get());
            } else
            {
                log.error("Couldn't find " + getUsername() + " in database.");
            }
        }
        if(ZonedDateTime.now().isAfter(endTime))
        {
            endOfShift = true;
            shutdown = true;
        }
        
        if(shutdown && !sentShutdown)
        {
            send(JsonProperty.of(Property.CLIENT, (endOfShift ? "endofshift" : "shutdown")));

            sentShutdown = true;
        }
    }

    private void setRuntime(Account account)
    {
        isMule = account instanceof MuleAccount;
        
        if(!isMule)
        {
            WorkerAccount worker = (WorkerAccount) account;
            Schedule todaysSchedule = new Schedule(worker.getTask().timingPolicy, worker.getShift(), worker.getTask());
            long totalRuntime = database.getReports().getRuntimeBefore(worker.getId(), todaysSchedule.getWorkTimeframe().getStart());
            long todaysRuntime = database.getReports().getRuntimeAfter(worker.getId(), todaysSchedule.getWorkTimeframe().getStart());
    
    
            //We take the max of total runtime and 2 hours(7200 seconds * 1000 ms/sec) so that we always run for 2 hours minimum
            long maxCalcMillis = (long) Math.max(totalRuntime * 1.3, TimeConstants.MILLISECONDS_IN_HOUR * 2);
            //Then get the max amount of time we can still run for the day, minimum 0;
            long maxSessionMillis = Math.max(0, maxCalcMillis - todaysRuntime);
            print("Session hours remaining: " + (maxSessionMillis / TimeConstants.MILLISECONDS_IN_HOUR) + ", Total lifetime hours worked: " + ((totalRuntime + todaysRuntime) / TimeConstants.MILLISECONDS_IN_HOUR) + ", Today's hours worked: " + (todaysRuntime / TimeConstants.MILLISECONDS_IN_HOUR));
            ZonedDateTime timeLeftTime = ZonedDateTime.now().plusSeconds(maxSessionMillis / TimeConstants.MILLISECONDS_IN_SECOND);
            
            if(timeLeftTime.isBefore(todaysSchedule.getOffsetTimeframe().getEnd()))
            {
                this.endTime = timeLeftTime;
            }
            else
            {
                this.endTime = todaysSchedule.getOffsetTimeframe().getEnd();
            }
            
            nextStartTime = todaysSchedule.getOffsetTimeframe().getStart().plusDays(1);
        }
        else
        {
            //This is to make sure that any mule automatically shuts down if something goes wrong.
            endTime = ZonedDateTime.now().plusMinutes(30);
            nextStartTime = ZonedDateTime.now();
        }
        print((isMule ? "Mule" : "Worker") + " session scheduled end time: " + Utils.getPrettyTime(endTime) + ". Next session start time: " + Utils.getPrettyTime(nextStartTime));
        
    }
    
    private void shutdownForDay(boolean callMule)
    {
        if(callMule)
        {
            shutdown = true;
            endOfShift = true;
        }
        else
        {
            database.getAccountProvisioner().setStartTime(getAccountID(), nextStartTime);
            shutdown = true;
        }
    }
    
    public int getWorld()
    {
        return world;
    }
    
    
    public int getAccountID()
    {
        return accountID;
    }
    
    public boolean isUsernameSet()
    {
        return getUsername() != null && !getUsername().isEmpty();
    }

    public void callForMule()
    {
        clientManager.getMuler().registerJob(getAccountID(), isMule, null, null);
    }

    public synchronized void shutdown()
    {
        if(!shutdown)
        {
            print("Shutting down" + (endOfShift ? " at shifts end." : "."));
        }
        shutdown = true;
    }
    
    public synchronized boolean isShuttingDown()
    {
        return shutdown;
    }
    
    @Override
    protected void disconnect()
    {
        //If the report is longer than 5 minutes and task isn't a mule
        if(!isMule && getRunTimeMillis() > 5 * TimeConstants.MILLISECONDS_IN_MINUTE)
        {
            WorkerReport report = new WorkerReport(startTime, database.getAccountProvisioner().getWorker(accountID).get(), profit, expenses, ZonedDateTime.now());
            log.info("Adding session report to database " + report);
            database.getReports().save(report);
        }
        if(process != null && process.isAlive())
        {
            if(isMule || ClientManager.getAutoLaunchClients())
            {
                process.destroy();
                print("Process destroyed.");
            }
            else print("Process spared.");
        }
        if(endOfShift && !isMule && getAccountID() != -1) //End of shift should be enough to ensure no conflicts with a banned start time.
        {
            database.getAccountProvisioner().setStartTime(getAccountID(), nextStartTime);
            print("Tomorrows start time set: " + nextStartTime);
        }
    }
    
    public boolean validate()
    {
        if(hasCriticallyFailed())
        {
            log.info("Critical Failure(" + getAccountID() + "): " + getException());
            stop();
        }
        //the process is good and it's still connected.
        if(process == null)
        {
            if(ZonedDateTime.now().isAfter(startTime.plusMinutes(5)))
            {
                log.info("Session was never connected with a client. Shutting down.");
                stop();
                return false;
            }
        }
        else if(!process.isAlive())
        {
            log.info("Session client has expired. Shutting down.");
            stop();
            return false;
        }
        if(isConnected())
        {
            return true;
        }
        else
        {
            log.info("Session is no longer connected. Shutting down.");
            stop();
            return false;
        }
    }

    @Override
    protected void print(String stringToPrint)
    {
        log.info("[" + getUsername() + "] " + stringToPrint);
    }
    
    /*********************************** Responses / Queries ********************************************/
    
    @Override
    public String displayNameQuery()
    {
        return "Server";
    }
    
    @Override
    public void displayNameResponse(String response)
    {
        setUsername(response);
    }
    
    @Override
    public String clientQuery()
    {
        return "";
    }

    @Override
    public void clientResponse(String response)
    {
        if(response.equals("outdated"))
        {
            ClientUpdater.setOutOfDate();
        }
        else if(response.equals("initialized"))
        {
            database.getAccountProvisioner().removeNote(getAccountID(), Flag.UNINITIALIZED);
        }
        else if(response.equals("doneforday"))
        {
            shutdownForDay(false);
        }
    }

    @Override
    public int worldQuery()
    {
        return -1;
    }
    
    @Override
    public void worldResponse(int response)
    {
        world = response;
    }
    
    @Override
    public int gameTimeQuery()
    {
        return 0; //The session will never ask me this.
    }
    
    @Override
    public void gameTimeResponse(int response)
    {
        if(accountID != -1) database.getAccountProvisioner().setGameTime(getAccountID(), response);
    }
    
    @Override
    public boolean availabilityQuery()
    {
        return true;
    }
    
    @Override
    public void availabilityResponse(boolean response)
    {
        //If the bot agrees to be muled, then immediately send an imports request.
        if(response)
        {
            print("is available for whatever ;)");
        }
        else{
            print("is not available for whatever ;)");
        }
    }

    /*
     * The Standard way the bot should make a mule request.
     */
    @Override
    public void muleRequestResponse(MuleRequest response)
    {
        clientManager.getMuler().registerJob(getAccountID(), isMule, response.getImports(), response.getExports());
    }
    
    @Override
    public void muleTargetResponse(MuleTarget response)
    {
        //The session should never be receiving this.
    }

    @Override
    public void muleFinishedResponse(List<Item> response)
    {
        //having this call the muler would be a good way to ensure theres no deadlocks with the mules and worker.
        //If the bots say they're finished before the MuleJob gets a chance to send the completion query then it would deadlock.
        clientManager.getMuler().jobComplete(getAccountID(), response);
    }
    
    @Override
    public void muleFailedResponse(String response)
    {
        clientManager.getMuler().jobFailed(getAccountID());
    }
    
    @Override
    public int profitQuery()
    {
        return 0;
    }
    
    @Override
    public void profitResponse(int response)
    {
        profit = response;
    }
    
    @Override
    public int expenseQuery()
    {
        return 0;
    }
    
    @Override
    public void expenseResponse(int response)
    {
        expenses = response;
    }
    
    @Override
    public void loginResponse(RsLoginResponse response)
    {
        print("[Login Response]" + response);
        
        switch(response)
        {
            case ADDRESS_BLOCKED:
                Discord.post("Proxy", "Proxy is IP Banned: " + database.getAccountProvisioner().getAccount(getAccountID()).get().getProxy());
                break;

            case ACCOUNT_LOCKED:
            case DISABLED:
                database.getAccountProvisioner().banned(getAccountID());
                stop();
                break;
            case MEMBERS_WORLD:
            case MEMBERS_AREA:
                database.getAccountProvisioner().setGameTime(getAccountID(), 0);
                stop();
                break;
                
                //set F2P
            case SET_DISPLAY_NAME:
                database.getAccountProvisioner().addNote(getAccountID(), Flag.DISPLAY_NAME, "Set display name");
                break;
                
            case BAD_AUTH_CODE:
            case INVALID_LOGIN:
            case INVALID_LOGIN_SERVER:
                database.getAccountProvisioner().addNote(getAccountID(), Flag.BAD_CREDENTIALS, "Invalid Login");
                break;
            //Still logged in
            case LOGIN_LIMIT_EXCEEDED:
            case ALREADY_LOGGED_IN:
            case STILL_LOGGED_IN:
            case NOT_LOGGED_OUT:
            case SERVICE_UNAVAILABLE:
            case NO_REPLY:
            case NO_RESPONSE:
            case INACCESSIBLE:
            case CONNECTION_TIMED_OUT:
            case BAD_SESSION:
            case UNEXPECTED_LOGIN_RESPONSE:
            case UNEXPECTED_SERVER_RESPONSE:
            case ERROR_CONNECTING:
            case ERROR_LOADING_PROFILE:
            case UNABLE_TO_CONNECT:
            case FAILED_TO_COMPLETE_LOGIN:
            case UNSUCCESSFUL_LOGIN:
            case MALFORMED_PACKET:
            case TOO_MANY_ATTEMPTS:
            case UPDATED:
            case SERVER_UPDATED:
            //Bad World
            case FULL_WORLD:
            case NOT_ELIGIBLE:
            case CLOSED_BETA:
                //we just want to try logging in again
                log.info("Login response: " + response + " Stopping");
                stop();
                break;

        }
        
    }
    
    @Override
    public void errorResponse(Error error)
    {
    }
    
    @Override
    public void trekkDataResponse(TrekkingData response)
    {
        Optional<Account> account = database.getAccountProvisioner().getAccount(accountID);
        if(account.isPresent() && account.get() instanceof WorkerAccount)
        {
            database.getReports().save(new TrekkingReport((WorkerAccount) account.get(), response));
        }
        else
        {
            log.info("Failed to set trekking data.");
        }
    }
    
    @Override
    public int cashQuery()
    {
        //The bot will never ask us this.
        return 0;
    }
    
    @Override
    public void cashResponse(int response)
    {
        database.getAccountProvisioner().setCash(getAccountID(), response);
    }
    
    @Override
    public List<Item> importsQuery()
    {
        return new ArrayList<>(); //The bot will never ask for items.
    }
    
    @Override
    public void importsResponse(List<Item> response)
    {
    }
    
    @Override
    public List<Item> exportsQuery()
    {
        return new ArrayList<>(); //The bot will never ask for items.
    }
    
    @Override
    public void exportsResponse(List<Item> response)
    {
    }
    

}
