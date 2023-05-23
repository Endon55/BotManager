package core.clients.mule.job;

import core.clients.ClientManager;
import core.clients.sockets.Session;
import core.database.tables.accounts.Account;
import core.database.tables.accounts.WorkerAccount;
import core.exceptions.MuleFailedException;
import types.game.Item;

import java.util.List;

public class WorkerToMuleJob extends MuleJob
{
    WorkerAccount worker;
    public WorkerToMuleJob(ClientManager clientManager, WorkerAccount worker, List<Item> imports, List<Item> exports)
    {
        super(clientManager, imports, exports);
        this.worker = worker;
    }
    
    @Override
    public Session getPrimarySession() throws MuleFailedException
    {
        return ClientManager.getSession(getPrimaryAccount().getId())
                .orElseThrow(() -> new MuleFailedException("Worker session couldn't be found."));
    }
    
    @Override
    public Account getPrimaryAccount()
    {
        return worker;
    }
}
