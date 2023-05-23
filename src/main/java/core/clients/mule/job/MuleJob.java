package core.clients.mule.job;

import communication.sockets.properties.JsonProperty;
import communication.sockets.properties.Property;
import communication.sockets.properties.Query;
import core.clients.ClientManager;
import core.clients.ConnectionStatus;
import core.clients.sockets.Session;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.MuleAccount;
import core.exceptions.MuleFailedException;
import lombok.extern.log4j.Log4j2;
import tools.JsonTools;
import types.game.Item;
import types.muling.MuleTarget;

import java.time.ZonedDateTime;
import java.util.List;

@Log4j2
public abstract class MuleJob
{
    private final ZonedDateTime startTime;
    private long endTimeMS = -1;
    
    private MuleAccount muleAccount;
    private Session primarySession;
    private Session muleSession;
    
    private List<Item> imports;
    private List<Item> exports;
    
    private Query primaryImportsQuery;
    private Query primaryExportsQuery;
    
    MuleTarget primaryTarget = null;
    MuleTarget muleTarget = null;
    
    private boolean sentPrimaryTarget = false;
    private boolean sentMuleTarget = false;
    
    private List<Item> primaryTradedItems;
    private List<Item> muleTradedItems;
    
    private volatile boolean failed = false;
    
    protected final ClientManager clientManager;
    
    public MuleJob(ClientManager clientManager, List<Item> imports, List<Item> exports)
    {
        this.clientManager = clientManager;
        this.imports = imports;
        this.exports = exports;
        startTime = ZonedDateTime.now();
    }
    
    public boolean mule() throws MuleFailedException
    {
        if(primarySession == null)
        {
            primarySession = getPrimarySession();
            return false;
        }
        if(!primarySession.isConnected())
        {
            primarySession = null;
            throw new MuleFailedException("Primary account disconnected.");
        }
        
        if(!fetchImports() || !fetchExports())
        {
            return false;
        }
        
        if(!validateImportExports())
        {
        }
        
        if(needsMule())
        {
            return false;
        }
        
        if(!isPrimaryReady() || !isMuleReady())
        {
            return false;
        }
    
        if(muleSession == null)
        {
            muleSession = ClientManager.getSession(getMuleAccount().getId())
                    .orElseThrow(() -> new MuleFailedException("Mule session couldn't be found."));
        }
    
        if(!sendMuleTarget())
        {
            return false;
        }
        
        return tradeComplete();
    }
    
