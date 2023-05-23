package core.clients.mule.job;

import core.clients.ClientManager;
import core.clients.ConnectionStatus;
import core.clients.sockets.Session;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.MuleAccount;
import core.exceptions.MuleFailedException;
import lombok.extern.log4j.Log4j2;
import types.game.Item;

import java.util.List;

@Log4j2
public class MuleToMuleJob extends MuleJob
{
    MuleAccount primaryMule;
    
    public MuleToMuleJob(ClientManager clientManager, MuleAccount primaryMule, List<Item> imports, List<Item> exports)
    {
        super(clientManager, imports, exports);
        this.primaryMule = primaryMule;
    }
    
    @Override
    public Session getPrimarySession() throws MuleFailedException
    {
        ConnectionStatus status = clientManager.getConnectionStatus(getPrimaryAccount().getId());
        //If the bot isn't currently running
        if(status != ConnectionStatus.CONNECTED)
        {
            //Check if the client is open and if it's not, launch a new client.
            if(status != ConnectionStatus.CONNECTING)
            {
                clientManager.launch(primaryMule);
                log.info("Launching Primary Mule: " + getPrimaryAccount().getDisplayName());
            }
            return null;
        }
        return ClientManager.getSession(getPrimaryAccount().getId())
                .orElseThrow(() -> new MuleFailedException("Mule session couldn't be found."));
    }

    @Override
    public Account getPrimaryAccount()
    {
        return primaryMule;
    }
}
