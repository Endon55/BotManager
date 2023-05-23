package core.database.access.accounts;

import core.database.access.Dao;
import core.database.tables.accounts.MuleAccount;
import core.database.tables.accounts.WorkerAccount;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.Optional;

@Log4j2
public class WorkerAccounts extends Dao<WorkerAccount, Integer>
{
    public WorkerAccounts(SessionFactory sessionFactory)
    {
        super(sessionFactory, WorkerAccount.class, "Worker");
    }
    
    public synchronized void setMule(int workerID, MuleAccount mule)
    {
        Optional<WorkerAccount> account = get(workerID);
        if(account.isPresent())
        {
            setMule(account.get(), mule);
        }
        else
        {
            log.info("Couldn't find account(" + workerID + ") to set game time.");
        }
    }
    
    
    public synchronized Optional<WorkerAccount> get(String displayName)
    {
        Session session = getSession();
        Optional<WorkerAccount> accountOpt = Optional.of(createQuery(session, "where DISPLAYNAME='" + displayName + "'").getSingleResult());
        session.close();
        return accountOpt;
    }
    
    public synchronized void setMule(WorkerAccount worker, MuleAccount mule)
    {
        worker.setMule(mule);
        update(worker);
    }

}