    private boolean fetchImports()
    {
        if(imports == null)
        {
            if(primaryImportsQuery == null)
            {
                primaryImportsQuery = Property.IMPORTS.getQuery();
                primarySession.send(primaryImportsQuery);
                return false;
            }
            else if(primaryImportsQuery.isAnswered())
            {
                imports = JsonTools.jsonToList(primaryImportsQuery.response.getAsJsonArray(), Item.class);
                log.info("Primary Imports fetched.");
            }
            else
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean fetchExports()
    {
        if(exports == null)
        {
            if(primaryExportsQuery == null)
            {
                primaryExportsQuery = Property.EXPORTS.getQuery();
                primarySession.send(primaryExportsQuery);
                return false;
            }
            else if(primaryExportsQuery.isAnswered())
            {
                exports = JsonTools.jsonToList(primaryExportsQuery.response.getAsJsonArray(), Item.class);
                log.info("Primary Exports fetched.");
            }
            else
            {
                return false;
            }
        }
        return true;
    }
    
    private boolean validateImportExports()
    {
        return !(imports.isEmpty() && exports.isEmpty());
    }
    
    public boolean needsMule()
    {
        return muleAccount == null;
    }
    
    public MuleAccount getMuleAccount()
    {
        return muleAccount;
    }

    public boolean isBusy(int accountID)
    {
        return getPrimaryAccount().getId() == accountID || (getMuleAccount() != null && getMuleAccount().getId() == accountID);
    }
    
    
    public void setMule(MuleAccount muleAccount)
    {
        if(muleAccount != null)
        {
            this.muleAccount = muleAccount;
            log.info("Mule set. " + muleAccount);
        }
    }
    
    private boolean isPrimaryReady() throws MuleFailedException
    {
        if(!ClientManager.isConnected(getPrimaryAccount().getId()))
        {
            //log.warn("Mule job between, (" + getPrimaryAccount() + "), and mule(" + getMuleAccount().getId() + ") cannot continue, primary is disconnected.");
            throw new MuleFailedException("Mule job between, (" + getPrimaryAccount() + "), and mule(" + getMuleAccount().getId() + ") cannot continue, primary is disconnected.");
        }
        return true;
    }
    
    private boolean isMuleReady()
    {
        if(needsMule()) return false;
    
        ConnectionStatus status = clientManager.getConnectionStatus(getMuleAccount().getId());
        //If the bot isn't currently running
        if(status != ConnectionStatus.CONNECTED)
        {
            //Check if the client is open and if it's not, launch a new client.
            if(status != ConnectionStatus.CONNECTING)
            {
                muleSession = null;
                sentMuleTarget = false;
                clientManager.launch(getMuleAccount(), primarySession.getWorld());
                log.info("Launching Mule: " + getMuleAccount().getDisplayName());
            }
            return false;
        }
        return true;
    }
    
    private void generateTargets()
    {
        int world = primarySession.getWorld();
        String primaryName = primarySession.getUsername();
        String muleName = muleSession.getUsername();
        MuleLocation muleLocation = MuleLocationGenerator.getMuleLocation();
        muleTarget = new MuleTarget(primaryName, world, muleLocation.getPrimaryTile(), imports);
        primaryTarget = new MuleTarget(muleName, world, muleLocation.getMuleTile(), exports);
    }
    
    private boolean sendMuleTarget()
    {
        if(!sentPrimaryTarget && !sentMuleTarget)
        {
            //On the off chance that somehow both the primary and mule lose their targets.
            generateTargets();
            primarySession.send(JsonProperty.of(Property.MULE_TARGET, JsonTools.serialize(primaryTarget)));
            muleSession.send(JsonProperty.of(Property.MULE_TARGET, JsonTools.serialize(muleTarget)));
            log.info("Mule information sent to primary: " + primaryTarget.displayName + ", and mule: " + muleTarget.displayName);
            sentPrimaryTarget = true;
            sentMuleTarget = true;
        }
        else if(!sentPrimaryTarget)
        {
            primarySession.send(JsonProperty.of(Property.MULE_TARGET, JsonTools.serialize(primaryTarget)));
            log.info("Mule information sent to primary: " + primaryTarget.displayName);
            sentPrimaryTarget = true;
        }
        else if(!sentMuleTarget)
        {
            muleSession.send(JsonProperty.of(Property.MULE_TARGET, JsonTools.serialize(muleTarget)));
            log.info("Mule information sent to mule: " + muleTarget.displayName);
            sentMuleTarget = true;
        }

        return true;
    }
    
    
    public synchronized void setCompleteResult(int accountID, List<Item> tradedItems)
    {
        if(getPrimaryAccount().getId() == accountID)
        {
            primaryTradedItems = tradedItems;
        }
        else
        {
            muleTradedItems = tradedItems;
        }
    }
    
    public synchronized boolean isComplete()
    {
        return (primaryTradedItems != null && muleTradedItems != null);
    }
    public synchronized List<Item> getPrimaryTradedItems()
    {
        return primaryTradedItems;
    }
    
    public synchronized List<Item> getMuleTradedItems()
    {
        return muleTradedItems;
    }
    
    private boolean tradeComplete() throws MuleFailedException
    {
        if(isComplete())
        {
            if(endTimeMS == -1) endTimeMS = System.currentTimeMillis();
            
            //If both accounts say they didn't trade anything
            if((getPrimaryTradedItems().isEmpty() && getMuleTradedItems().isEmpty()) || failed)
            {
                throw new MuleFailedException("Bots failed to mule together.");
            }
            
            //log.info("Job between primary(" + getPrimaryAccount() + "), and mule(" + getMuleAccount().getId() + ") complete.");
            return true;
        }
        return false;
    }
   
    public synchronized void failed()
    {
        this.failed = true;
    }
    
    public synchronized ZonedDateTime getStartTime()
    {
        return startTime;
    }
    
    @Override
    public String toString()
    {
        return "MuleJob{" +
                "primary=" + getPrimaryAccount().getDisplayName() +
                ", primaryImports=" +
                (imports != null ? imports : "{None}") +
                ", primaryExports=" +
                (exports != null ? exports : "{None}") +
                '}';
    }
    
    public abstract Session getPrimarySession() throws MuleFailedException;
    
    public abstract Account getPrimaryAccount();
    
}
